package com.hexagram2021.subject3.common;

import com.hexagram2021.subject3.common.utils.STBedVehicles;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class STSavedData extends WorldSavedData {
	private static STSavedData INSTANCE;
	public static final String SAVED_DATA_NAME = "Subject3-SavedData";

	private final STBedVehicles bedVehicles;

	private static final String BED_VEHICLES_KEY = "BedVehicles";
	private static final String POSITION_KEY = "position";
	private static final String UUID_KEY = "UUID";

	public STSavedData() {
		this(SAVED_DATA_NAME);
	}

	public STSavedData(String name) {
		super(name);
		this.bedVehicles = new STBedVehicles();
	}

	@Override
	public void load(CompoundNBT nbt) {
		if(nbt.contains(BED_VEHICLES_KEY, Constants.NBT.TAG_LIST)) {
			ListNBT allBedVehicles = nbt.getList(BED_VEHICLES_KEY, Constants.NBT.TAG_COMPOUND);

			for(INBT entry: allBedVehicles) {
				CompoundNBT compound = (CompoundNBT)entry;
				this.bedVehicles.bedVehicles.put(
						compound.getUUID(UUID_KEY),
						new ChunkPos(compound.getLong(POSITION_KEY))
				);
			}
		}
	}

	@Override @Nonnull
	public CompoundNBT save(CompoundNBT nbt) {
		ListNBT allBedVehicles = new ListNBT();
		synchronized (this.bedVehicles.bedVehicles) {
			this.bedVehicles.bedVehicles.forEach((uuid, chunkPos) -> {
				CompoundNBT compound = new CompoundNBT();
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
			ServerWorld level = server.overworld();
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

	public static void updateForceChunk(ChunkPos chunkPos, ServerWorld level, boolean force) {
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
