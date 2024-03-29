package com.hexagram2021.subject3.common.entities;

import com.hexagram2021.subject3.common.STSavedData;
import com.hexagram2021.subject3.register.STBlocks;
import com.hexagram2021.subject3.register.STEntities;
import com.hexagram2021.subject3.register.STItems;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class BedMinecartEntity extends AbstractMinecartEntity implements IBedVehicle {
	public static Type BED;

	private static final DataParameter<Integer> DATA_ID_DYE_COLOR = EntityDataManager.defineId(BedMinecartEntity.class, DataSerializers.INT);

	public BedMinecartEntity(EntityType<?> type, World level) {
		super(type, level);
		this.forcedLoading = true;
	}

	public BedMinecartEntity(World level, double x, double y, double z) {
		super(STEntities.BED_MINECART.get(), level, x, y, z);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_ID_DYE_COLOR, DyeColor.WHITE.ordinal());
	}

	@Override @Nonnull
	public ActionResultType interact(@Nonnull PlayerEntity player, @Nonnull Hand hand) {
		ActionResultType ret = super.interact(player, hand);
		if (ret.consumesAction()) return ret;
		if (player.isSecondaryUseActive()) {
			return ActionResultType.PASS;
		}
		if (this.isVehicle()) {
			return ActionResultType.PASS;
		}
		if (!this.level.isClientSide) {
			if (!BedBlock.canSetSpawn(this.level)) {
				this.level.explode(this, DamageSource.badRespawnPointExplosion(), null, this.getX() + 0.5D, this.getY() + 0.125D, this.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
				this.removeBedVehicle();
				return ActionResultType.SUCCESS;
			}
			if(player.startRiding(this)) {
				ret = ActionResultType.CONSUME;
				if(player instanceof IHasVehicleRespawnPosition) {
					((IHasVehicleRespawnPosition)player).setBedVehicleUUID(this.uuid);
				}
			} else {
				ret = ActionResultType.PASS;
			}
			return ret;
		}
		return ActionResultType.SUCCESS;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean hurt(@Nonnull DamageSource damageSource, float value) {
		if (!this.level.isClientSide && !this.removed) {
			if (this.isInvulnerableTo(damageSource)) {
				return false;
			}
			this.setHurtDir(-this.getHurtDir());
			this.setHurtTime(10);
			this.markHurt();
			this.setDamage(this.getDamage() + value * 10.0F);
			boolean flag = damageSource.getEntity() instanceof PlayerEntity && ((PlayerEntity)damageSource.getEntity()).abilities.instabuild;
			if (flag || this.getDamage() > 40.0F) {
				this.ejectPassengers();
				if (flag && !this.hasCustomName()) {
					this.removeBedVehicle();
				} else {
					this.destroy(damageSource);
				}
			}
		}
		return true;
	}

	@Override
	public void destroy(@Nonnull DamageSource damageSource) {
		this.removeBedVehicle();
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			ItemStack itemstack = new ItemStack(Items.MINECART);
			if (this.hasCustomName()) {
				itemstack.setHoverName(this.getCustomName());
			}

			this.spawnAtLocation(itemstack);
			this.spawnAtLocation(IBedVehicle.getBedBlock(this.getBedColor()));
		}
	}

	@Override @Nonnull
	public Type getMinecartType() {
		return BED;
	}

	@Override
	public ItemStack getCartItem() {
		return new ItemStack(STItems.BedMinecarts.byColor(this.getBedColor()));
	}

	@Override
	public boolean canBeRidden() {
		return true;
	}

	@Override
	public void setColor(DyeColor color) {
		this.entityData.set(DATA_ID_DYE_COLOR, color.ordinal());
	}

	@Override @Nonnull
	public DyeColor getBedColor() {
		return DyeColor.byId(this.entityData.get(DATA_ID_DYE_COLOR));
	}

	@Override
	protected void addAdditionalSaveData(@Nonnull CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putString("DyeColor", this.getBedColor().getName());
	}

	@Override
	protected void readAdditionalSaveData(@Nonnull CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		if (nbt.contains("DyeColor", Constants.NBT.TAG_STRING)) {
			this.setColor(DyeColor.byName(nbt.getString("DyeColor"), DyeColor.WHITE));
		}
	}

	@Override @Nonnull
	public BlockState getDefaultDisplayBlockState() {
		return STBlocks.Technical.getMinecartBedBlockState(this.getBedColor());
	}

	@Override @Nonnull
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void removeBedVehicle() {
		ChunkPos chunkPos = STSavedData.removeBedVehicle(this.uuid);
		if(chunkPos != null && this.level instanceof ServerWorld) {
			STSavedData.updateForceChunk(chunkPos, (ServerWorld)this.level, false);
		}
		this.remove();
	}

	@Override
	public float getBedVehicleRotY() {
		return this.yRot + 90.0f;
	}
	@Override
	public double getBedVehicleOffsetY() {
		return 0.875D;
	}

	@Override
	public int passengersCount() {
		return this.getPassengers().size();
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	public static BedMinecartEntity createBedMinecart(World level, double x, double y, double z) {
		return new BedMinecartEntity(level, x, y, z);
	}
}
