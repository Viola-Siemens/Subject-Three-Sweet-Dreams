package com.hexagram202.subject3.common.utils;

import com.hexagram202.subject3.Subject3;
import com.hexagram202.subject3.common.capability.Subject3Capabilities;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class STBedVehiclesChunkHandler {

    static {
        ForgeChunkManager.setForcedChunkLoadingCallback(Subject3.instance, (list, world) -> {
            // NO - OP
        });
    }

    private static final Object2ObjectOpenHashMap<UUID, ForgeChunkManager.Ticket> vehicles = new Object2ObjectOpenHashMap<>();

    public static void clearWhenClientLoggedOut() {
        vehicles.clear();
    }

    private static ForgeChunkManager.Ticket getTicket(WorldServer worldServer, Entity vec){
        if (!vehicles.containsKey(vec.getUniqueID())) {
            ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(Subject3.instance, worldServer, ForgeChunkManager.Type.ENTITY);
            if (ticket != null) {
                ticket.bindEntity(vec);
                vehicles.put(vec.getUniqueID(), ticket);
                return ticket;
            } else return null;
        } else return vehicles.get(vec.getUniqueID());
    }

    public static void updateBedVehicles(WorldServer worldServer) {
        synchronized (vehicles) {
            for (Entity entity : worldServer.getEntities(Entity.class, (entity) -> entity.hasCapability(Subject3Capabilities.BED_VEHICLE, null))) {
                ForgeChunkManager.Ticket ticket = getTicket(worldServer, entity);
                if (ticket != null) {
                    for (ChunkPos pos : ticket.getChunkList()) {
                        ForgeChunkManager.unforceChunk(ticket, pos);
                    }
                    ForgeChunkManager.forceChunk(ticket, new ChunkPos(entity.chunkCoordX, entity.chunkCoordZ));
                } else {
                   Subject3.LOGGER.fatal("Unable to requestTicket for {}#{}#{}", entity.getName(), entity.getUniqueID(), entity.getPosition());
                }
            }

            final HashSet<UUID> toRemove = new HashSet<>();
            vehicles.forEach(
                    (uuid, ticket) -> {
                        if ((ticket.getType() != ForgeChunkManager.Type.ENTITY)
                                || (ticket.getEntity() == null || ticket.getEntity().isDead)
                                || (ticket.world instanceof WorldServer && ((WorldServer)ticket.world).getEntityFromUuid(uuid) == null)) {
                            ForgeChunkManager.releaseTicket(ticket);
                            toRemove.add(uuid);
                        }
                    }
            );
            for (UUID uuid : toRemove) {
                vehicles.remove(uuid);
            }
        }
    }
}
