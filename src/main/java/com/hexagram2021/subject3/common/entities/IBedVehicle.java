package com.hexagram2021.subject3.common.entities;

import com.hexagram2021.subject3.common.STEventHandler;
import com.hexagram2021.subject3.common.STSavedData;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;

public interface IBedVehicle {
	static BedBlock getBedBlock(DyeColor color) {
		switch (color) {
			case BLACK:
				return (BedBlock) Blocks.BLACK_BED;
			case BLUE:
				return (BedBlock) Blocks.BLUE_BED;
			case BROWN:
				return (BedBlock) Blocks.BROWN_BED;
			case CYAN:
				return (BedBlock) Blocks.CYAN_BED;
			case GRAY:
				return (BedBlock) Blocks.GRAY_BED;
			case GREEN:
				return (BedBlock) Blocks.GREEN_BED;
			case LIGHT_BLUE:
				return (BedBlock) Blocks.LIGHT_BLUE_BED;
			case LIGHT_GRAY:
				return (BedBlock) Blocks.LIGHT_GRAY_BED;
			case LIME:
				return (BedBlock) Blocks.LIME_BED;
			case MAGENTA:
				return (BedBlock) Blocks.MAGENTA_BED;
			case ORANGE:
				return (BedBlock) Blocks.ORANGE_BED;
			case PINK:
				return (BedBlock) Blocks.PINK_BED;
			case PURPLE:
				return (BedBlock) Blocks.PURPLE_BED;
			case RED:
				return (BedBlock) Blocks.RED_BED;
			case WHITE:
			default:
				return (BedBlock) Blocks.WHITE_BED;
			case YELLOW:
				return (BedBlock) Blocks.YELLOW_BED;
		}
	}

	int passengersCount();

	void removeBedVehicle();

	float getBedVehicleRotY();
	double getBedVehicleOffsetY();
}
