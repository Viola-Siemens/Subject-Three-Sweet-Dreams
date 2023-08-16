package com.hexagram2021.subject3.common.items;

import com.hexagram2021.subject3.common.entities.BedMinecartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public class BedMinecartItem extends MinecartItem {
	private static final DispenseItemBehavior DISPENSE_BED_MINECART_BEHAVIOR = new DefaultDispenseItemBehavior() {
		private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

		@SuppressWarnings("deprecation")
		@Override
		public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
			Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
			Level level = blockSource.getLevel();
			double x = blockSource.x() + direction.getStepX() * 1.125D;
			double y = Math.floor(blockSource.y()) + direction.getStepY();
			double z = blockSource.z() + direction.getStepZ() * 1.125D;
			BlockPos facingPos = blockSource.getPos().relative(direction);
			BlockState facing = level.getBlockState(facingPos);
			RailShape facingRailShape = facing.getBlock() instanceof BaseRailBlock railBlock ? railBlock.getRailDirection(facing, level, facingPos, null) : RailShape.NORTH_SOUTH;
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
				RailShape belowRailShape = below.getBlock() instanceof BaseRailBlock railBlock ? below.getValue(railBlock.getShapeProperty()) : RailShape.NORTH_SOUTH;
				if (direction != Direction.DOWN && belowRailShape.isAscending()) {
					addHeight = -0.4D;
				} else {
					addHeight = -0.9D;
				}
			}

			BedMinecartEntity minecartEntity = BedMinecartEntity.createBedMinecart(level, x, y + addHeight, z);
			minecartEntity.setColor(((BedMinecartItem)itemStack.getItem()).color);
			if (itemStack.hasCustomHoverName()) {
				minecartEntity.setCustomName(itemStack.getHoverName());
			}

			level.addFreshEntity(minecartEntity);
			itemStack.shrink(1);
			return itemStack;
		}

		@Override
		protected void playSound(BlockSource blockSource) {
			blockSource.getLevel().levelEvent(1000, blockSource.getPos(), 0);
		}
	};

	private final DyeColor color;

	public BedMinecartItem(DyeColor color, Item.Properties props) {
		super(BedMinecartEntity.BED, props);
		this.color = color;
		DispenserBlock.registerBehavior(this, DISPENSE_BED_MINECART_BEHAVIOR);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		BlockState blockstate = world.getBlockState(blockpos);
		if (!blockstate.is(BlockTags.RAILS)) {
			return InteractionResult.FAIL;
		}
		ItemStack itemstack = context.getItemInHand();
		if (!world.isClientSide) {
			RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock railBlock ? railBlock.getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
			double d0 = 0.0D;
			if (railshape.isAscending()) {
				d0 = 0.5D;
			}

			BedMinecartEntity minecartEntity = BedMinecartEntity.createBedMinecart(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.0625D + d0, (double)blockpos.getZ() + 0.5D);
			minecartEntity.setColor(this.color);
			if (itemstack.hasCustomHoverName()) {
				minecartEntity.setCustomName(itemstack.getHoverName());
			}

			world.addFreshEntity(minecartEntity);
		}

		itemstack.shrink(1);
		return InteractionResult.sidedSuccess(world.isClientSide);
	}
}
