package com.hexagram2021.subject3.common.entities;

import com.hexagram2021.subject3.common.STEventHandler;
import com.hexagram2021.subject3.common.STSavedData;
import com.hexagram2021.subject3.register.STBlocks;
import com.hexagram2021.subject3.register.STEntities;
import com.hexagram2021.subject3.register.STItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

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

	@Override @Nonnull
	public BlockState getDefaultDisplayBlockState() {
		return STBlocks.Technical.getMinecartBedBlockState(this.color);
	}

	@Override @Nonnull
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void remove(boolean keepData) {
		ChunkPos chunkPos = STSavedData.removeBedVehicle(this.uuid);
		if(chunkPos != null && this.level instanceof ServerWorld && !STEventHandler.isChunkForced((ServerWorld)this.level, chunkPos)) {
			this.level.getChunkSource().updateChunkForced(chunkPos, false);
		}
		super.remove(keepData);
	}

	@Override
	public int passengersCount() {
		return this.getPassengers().size();
	}

	public static BedMinecartEntity createBedMinecart(World level, double x, double y, double z, DyeColor color) {
		return new BedMinecartEntity(level, x, y, z, color);
	}
}
