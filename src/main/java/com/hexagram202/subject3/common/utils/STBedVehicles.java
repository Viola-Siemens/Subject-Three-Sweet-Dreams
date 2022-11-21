package com.hexagram202.subject3.common.utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hexagram202.subject3.Subject3;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class STBedVehicles {
//    public static final HashMap<UUID, ForgeChunkManager.Ticket> TICKETS = new HashMap<>();
//
//    public static void onBedVehicleRemoved(WorldServer worldServer, Entity vec) {
//        ForgeChunkManager.Ticket ticket = getTicket(worldServer, vec);
//        ForgeChunkManager.releaseTicket(ticket);
//    }
//
//    public static void onBedVehicleAddToChunk(WorldServer worldServer, ChunkPos chunkPos, Entity vec) {
//        if (isInChunk(chunkPos, vec)) {
//            ForgeChunkManager.Ticket ticket = getTicket(worldServer, vec);
//            ForgeChunkManager.releaseTicket(ticket);
//            ForgeChunkManager.forceChunk(ticket, chunkPos);
//        }
//    }
//
//    public static ForgeChunkManager.Ticket getTicket(WorldServer worldServer, Entity vec){
//        if (!TICKETS.containsKey(vec.getUniqueID())) {
//            ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(Subject3.instance, worldServer, ForgeChunkManager.Type.ENTITY);
//            if (ticket != null) {
//                ticket.bindEntity(vec);
//            }
//        } else return TICKETS.get(vec.getUniqueID());
//    }
//
//    public static boolean isInChunk(ChunkPos chunkPos, Entity entity) {
//        return chunkPos.x == entity.chunkCoordX && chunkPos.z == entity.chunkCoordZ;
//    }

    public final Map<UUID, ChunkPos> bedVehicles = Maps.newHashMap();
    public final Object2IntMap<ChunkPos> loadedChunkTickets = new Object2IntOpenHashMap<>();

    public STBedVehicles() {
        this.loadedChunkTickets.defaultReturnValue(0);
    }

    public void markAllRelatedChunks(ServerLevel level) {
        synchronized (this.bedVehicles) {
            synchronized (this.loadedChunkTickets) {
                this.bedVehicles.forEach(((uuid, chunkPos) -> {
                    if (!isChunkForced(level, chunkPos)) {
                        if(this.loadedChunkTickets.containsKey(chunkPos)) {
                            this.loadedChunkTickets.computeIntIfPresent(chunkPos, (cp, i) -> i + 1);
                        } else {
                            level.getChunkSource().updateChunkForced(chunkPos, true);
                            this.loadedChunkTickets.put(chunkPos, 1);
                        }
                    }
                }));
            }
        }
    }

    public void removeIllegalBedVehicles(WorldServer level) {
        synchronized (this.bedVehicles) {
            synchronized (this.loadedChunkTickets) {
                Set<UUID> toRemoves = Sets.newHashSet();
                this.bedVehicles.forEach(((uuid, chunkPos) -> {
                    if(level.getEntity(uuid) == null) {
                        toRemoves.add(uuid);
                    }
                }));
                toRemoves.forEach(uuid -> {
                    ChunkPos chunkPos = this.bedVehicles.get(uuid);
                    this.updateUnforceChunkLoad(chunkPos, level);
                    this.bedVehicles.remove(uuid);
                });
            }
        }
    }

    @Nullable
    public ChunkPos addVehicleWithoutUpdate(UUID uuid, ChunkPos chunkPos) {
        synchronized (this.bedVehicles) {
            return this.bedVehicles.put(uuid, chunkPos);
        }
    }

    public ChunkPos removeBedVehicleWithoutUpdate(UUID uuid) {
        synchronized (this.bedVehicles) {
            return this.bedVehicles.remove(uuid);
        }
    }

    public void updateForceChunkLoad(ChunkPos chunkPos, WorldServer level) {
        if(!isChunkForced(level, chunkPos)) {
            if(this.loadedChunkTickets.containsKey(chunkPos)) {
                this.loadedChunkTickets.computeIntIfPresent(chunkPos, (cp, i) -> i + 1);
            } else {
                level.getChunkSource().updateChunkForced(chunkPos, true);
                this.loadedChunkTickets.put(chunkPos, 1);
            }
        }
    }

    public void updateUnforceChunkLoad(ChunkPos chunkPos, WorldServer level) {
        if(!isChunkForced(level, chunkPos) && this.loadedChunkTickets.containsKey(chunkPos)) {
            this.loadedChunkTickets.computeIntIfPresent(chunkPos, (cp, i) -> i - 1);
            if(this.loadedChunkTickets.getInt(chunkPos) <= 0) {
                this.loadedChunkTickets.removeInt(chunkPos);
                level.getChunkSource().updateChunkForced(chunkPos, false);
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isChunkForced(WorldServer level, ChunkPos pos) {
        WorldInfo levelData = level.getWorldInfo();
        ChunkPos spawnChunk = new ChunkPos(new BlockPos(levelData.getSpawnX(), 0, levelData.getSpawnZ()));
        Stream<ChunkPos> spawnChunks = ChunkPoss.rangeClosed(spawnChunk, 9);

        for (long values : level.getForcedChunks()) {
            if (pos.equals(new ChunkPos(ChunkPoss.getX(values), ChunkPoss.getZ(values)))) {
                return true;
            }
        }

        return spawnChunks.anyMatch(chunk -> chunk.equals(pos));
    }



}
