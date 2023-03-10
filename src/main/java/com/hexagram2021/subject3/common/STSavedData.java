package com.hexagram2021.subject3.common;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public class STSavedData extends WorldSavedData {
	private static STSavedData INSTANCE;
	public static final String SAVED_DATA_NAME = "Subject3-SavedData";

	private final Map<GlobalPos, UUID> bedVehicles = Maps.newHashMap();

	private static final String BED_VEHICLES_KEY = "BedVehicles";
	private static final String POSITION_KEY = "position";
	private static final String LEVEL_KEY = "level";
	private static final String UUID_KEY = "UUID";

	public STSavedData() {
		this(SAVED_DATA_NAME);
	}

	public STSavedData(String name) {
		super(name);
	}

	@Override
	public void load(CompoundNBT nbt) {
		if(nbt.contains(BED_VEHICLES_KEY, Constants.NBT.TAG_LIST)) {
			ListNBT allBedVehicles = nbt.getList(BED_VEHICLES_KEY, Constants.NBT.TAG_COMPOUND);

			for(INBT entry: allBedVehicles) {
				CompoundNBT compound = (CompoundNBT)entry;
				this.bedVehicles.put(
						GlobalPos.of(
								RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString(LEVEL_KEY))),
								BlockPos.of(compound.getLong(POSITION_KEY))
						),
						compound.getUUID(UUID_KEY)
				);
			}
		}
	}

	@Override @Nonnull
	public CompoundNBT save(CompoundNBT nbt) {
		ListNBT allBedVehicles = new ListNBT();
		this.bedVehicles.forEach((globalPos, uuid) ->  {
			CompoundNBT compound = new CompoundNBT();
			compound.putString(LEVEL_KEY, globalPos.dimension().location().toString());
			compound.putLong(POSITION_KEY, globalPos.pos().asLong());
			compound.putUUID(UUID_KEY, uuid);

			allBedVehicles.add(compound);
		});
		nbt.put(BED_VEHICLES_KEY, allBedVehicles);
		return nbt;
	}

	public static void markAllRelatedChunk(MinecraftServer server) {
		INSTANCE.bedVehicles.forEach(((globalPos, uuid) -> {
			ServerWorld level = server.getLevel(globalPos.dimension());
			if(level != null) {
				level.getChunkSource().updateChunkForced(new ChunkPos(globalPos.pos()), true);
			}
		}));
	}

	public static void markInstanceDirty() {
		if(INSTANCE != null) {
			INSTANCE.setDirty();
		}
	}

	public static void addBedVehicle(GlobalPos globalPos, UUID uuid) {
		INSTANCE.bedVehicles.put(globalPos, uuid);
	}

	public static void setInstance(STSavedData in) {
		INSTANCE = in;
	}
}
