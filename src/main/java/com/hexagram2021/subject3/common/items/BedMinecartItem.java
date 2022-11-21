package com.hexagram2021.subject3.common.items;

import com.hexagram2021.subject3.common.entities.BedMinecartEntity;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.*;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BedMinecartItem extends MinecartItem {
	private static final IDispenseItemBehavior DISPENSE_BED_MINECART_BEHAVIOR = new DefaultDispenseItemBehavior() {
		private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

		@SuppressWarnings("deprecation")
		@Override @Nonnull
		public ItemStack execute(IBlockSource blockSource, @Nonnull ItemStack itemStack) {
			Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
			World level = blockSource.getLevel();
			double x = blockSource.x() + direction.getStepX() * 1.125D;
			double y = Math.floor(blockSource.y()) + direction.getStepY();
			double z = blockSource.z() + direction.getStepZ() * 1.125D;
			BlockPos facingPos = blockSource.getPos().relative(direction);
			BlockState facing = level.getBlockState(facingPos);
			RailShape facingRailShape = facing.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)facing.getBlock()).getRailDirection(facing, level, facingPos, null) : RailShape.NORTH_SOUTH;
			double addHeight;
			if (facing.is(BlockTags.RAILS)) {
				if (facingRailShape.isAscending()) {
					addHeight = 0.6D;
				} else {
					addHeight = 0.1D;
				}
			} else {
				if (!facing.isAir() || !level.getBlockState(facingPos.below()).is(BlockTags.RAILS)) {
					return this.defaultDispenseItemBehavior.dispense(blockSource, itemStack);
				}

				BlockState below = level.getBlockState(facingPos.below());
				RailShape belowRailShape = below.getBlock() instanceof AbstractRailBlock ? below.getValue(((AbstractRailBlock)below.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
				if (direction != Direction.DOWN && belowRailShape.isAscending()) {
					addHeight = -0.4D;
				} else {
					addHeight = -0.9D;
				}
			}

			AbstractMinecartEntity minecartEntity = BedMinecartEntity.createBedMinecart(level, x, y + addHeight, z, ((BedMinecartItem)itemStack.getItem()).color);
			if (itemStack.hasCustomHoverName()) {
				minecartEntity.setCustomName(itemStack.getHoverName());
			}

			level.addFreshEntity(minecartEntity);
			itemStack.shrink(1);
			return itemStack;
		}

		@Override
		protected void playSound(IBlockSource blockSource) {
			blockSource.getLevel().levelEvent(1000, blockSource.getPos(), 0);
		}
	};

	private final DyeColor color;

	public BedMinecartItem(DyeColor color, Item.Properties props) {
		super(BedMinecartEntity.BED, props);
		this.color = color;
		DispenserBlock.registerBehavior(this, DISPENSE_BED_MINECART_BEHAVIOR);
	}

	@Override @Nonnull
	public ActionResultType useOn(ItemUseContext context) {
		World world = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		BlockState blockstate = world.getBlockState(blockpos);
		if (!blockstate.is(BlockTags.RAILS)) {
			return ActionResultType.FAIL;
		} else {
			ItemStack itemstack = context.getItemInHand();
			if (!world.isClientSide) {
				RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
				double d0 = 0.0D;
				if (railshape.isAscending()) {
					d0 = 0.5D;
				}

				AbstractMinecartEntity minecartEntity = BedMinecartEntity.createBedMinecart(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.0625D + d0, (double)blockpos.getZ() + 0.5D, this.color);
				if (itemstack.hasCustomHoverName()) {
					minecartEntity.setCustomName(itemstack.getHoverName());
				}

				world.addFreshEntity(minecartEntity);
			}

			itemstack.shrink(1);
			return ActionResultType.sidedSuccess(world.isClientSide);
		}
	}
}
