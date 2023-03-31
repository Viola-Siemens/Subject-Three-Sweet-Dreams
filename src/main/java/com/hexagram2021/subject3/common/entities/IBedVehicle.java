package com.hexagram2021.subject3.common.entities;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;

public interface IBedVehicle {
	static BedBlock getBedBlock(DyeColor color) {
		return switch (color) {
			case BLACK -> (BedBlock) Blocks.BLACK_BED;
			case BLUE -> (BedBlock) Blocks.BLUE_BED;
			case BROWN -> (BedBlock) Blocks.BROWN_BED;
			case CYAN -> (BedBlock) Blocks.CYAN_BED;
			case GRAY -> (BedBlock) Blocks.GRAY_BED;
			case GREEN -> (BedBlock) Blocks.GREEN_BED;
			case LIGHT_BLUE -> (BedBlock) Blocks.LIGHT_BLUE_BED;
			case LIGHT_GRAY -> (BedBlock) Blocks.LIGHT_GRAY_BED;
			case LIME -> (BedBlock) Blocks.LIME_BED;
			case MAGENTA -> (BedBlock) Blocks.MAGENTA_BED;
			case ORANGE -> (BedBlock) Blocks.ORANGE_BED;
			case PINK -> (BedBlock) Blocks.PINK_BED;
			case PURPLE -> (BedBlock) Blocks.PURPLE_BED;
			case RED -> (BedBlock) Blocks.RED_BED;
			case WHITE -> (BedBlock) Blocks.WHITE_BED;
			case YELLOW -> (BedBlock) Blocks.YELLOW_BED;
		};
	}

	int passengersCount();

	void removeBedVehicle();

	float getBedVehicleRotY();
	double getBedVehicleOffsetY();

	void setColor(DyeColor color);
	@Nonnull
	DyeColor getBedColor();
}
