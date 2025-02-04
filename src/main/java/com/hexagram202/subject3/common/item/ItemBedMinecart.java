package com.hexagram202.subject3.common.item;

import com.hexagram202.subject3.common.entities.EntityBedMinecart;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemBedMinecart extends ItemBed{
    public static final int MAX = EnumDyeColor.values().length * EntityBoat.Type.values().length;

    public static EnumDyeColor getData(int damage){
        return EnumDyeColor.byMetadata(damage);
    }

    public static int makeData(EnumDyeColor r){
        return r.getMetadata();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float posX, float posY, float posZ) {
        IBlockState iblockstate = world.getBlockState(pos);
        if (!BlockRailBase.isRailBlock(iblockstate)) {
            return EnumActionResult.FAIL;
        } else {
            ItemStack itemstack = player.getHeldItem(hand);
            if (!world.isRemote) {
                BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate.getBlock() instanceof BlockRailBase ? ((BlockRailBase)iblockstate.getBlock()).getRailDirection(world, pos, iblockstate, null) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                double d0 = 0.0F;
                if (blockrailbase$enumraildirection.isAscending()) {
                    d0 = 0.5F;
                }
                
                Entity entity = createBed(world, player, itemstack);

                entity.setPosition(pos.getX() + 0.5D, pos.getY() + 0.0625D + d0, pos.getZ() + 0.5D);

                if (itemstack.hasDisplayName()) {
                    entity.setCustomNameTag(itemstack.getDisplayName());
                }

                if (world.spawnEntity(entity)) {
                    consumeItem(player, itemstack);
                    return EnumActionResult.SUCCESS;
                } else return EnumActionResult.FAIL;
            }

            return EnumActionResult.SUCCESS;
        }
    }

    @Override
    public Entity createBed(World world, EntityPlayer player, ItemStack stack) {
        return new EntityBedMinecart(world, player, stack);
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getItemStackDisplayName(ItemStack p_77653_1_) {
        if (p_77653_1_.hasDisplayName()) return p_77653_1_.getDisplayName();
        else {
            EnumDyeColor pair = getData(p_77653_1_.getMetadata());
            // Color + Boat + Sleep
            return I18n.translateToLocal(pair.getTranslationKey())
                    + " " + I18n.translateToLocal("item.subject3.bed_minecart.name");
        }
    }

    @Override
    public void getSubItems(CreativeTabs p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
        if (this.isInCreativeTab(p_150895_1_)) {
            for (EnumDyeColor color : EnumDyeColor.values()){
                p_150895_2_.add(new ItemStack(this, 1, makeData(color)));
            }
        }
    }

}
