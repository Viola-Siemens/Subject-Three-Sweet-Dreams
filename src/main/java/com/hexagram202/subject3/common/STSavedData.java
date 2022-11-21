package com.hexagram202.subject3.common;

import com.hexagram202.subject3.common.utils.ChunkPoss;
import com.hexagram202.subject3.common.utils.STBedVehicles;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class STSavedData extends WorldSavedData {

    @Nullable
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
    }

    public static void markAllRelatedChunks(MinecraftServer server) {
        if(INSTANCE != null) {
            WorldServer level = server.getWorld(0);
            INSTANCE.bedVehicles.markAllRelatedChunks(level);
            INSTANCE.bedVehicles.removeIllegalBedVehicles(level);
        }
    }

    public static void removeIllegalBedVehicles(WorldServer level) {
        if(INSTANCE != null) {
            INSTANCE.bedVehicles.removeIllegalBedVehicles(level);
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

    public static void updateForceChunk(ChunkPos chunkPos, WorldServer level, boolean force) {
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

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if(nbt.hasKey(BED_VEHICLES_KEY, Constants.NBT.TAG_LIST)) {
            NBTTagList allBedVehicles = nbt.getTagList(BED_VEHICLES_KEY, Constants.NBT.TAG_COMPOUND);

            for(NBTBase entry: allBedVehicles) {
                NBTTagCompound compound = (NBTTagCompound)entry;
                this.bedVehicles.bedVehicles.put(
                        compound.getUniqueId(UUID_KEY),
                        ChunkPoss.chunkPosFromLog(compound.getLong(POSITION_KEY))
                );
            }
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbtTagCompound) {
        NBTTagList allBedVehicles = new NBTTagList();
        synchronized (this.bedVehicles.bedVehicles) {
            this.bedVehicles.bedVehicles.forEach((uuid, chunkPos) -> {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setUniqueId(UUID_KEY, uuid);
                compound.setLong(POSITION_KEY, ChunkPoss.chunkPosToLong(chunkPos));

                allBedVehicles.appendTag(compound);
            });
        }
        nbt.put(BED_VEHICLES_KEY, allBedVehicles);
        return nbt;
    }
}
