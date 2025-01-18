package com.hexagram202.subject3.common.item;

import com.hexagram202.subject3.common.capability.IBedVehicle;
import com.hexagram202.subject3.common.capability.Subject3Capabilities;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ItemBed extends Item {

    // it is when player click a block, do not use onItemRightClick.
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float posX, float posY, float posZ) {
        // place the bed
        if (!world.isRemote) {
            pos = pos.add(facing.getDirectionVec());
            Entity bed = createBed(world, player, player.getHeldItem(hand));
            bed.setLocationAndAngles(pos.getX(), pos.getZ(), pos.getZ(), player.rotationYaw, player.rotationPitch);
            if (world.spawnEntity(bed)) {
                // On Item Use will add stats when success
                consumeItem(player, player.getHeldItem(hand));
                return EnumActionResult.SUCCESS;
            } else return EnumActionResult.FAIL;
        } else return EnumActionResult.SUCCESS; // For client, we always return success, in order to send action to server.

    }

    public abstract Entity createBed(World world, EntityPlayer player, ItemStack stack);

    public static void consumeItem(EntityPlayer player, ItemStack stack) {
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
    }

    private static boolean bedInRange(EntityPlayer player, BlockPos p_190774_1_) {
        return Math.abs(player.posX - (double) p_190774_1_.getX()) <= (double) 3.0F && Math.abs(player.posY - (double) p_190774_1_.getY()) <= (double) 2.0F && Math.abs(player.posZ - (double) p_190774_1_.getZ()) <= (double) 3.0F;
    }
}
