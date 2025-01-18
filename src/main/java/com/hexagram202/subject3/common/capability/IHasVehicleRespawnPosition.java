package com.hexagram202.subject3.common.capability;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public interface IHasVehicleRespawnPosition{
    @Nullable
    UUID getBedVehicleUUID();

    void setBedVehicleUUID(@Nullable UUID uuid);

    class Storage implements Capability.IStorage<IHasVehicleRespawnPosition> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IHasVehicleRespawnPosition> capability, IHasVehicleRespawnPosition iHasVehicleRespawnPosition, EnumFacing enumFacing) {
            NBTTagCompound compound = new NBTTagCompound();
            if (iHasVehicleRespawnPosition.getBedVehicleUUID() != null) {
                compound.setUniqueId("BedVehicleUUID", iHasVehicleRespawnPosition.getBedVehicleUUID());
            } else compound.setInteger("BedVehicleUUID", 0);
            return compound;
        }

        @Override
        public void readNBT(Capability<IHasVehicleRespawnPosition> capability, IHasVehicleRespawnPosition iHasVehicleRespawnPosition, EnumFacing enumFacing, NBTBase nbtBase) {
            NBTTagCompound compound = (NBTTagCompound) nbtBase;
            if (compound.hasKey("BedVehicleUUID", Constants.NBT.TAG_INT)) {
                iHasVehicleRespawnPosition.setBedVehicleUUID(null);
            } else iHasVehicleRespawnPosition.setBedVehicleUUID(compound.getUniqueId("BedVehicleUUID"));
        }
    }

    class Implementation implements IHasVehicleRespawnPosition
    {
        private UUID uuid = null;

        @Nullable
        @Override
        public UUID getBedVehicleUUID() {
            return uuid;
        }

        @Override
        public void setBedVehicleUUID(@Nullable UUID uuid) {
            this.uuid = uuid;
        }
    }

    class ProviderPlayer implements ICapabilitySerializable<NBTTagCompound>
    {
        private IHasVehicleRespawnPosition position = new Implementation();
        private Capability.IStorage<IHasVehicleRespawnPosition> storage = Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION.getStorage();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            return Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION.equals(capability);
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            if (Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION.equals(capability))
            {
                return Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION.cast(position);
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("HAS_VEHICLE_RESPAWN_POSITION", storage.writeNBT(Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION, position, null));
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound)
        {
            NBTTagCompound compound1 = compound.getCompoundTag("HAS_VEHICLE_RESPAWN_POSITION");
            storage.readNBT(Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION, position, null, compound1);
        }
    }
}
