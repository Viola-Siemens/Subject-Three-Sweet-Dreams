package com.hexagram2021.subject3.common.entities;

import com.hexagram2021.subject3.register.STEntities;
import com.hexagram2021.subject3.register.STItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BedMinecartEntity extends AbstractMinecartEntity implements IBedVehicle {
	public static Type BED;

	@Nonnull
	private DyeColor color = DyeColor.WHITE;

	public BedMinecartEntity(EntityType<?> type, World level) {
		super(type, level);
		this.forcedLoading = true;
	}

	public BedMinecartEntity(World level, double x, double y, double z, @Nonnull DyeColor color) {
		super(STEntities.BED_MINECART.get(), level, x, y, z);
		this.color = color;
	}

	@Override
	public void destroy(@Nonnull DamageSource damageSource) {
		super.destroy(damageSource);
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			this.spawnAtLocation(IBedVehicle.getBedBlock(this.color));
		}
	}

	@Override @Nonnull
	public Type getMinecartType() {
		return BED;
	}

	@Override
	public ItemStack getCartItem() {
		return new ItemStack(STItems.BedMinecarts.byColor(this.color));
	}

	@Override
	public boolean canBeRidden() {
		return true;
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

	public static BedMinecartEntity createBedMinecart(World level, double x, double y, double z, DyeColor color) {
		return new BedMinecartEntity(level, x, y, z, color);
	}
}
