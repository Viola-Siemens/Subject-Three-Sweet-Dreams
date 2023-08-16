package com.hexagram2021.subject3.common.utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.LevelData;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class STBedVehicles {
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

	public void removeIllegalBedVehicles(ServerLevel level) {
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

	public void updateForceChunkLoad(ChunkPos chunkPos, ServerLevel level) {
		if(!isChunkForced(level, chunkPos)) {
			if(this.loadedChunkTickets.containsKey(chunkPos)) {
				this.loadedChunkTickets.computeIntIfPresent(chunkPos, (cp, i) -> i + 1);
			} else {
				level.getChunkSource().updateChunkForced(chunkPos, true);
				this.loadedChunkTickets.put(chunkPos, 1);
			}
		}
	}

	public void updateUnforceChunkLoad(ChunkPos chunkPos, ServerLevel level) {
		if(!isChunkForced(level, chunkPos) && this.loadedChunkTickets.containsKey(chunkPos)) {
			this.loadedChunkTickets.computeIntIfPresent(chunkPos, (cp, i) -> i - 1);
			if(this.loadedChunkTickets.getInt(chunkPos) <= 0) {
				this.loadedChunkTickets.removeInt(chunkPos);
				level.getChunkSource().updateChunkForced(chunkPos, false);
			}
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isChunkForced(ServerLevel level, ChunkPos pos) {
		LevelData levelData = level.getLevelData();
		ChunkPos spawnChunk = new ChunkPos(new BlockPos(levelData.getXSpawn(), 0, levelData.getZSpawn()));
		Stream<ChunkPos> spawnChunks = ChunkPos.rangeClosed(spawnChunk, 9);

		for (long values : level.getForcedChunks()) {
			if (pos.equals(new ChunkPos(ChunkPos.getX(values), ChunkPos.getZ(values)))) {
				return true;
			}
		}

		return spawnChunks.anyMatch(chunk -> chunk.equals(pos));
	}
}
