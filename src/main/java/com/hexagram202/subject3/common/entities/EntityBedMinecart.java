package com.hexagram202.subject3.common.entities;

import com.hexagram202.subject3.Subject3;
import com.hexagram202.subject3.common.capability.IBedVehicle;
import com.hexagram202.subject3.common.capability.Subject3Capabilities;
import com.hexagram202.subject3.common.item.ItemBed;
import com.hexagram202.subject3.common.item.ItemBedMinecart;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityBedMinecart extends EntityMinecartEmpty implements IBedVehicle {
    public static final Type BED = EnumHelper.addEnum(Type.class, "subject3_bed", new Class[]{int.class, String.class}, 12, "MinecartBed"); // magic id ?

    private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntityBedMinecart.class, DataSerializers.VARINT);

    public EntityBedMinecart(World world){
        super(world);
    }

    public EntityBedMinecart(World world, EntityPlayer player, ItemStack stack){
        super(world);
        this.setDataColor(ItemBedMinecart.getData(stack.getMetadata()));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(DATA_COLOR, 0);
    }

    public void setDataColor(EnumDyeColor type) {
        this.dataManager.set(DATA_COLOR, type.getMetadata());
    }

    public EnumDyeColor getDataColor() {
        return EnumDyeColor.byMetadata(this.dataManager.get(DATA_COLOR));
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
    public float getBedVehicleRotY() {
        return -270f;
    }

    @Override
    public void setBedColor(EnumDyeColor color) {
        setDataColor(color);
    }

    @Override
    public EnumDyeColor getBedColor() {
        return getDataColor();
    }

    @Override
    public void killMinecart(DamageSource p_94095_1_) {
        super.killMinecart(p_94095_1_);
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.entityDropItem(getBed(), 0f);
        }
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
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public ItemStack getCartItem() {
        return Subject3.createItem(this);
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
