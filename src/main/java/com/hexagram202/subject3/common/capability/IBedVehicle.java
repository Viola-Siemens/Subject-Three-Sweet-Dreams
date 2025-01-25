package com.hexagram202.subject3.common.capability;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IBedVehicle{

    static ItemStack getBed(EnumDyeColor color) {
        return new ItemStack(Items.BED, 1, color.getMetadata());
    }
    default float getBedVehicleRotY(){
        return 270;
    }

    default void onEntityStartToRide(Entity entity){

    }

    void setBedColor(EnumDyeColor color);
    EnumDyeColor getBedColor();

    class Implementation implements IBedVehicle
    {
        private EnumDyeColor color = EnumDyeColor.WHITE;

        @Override
        public void setBedColor(EnumDyeColor color) {
            this.color = color;
        }

        @Override
        public EnumDyeColor getBedColor() {
            return this.color;
        }
    }

    class Storage implements Capability.IStorage<IBedVehicle> {
        @javax.annotation.Nullable
        @Override
        public NBTBase writeNBT(Capability<IBedVehicle> capability, IBedVehicle bedVehicle, EnumFacing enumFacing) {
            return new NBTTagInt(bedVehicle.getBedColor().getMetadata());
        }

        @Override
        public void readNBT(Capability<IBedVehicle> capability, IBedVehicle bedVehicle, EnumFacing enumFacing, NBTBase nbtBase) {
            bedVehicle.setBedColor(EnumDyeColor.byMetadata(((NBTTagInt)nbtBase).getInt()));
        }
    }

    class ProviderEntity implements ICapabilitySerializable<NBTTagCompound>
    {
        private IBedVehicle instance = new Implementation();
        private Capability.IStorage<IBedVehicle> storage = Subject3Capabilities.BED_VEHICLE.getStorage();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            return Subject3Capabilities.BED_VEHICLE.equals(capability);
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            if (Subject3Capabilities.BED_VEHICLE.equals(capability))
            {
                return Subject3Capabilities.BED_VEHICLE.cast(instance);
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("BED_VEHICLE", storage.writeNBT(Subject3Capabilities.BED_VEHICLE, instance, null));
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound)
        {
            NBTTagCompound compound1 = compound.getCompoundTag("BED_VEHICLE");
            storage.readNBT(Subject3Capabilities.BED_VEHICLE, instance, null, compound1);
        }
    }
}
