package com.hexagram2021.subject3.common;

import com.hexagram2021.subject3.common.utils.STBedVehicles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class STSavedData extends SavedData {
	private static STSavedData INSTANCE;
	public static final String SAVED_DATA_NAME = "Subject3-SavedData";

	private final STBedVehicles bedVehicles;

	private static final String BED_VEHICLES_KEY = "BedVehicles";
	private static final String POSITION_KEY = "position";
	private static final String UUID_KEY = "UUID";

	public STSavedData() {
		super();
		this.bedVehicles = new STBedVehicles();
	}

	public STSavedData(CompoundTag nbt) {
		this();
		if(nbt.contains(BED_VEHICLES_KEY, Tag.TAG_LIST)) {
			ListTag allBedVehicles = nbt.getList(BED_VEHICLES_KEY, Tag.TAG_COMPOUND);

			for(Tag entry: allBedVehicles) {
				CompoundTag compound = (CompoundTag)entry;
				this.bedVehicles.bedVehicles.put(
						compound.getUUID(UUID_KEY),
						new ChunkPos(compound.getLong(POSITION_KEY))
				);
			}
		}
	}

	@Override @Nonnull
	public CompoundTag save(CompoundTag nbt) {
		ListTag allBedVehicles = new ListTag();
		synchronized (this.bedVehicles.bedVehicles) {
			this.bedVehicles.bedVehicles.forEach((uuid, chunkPos) -> {
				CompoundTag compound = new CompoundTag();
				compound.putUUID(UUID_KEY, uuid);
				compound.putLong(POSITION_KEY, chunkPos.toLong());

				allBedVehicles.add(compound);
			});
		}
		nbt.put(BED_VEHICLES_KEY, allBedVehicles);
		return nbt;
	}

	public static void markAllRelatedChunks(MinecraftServer server) {
		if(INSTANCE != null) {
			ServerLevel level = server.overworld();
			INSTANCE.bedVehicles.markAllRelatedChunks(level);
		}
	}

	@Nullable
	public static ChunkPos addBedVehicle(UUID uuid, ChunkPos chunkPos) {
		ChunkPos ret = null;
		if(INSTANCE != null) {
			ret = INSTANCE.bedVehicles.addVehicleWithoutUpdate(uuid, chunkPos);
			INSTANCE.setDirty();
		}
		return ret;
	}

	@Nullable
	public static ChunkPos removeBedVehicle(UUID uuid) {
		ChunkPos ret = null;
		if(INSTANCE != null) {
			ret = INSTANCE.bedVehicles.removeBedVehicleWithoutUpdate(uuid);
			INSTANCE.setDirty();
		}
		return ret;
	}

	public static void updateForceChunk(ChunkPos chunkPos, ServerLevel level, boolean force) {
		if(INSTANCE != null) {
			if (force) {
				INSTANCE.bedVehicles.updateForceChunkLoad(chunkPos, level);
			} else {
				INSTANCE.bedVehicles.updateUnforceChunkLoad(chunkPos, level);
			}
		}
	}

	public static void setInstance(STSavedData in) {
		INSTANCE = in;
	}
}
