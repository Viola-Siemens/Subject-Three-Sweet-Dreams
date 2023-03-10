package com.hexagram2021.subject3.common;

import com.hexagram2021.subject3.common.entities.IBedVehicle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.stream.Stream;

import static com.hexagram2021.subject3.Subject3.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class STEventHandler {
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		PlayerEntity player = event.getPlayer();
	}

	@SubscribeEvent
	public static void onEntityEnterChunk(EntityEvent.EnteringChunk event) {
		if(event.getEntity() instanceof IBedVehicle) {
			World level = event.getEntity().level;
			if(level instanceof ServerWorld) {
				ChunkPos oldPos = new ChunkPos(event.getOldChunkX(), event.getOldChunkZ());
				ChunkPos newPos = new ChunkPos(event.getNewChunkX(), event.getNewChunkZ());

				level.getChunkSource().updateChunkForced(newPos, true);
				if (!isChunkForced((ServerWorld)level, oldPos)) {
					level.getChunkSource().updateChunkForced(oldPos, false);
				}
			}
		}
	}

	public static boolean isChunkForced(ServerWorld level, ChunkPos pos) {
		IWorldInfo levelData = level.getLevelData();
		ChunkPos spawnChunk = new ChunkPos(new BlockPos(levelData.getXSpawn(), 0, levelData.getZSpawn()));
		Stream<ChunkPos> spawnChunks = ChunkPos.rangeClosed(spawnChunk, 11);

		for (long values : level.getForcedChunks()) {
			if (pos.equals(new ChunkPos(ChunkPos.getX(values), ChunkPos.getZ(values)))) {
				return true;
			}
		}

		return spawnChunks.anyMatch(chunk -> chunk.equals(pos));
	}
}
