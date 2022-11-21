package com.hexagram202.subject3.common;

import com.hexagram202.subject3.Subject3;
import com.hexagram202.subject3.common.entities.IBedVehicle;
import com.hexagram202.subject3.common.entities.IHasVehicleRespawnPosition;
import com.hexagram202.subject3.common.utils.Teleport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.UUID;

@Mod.EventBusSubscriber
public class STEventHandler {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        EntityPlayer player = event.player;
        if (player instanceof IHasVehicleRespawnPosition && player.world instanceof WorldServer) {
            IHasVehicleRespawnPosition iHasVehicleRespawnPosition = (IHasVehicleRespawnPosition) player;
            WorldServer worldServer = (WorldServer) player.world;
            UUID bedVehicleUUID = iHasVehicleRespawnPosition.getBedVehicleUUID();
            if (bedVehicleUUID != null) {
                Entity bedVehicle = worldServer.getEntityFromUuid(bedVehicleUUID);
                if (bedVehicle instanceof IBedVehicle) {
                    if ((bedVehicle.getPassengers().isEmpty()) {
                        if (player.world == bedVehicle.world) {
                            player.setPosition(bedVehicle.posX, bedVehicle.posY, bedVehicle.posZ);
                        } else {
                            player.changeDimension(bedVehicle.dimension, new Teleport(bedVehicle));
                        }
                        player.startRiding(bedVehicle);
                        return;
                    }
                } else {
                    ((IHasVehicleRespawnPosition) player).setBedVehicleUUID(null);
                }
                player.sendMessage(new TextComponentTranslation("message.subject3.bed_vehicle_occupied"), Util.NIL_UUID);

            }
        }
    }

    // Copy the data
    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        EntityPlayer player = event.getEntityPlayer()
        EntityPlayer original = event.getOriginal();
        if(player instanceof IHasVehicleRespawnPosition && original instanceof IHasVehicleRespawnPosition) {
            ((IHasVehicleRespawnPosition)player).setBedVehicleUUID(((IHasVehicleRespawnPosition)original).getBedVehicleUUID());
        }
    }

    @SubscribeEvent
    public static void onEntityEnterChunk(EntityEvent.EnteringChunk event) {
        if(event.getEntity() instanceof IBedVehicle && event.getEntity().world instanceof WorldServer) {
            WorldServer worldServer = (WorldServer) event.getEntity().world;
            IBedVehicle bedVehicle = (IBedVehicle) event.getEntity();

            if (worldServer.provider.getDimensionType() == DimensionType.OVERWORLD) {
                ChunkPos newPos = new ChunkPos(event.getNewChunkX(), event.getNewChunkZ());
                ChunkPos oldPos = STSavedData.addBedVehicle(event.getEntity().getUniqueID(), newPos);

                //Subject3.LOGGER.debug(String.format("Bed vehicle moves in overworld: (%d, %d) -> (%d, %d).", event.getOldChunkX(), event.getOldChunkZ(), event.getNewChunkX(), event.getNewChunkZ()));
                if (!newPos.equals(oldPos)) {
                    STSavedData.updateForceChunk(newPos, worldServer, true);
                }
                if (oldPos != null && !oldPos.equals(newPos)) {
                    STSavedData.updateForceChunk(oldPos, worldServer, false);
                }
            } else {
                Subject3.LOGGER.debug("A bed vehicle enter dimension {}:{}", worldServer.provider.getDimensionType().getName(), worldServer.provider.getDimensionType().getId());
                ChunkPos oldPos = STSavedData.removeBedVehicle(event.getEntity().getUniqueID());
                if (oldPos != null) {
                    STSavedData.updateForceChunk(oldPos, worldServer, false);
                }
            }
        }
    }


}
