package com.hexagram2021.subject3.common;

import com.hexagram2021.subject3.Subject3;
import com.hexagram2021.subject3.common.entities.IBedVehicle;
import com.hexagram2021.subject3.common.entities.IHasVehicleRespawnPosition;
import net.minecraft.Util;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;
import java.util.function.Function;

import static com.hexagram2021.subject3.Subject3.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class STEventHandler {
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if(player instanceof IHasVehicleRespawnPosition) {
			UUID bedVehicleUUID = ((IHasVehicleRespawnPosition) player).getBedVehicleUUID();
			if(bedVehicleUUID != null) {
				Entity bedVehicle = ((ServerLevel) player.level).getEntity(bedVehicleUUID);
				if (bedVehicle instanceof IBedVehicle) {
					if (((IBedVehicle) bedVehicle).passengersCount() == 0) {
						if(player.level == bedVehicle.level) {
							player.setPos(bedVehicle.getX(), bedVehicle.getY(), bedVehicle.getZ());
						} else {
							player.changeDimension((ServerLevel) player.level, new ITeleporter() {
								@Override
								public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
									return repositionEntity.apply(false);
								}

								@Override
								public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
									return new PortalInfo(bedVehicle.position(), Vec3.ZERO, entity.getYRot(), entity.getXRot());
								}
							});
						}
						player.startRiding(bedVehicle);
						return;
					}
				} else {
					((IHasVehicleRespawnPosition) player).setBedVehicleUUID(null);
				}
				player.sendMessage(new TranslatableComponent("message.subject3.bed_vehicle_occupied"), Util.NIL_UUID);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		Player player = event.getPlayer();
		Player original = event.getOriginal();
		if(player instanceof IHasVehicleRespawnPosition && original instanceof IHasVehicleRespawnPosition) {
			((IHasVehicleRespawnPosition)player).setBedVehicleUUID(((IHasVehicleRespawnPosition)original).getBedVehicleUUID());
		}
	}

	@SubscribeEvent
	public static void onEntityEnterChunk(EntityEvent.EnteringSection event) {
		if(event.getEntity() instanceof IBedVehicle) {
			Level level = event.getEntity().level;
			if(level instanceof ServerLevel serverlevel) {
				if (level.dimension().equals(ServerLevel.OVERWORLD)) {
					SectionPos sectionPos = event.getNewPos();
					ChunkPos newPos = new ChunkPos(sectionPos.getX(), sectionPos.getZ());

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
