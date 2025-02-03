package com.hexagram202.subject3.common.capability;

import com.hexagram202.subject3.Subject3;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBedVehicle{

    default ItemStack getBed() {
        return Subject3.createItem(this);
    }

    default float getBedVehicleRotY(){
        return 270;
    }

    default void onEntityStartToRide(Entity entity){

    }

    void setBedColor(EnumDyeColor color);
    EnumDyeColor getBedColor();

    /**
     * Pre the render before render. At the specific position,
     *  Used for Posing the driver
     *  Node : have translated to the specific position and push / pop Matrix
     *
     * @param event context
     * @param driver driver entity
     * @param vehicle vehicle
     * @param bedVehicle vehicle
     */
    @SideOnly(Side.CLIENT)
    default void preEntityRender(RenderLivingEvent.Pre<?> event, Entity driver, Entity vehicle, IBedVehicle bedVehicle) {
        GlStateManager.rotate(vehicle.rotationYaw, 0, - 1, 0);
        GlStateManager.rotate(bedVehicle.getBedVehicleRotY(), 0, 1, 0);
        if (driver instanceof EntityPlayer) {
            GlStateManager.translate(driver.height / 2d, ( vehicle.getEyeHeight() + vehicle.getYOffset() + driver.width), 0);
            EntityPlayer player = ((EntityPlayer)event.getEntity());
            player.sleeping = true; // Use the sleeps
            player.updateSize();
            GlStateManager.rotate(player.getBedOrientationInDegrees(), 0, - 1, 0);
        } else {
            if (driver instanceof IMob) {
                GlStateManager.translate(driver.height / 2d, ( vehicle.getEyeHeight() + vehicle.getYOffset() + driver.width), 0);
            } else GlStateManager.translate(driver.height / 2d, ( vehicle.getEyeHeight() + vehicle.getYOffset() + driver.width) / 2d, 0);
            driver.rotationYaw = 0;
            GlStateManager.rotate(90f, 0, 0, 1);
            GlStateManager.rotate(90f, 0, 1, 0);
        }
    }

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
        private final IBedVehicle instance = new Implementation();
        private final Capability.IStorage<IBedVehicle> storage = Subject3Capabilities.BED_VEHICLE.getStorage();

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
