package com.hexagram2021.subject3.common;

import com.hexagram2021.subject3.Subject3;
import com.hexagram2021.subject3.common.entities.IBedVehicle;
import com.hexagram2021.subject3.common.entities.IHasVehicleRespawnPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static com.hexagram2021.subject3.Subject3.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class STEventHandler {
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		PlayerEntity player = event.getPlayer();
		if(player instanceof IHasVehicleRespawnPosition) {
			UUID bedVehicleUUID = ((IHasVehicleRespawnPosition) player).getBedVehicleUUID();
			if(bedVehicleUUID != null) {
				Entity bedVehicle = ((ServerWorld) player.level).getEntity(bedVehicleUUID);
				if (bedVehicle instanceof IBedVehicle) {
					if (((IBedVehicle) bedVehicle).passengersCount() == 0) {
						player.startRiding(bedVehicle);
						return;
					}
				} else {
					((IHasVehicleRespawnPosition) player).setBedVehicleUUID(null);
				}
				player.sendMessage(new TranslationTextComponent("message.subject3.bed_vehicle_occupied"), Util.NIL_UUID);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		PlayerEntity player = event.getPlayer();
		PlayerEntity original = event.getOriginal();
		if(player instanceof IHasVehicleRespawnPosition && original instanceof IHasVehicleRespawnPosition) {
			((IHasVehicleRespawnPosition)player).setBedVehicleUUID(((IHasVehicleRespawnPosition)original).getBedVehicleUUID());
		}
	}

	@SubscribeEvent
	public static void onEntityEnterChunk(EntityEvent.EnteringChunk event) {
		if(event.getEntity() instanceof IBedVehicle) {
			World level = event.getEntity().level;
			if(level instanceof ServerWorld) {
				ServerWorld serverlevel = (ServerWorld) level;
				if (level.dimension().equals(ServerWorld.OVERWORLD)) {
					ChunkPos newPos = new ChunkPos(event.getNewChunkX(), event.getNewChunkZ());

					ChunkPos oldPos = STSavedData.addBedVehicle(event.getEntity().getUUID(), newPos);

					//Subject3.LOGGER.debug(String.format("Bed vehicle moves in overworld: (%d, %d) -> (%d, %d).", event.getOldChunkX(), event.getOldChunkZ(), event.getNewChunkX(), event.getNewChunkZ()));
					if (!newPos.equals(oldPos)) {
						STSavedData.updateForceChunk(newPos, serverlevel, true);
					}
					if (oldPos != null && !oldPos.equals(newPos)) {
						STSavedData.updateForceChunk(oldPos, serverlevel, false);
					}
				} else {
					Subject3.LOGGER.debug("A bed vehicle enter dimension " + level.dimension().location());
					ChunkPos oldPos = STSavedData.removeBedVehicle(event.getEntity().getUUID());
					if (oldPos != null) {
						STSavedData.updateForceChunk(oldPos, serverlevel, false);
					}
				}
			}
		}
	}
}
