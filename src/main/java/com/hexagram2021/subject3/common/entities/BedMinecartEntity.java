package com.hexagram2021.subject3.common.entities;

import com.hexagram2021.subject3.register.STEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BedMinecartEntity extends AbstractMinecartEntity {
	public static Type BED;

	@Nonnull
	private DyeColor color = DyeColor.WHITE;

	public BedMinecartEntity(EntityType<?> type, World level) {
		super(type, level);
	}

	public BedMinecartEntity(World level, double x, double y, double z) {
		super(STEntities.BED_MINECART.get(), level, x, y, z);
	}

	@Override
	public void destroy(@Nonnull DamageSource damageSource) {
		super.destroy(damageSource);
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			this.spawnAtLocation(getBedBlock(this.color));
		}
	}

	@Override @Nonnull
	public Type getMinecartType() {
		return BED;
	}

	@Override
	protected void addAdditionalSaveData(@Nonnull CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putString("DyeColor", this.color.getName());
	}

	@Override
	protected void readAdditionalSaveData(@Nonnull CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		if(nbt.contains("DyeColor")) {
			this.color = DyeColor.byName(nbt.getString("DyeColor"), DyeColor.WHITE);
		}
	}

	private static Block getBedBlock(DyeColor color) {
		switch (color) {
			case BLACK:
				return Blocks.BLACK_BED;
			case BLUE:
				return Blocks.BLUE_BED;
			case BROWN:
				return Blocks.BROWN_BED;
			case CYAN:
				return Blocks.CYAN_BED;
			case GRAY:
				return Blocks.GRAY_BED;
			case GREEN:
				return Blocks.GREEN_BED;
			case LIGHT_BLUE:
				return Blocks.LIGHT_BLUE_BED;
			case LIGHT_GRAY:
				return Blocks.LIGHT_GRAY_BED;
			case LIME:
				return Blocks.LIME_BED;
			case MAGENTA:
				return Blocks.MAGENTA_BED;
			case ORANGE:
				return Blocks.ORANGE_BED;
			case PINK:
				return Blocks.PINK_BED;
			case PURPLE:
				return Blocks.PURPLE_BED;
			case RED:
				return Blocks.RED_BED;
			case YELLOW:
				return Blocks.YELLOW_BED;
			case WHITE:
			default:
				return Blocks.WHITE_BED;
		}
	}
}
