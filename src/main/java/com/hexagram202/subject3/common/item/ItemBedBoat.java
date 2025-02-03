package com.hexagram202.subject3.common.item;

import com.hexagram202.subject3.common.entities.EntityBedBoat;
import com.hexagram202.subject3.common.utils.Pair;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemBedBoat extends ItemBed{
    // enum Color might not be extended.
    public static final int COLOR_COUNT = EnumDyeColor.values().length;

    public static final int MAX = EnumDyeColor.values().length * EntityBoat.Type.values().length;

    public static Pair<EntityBoat.Type, EnumDyeColor> getData(int damage){
        int color = damage % COLOR_COUNT;
        int type = (damage - color) / COLOR_COUNT;
        return Pair.of(EntityBoat.Type.byId(type), EnumDyeColor.byMetadata(color));
    }

    public static int makeData(Pair<EntityBoat.Type, EnumDyeColor> colorPair){
        return colorPair.left().ordinal() * COLOR_COUNT + colorPair.right().ordinal();
    }

    public static int makeData(EntityBoat.Type type, EnumDyeColor colorPair){
        return type.ordinal() * COLOR_COUNT + colorPair.ordinal();
    }

    @Override
    public Entity createBed(World world, EntityPlayer player, ItemStack stack) {
        return new EntityBedBoat(world, player, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float posX, float posY, float posZ) {
        return EnumActionResult.PASS;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        float f = 1.0F;
        float f1 = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch);
        float f2 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw);
        double d0 = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX);
        double d1 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) + (double)playerIn.getEyeHeight();
        double d2 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ);
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * ((float)Math.PI / 180F));
        float f6 = MathHelper.sin(-f1 * ((float)Math.PI / 180F));
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        Vec3d vec3d1 = vec3d.add((double)f7 * (double)5.0F, (double)f6 * (double)5.0F, (double)f8 * (double)5.0F);
        RayTraceResult raytraceresult = worldIn.rayTraceBlocks(vec3d, vec3d1, true);
        if (raytraceresult == null) {
            return new ActionResult<>(EnumActionResult.PASS, itemstack);
        } else {
            Vec3d vec3d2 = playerIn.getLook(1.0F);
            boolean flag = false;
            List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.getEntityBoundingBox().expand(vec3d2.x * (double)5.0F, vec3d2.y * (double)5.0F, vec3d2.z * (double)5.0F).grow((double)1.0F));

            for (Entity entity : list) {
                if (entity.canBeCollidedWith()) {
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow((double) entity.getCollisionBorderSize());
                    if (axisalignedbb.contains(vec3d)) {
                        flag = true;
                    }
                }
            }

            if (flag) {
                return new ActionResult<>(EnumActionResult.PASS, itemstack);
            } else if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
                return new ActionResult<>(EnumActionResult.PASS, itemstack);
            } else {
                Entity entityboat = createBed(worldIn, playerIn, itemstack);
                entityboat.rotationYaw = playerIn.rotationYaw;
                if (!worldIn.getCollisionBoxes(entityboat, entityboat.getEntityBoundingBox().grow(-0.1)).isEmpty()) {
                    return new ActionResult<>(EnumActionResult.FAIL, itemstack);
                } else {
                    if (!worldIn.isRemote) {
                        worldIn.spawnEntity(entityboat);
                    }

                    if (!playerIn.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }

                    playerIn.addStat(StatList.getObjectUseStats(this));
                    return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
                }
            }
        }
    }


    @Override
    @SuppressWarnings("deprecation")
    public String getItemStackDisplayName(ItemStack p_77653_1_) {
        if (p_77653_1_.hasDisplayName()) return p_77653_1_.getDisplayName();
        else {
            Pair<EntityBoat.Type, EnumDyeColor> pair = getData(p_77653_1_.getMetadata());
            // Color + Boat + Sleep
            return I18n.translateToLocal(pair.right().getTranslationKey())
                    + " " + I18n.translateToLocal("item.boat." + pair.left().getName() + ".name")
                    + " " + I18n.translateToLocal("item.subject3.bed_boat.name");
        }
    }

    @Override
    public void getSubItems(CreativeTabs p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
        if (this.isInCreativeTab(p_150895_1_)) {
            for (EnumDyeColor color : EnumDyeColor.values()){
                for (EntityBoat.Type type : EntityBoat.Type.values()) {
                    p_150895_2_.add(new ItemStack(this, 1, makeData(type, color)));
                }
            }
        }
    }
}
