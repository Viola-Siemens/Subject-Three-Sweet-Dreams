package com.hexagram202.subject3.common.entities;

import com.hexagram202.subject3.common.capability.IBedVehicle;
import com.hexagram202.subject3.common.capability.Subject3Capabilities;
import com.hexagram202.subject3.common.item.ItemBed;
import com.hexagram202.subject3.common.item.ItemBedBoat;
import com.hexagram202.subject3.common.utils.Pair;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityBedBoat extends EntityBoat implements IBedVehicle {

    private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntityBedBoat.class, DataSerializers.VARINT);

    public EntityBedBoat(World world) {
        super(world);
    }

    public EntityBedBoat(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityBedBoat(World world, EntityPlayer player, ItemStack stack){
        super(world);
        Pair<Type, EnumDyeColor> data = ItemBedBoat.getData(stack.getMetadata());
        this.setDataColor(data.right());
        this.setBoatType(data.left());
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(DATA_COLOR, 0);
    }

    public Type getDataType() {
        return getBoatType();
    }

    public void setDataColor(EnumDyeColor type) {
        this.dataManager.set(DATA_COLOR, type.getMetadata());
    }

    public EnumDyeColor getDataColor() {
        return EnumDyeColor.byMetadata(this.dataManager.get(DATA_COLOR));
    }

    @Override
    public void setBedColor(EnumDyeColor color) {
        this.setDataColor(color);
    }

    @Override
    public EnumDyeColor getBedColor() {
        return this.getDataColor();
    }

    @Override
    public float getBedVehicleRotY() {
        return 270 ;
    }

    @Override
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        if (super.attackEntityFrom(p_70097_1_, p_70097_2_)) {
            if (this.isDead && this.world.getGameRules().getBoolean("doEntityDrops")) {
                this.entityDropItem(getBed(), 0f);
            }
            return true;
        } else return false;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setInteger("DyeColor", this.dataManager.get(DATA_COLOR));
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
        if (p_70037_1_.hasKey("DyeColor", Constants.NBT.TAG_INT)) {
            this.dataManager.set(DATA_COLOR, p_70037_1_.getInteger("DyeColor"));
        } else this.dataManager.set(DATA_COLOR, 0);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (player.world.isRemote) return true;
        else {
            ItemStack stack = player.getHeldItem(hand);
            if (!stack.isEmpty() && stack.getItem() == Items.DYE) {
                this.setDataColor(EnumDyeColor.byDyeDamage(stack.getItemDamage()));
                ItemBed.consumeItem(player, stack);
                return true;
            } else if (this.world.provider.canSleepAt(player, this.getPosition()) == WorldProvider.WorldSleepResult.BED_EXPLODES) {
                this.world.createExplosion(this, this.posX + 0.5D, this.posY + 0.125D, this.posZ + 0.5D, 5.0F, ForgeEventFactory.getMobGriefingEvent(this.world, this));
                this.onKillCommand();
                return false;
            } else if (super.processInitialInteract(player, hand)){
                if (player.hasCapability(Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION, null)) {
                    player.getCapability(Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION, null)
                            .setBedVehicleUUID(this.getUniqueID());
                }
                //ItemBed.trySleep(player, this);
                this.onEntityStartToRide(player);
                return true;
            } else return false;
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public void updatePassenger(Entity p_184232_1_) {
        super.updatePassenger(p_184232_1_);
        if (p_184232_1_ instanceof EntityLivingBase) {
            EntityLivingBase livingBase = (EntityLivingBase) p_184232_1_;
            livingBase.limbSwing = 0;
            livingBase.limbSwingAmount = 0;

        }
    }

    @Override
    public boolean hasCapability(Capability<?> p_hasCapability_1_, @Nullable EnumFacing p_hasCapability_2_) {
        return Subject3Capabilities.BED_VEHICLE.equals(p_hasCapability_1_) || super.hasCapability(p_hasCapability_1_, p_hasCapability_2_);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> p_getCapability_1_, @Nullable EnumFacing p_getCapability_2_) {
        if (Subject3Capabilities.BED_VEHICLE.equals(p_getCapability_1_)) {
            return Subject3Capabilities.BED_VEHICLE.cast(this); // Because the data sync, We couple the code here
        } else return super.getCapability(p_getCapability_1_, p_getCapability_2_);
    }
}
