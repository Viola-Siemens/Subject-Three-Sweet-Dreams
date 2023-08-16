package com.hexagram2021.subject3.common.entities;

import com.hexagram2021.subject3.common.STSavedData;
import com.hexagram2021.subject3.register.STBlocks;
import com.hexagram2021.subject3.register.STEntities;
import com.hexagram2021.subject3.register.STItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class BedMinecartEntity extends AbstractMinecart implements IBedVehicle {
	@SuppressWarnings("NotNullFieldNotInitialized")
	public static Type BED;

	private static final EntityDataAccessor<Integer> DATA_ID_DYE_COLOR = SynchedEntityData.defineId(BedMinecartEntity.class, EntityDataSerializers.INT);

	public BedMinecartEntity(EntityType<?> type, Level level) {
		super(type, level);
	}

	public BedMinecartEntity(Level level, double x, double y, double z) {
		super(STEntities.BED_MINECART.get(), level, x, y, z);
	}

	@Override
	public void setPos(double x, double y, double z) {
		super.setPos(x, y, z);

		if(this.level instanceof ServerLevel serverlevel) {
			if (serverlevel.dimension().equals(ServerLevel.OVERWORLD)) {
				ChunkPos newPos = new ChunkPos(this.blockPosition());
				ChunkPos oldPos = STSavedData.addBedVehicle(this.uuid, newPos);
				if (!newPos.equals(oldPos)) {
					STSavedData.updateForceChunk(newPos, serverlevel, true);
					if (oldPos != null) {
						STSavedData.updateForceChunk(oldPos, serverlevel, false);
					}
				}
			}
		}
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_ID_DYE_COLOR, DyeColor.WHITE.ordinal());
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		InteractionResult ret = super.interact(player, hand);
		if (ret.consumesAction()) return ret;
		if (player.isSecondaryUseActive()) {
			return InteractionResult.PASS;
		}
		if (this.isVehicle()) {
			return InteractionResult.PASS;
		}
		if (!this.level.isClientSide) {
			if (!BedBlock.canSetSpawn(this.level)) {
				Vec3 vec3 = new Vec3(this.getX() + 0.5D, this.getY() + 0.125D, this.getZ() + 0.5D);
				this.level.explode(
						this, DamageSource.badRespawnPointExplosion(vec3), null,
						vec3, 5.0F, true, Level.ExplosionInteraction.BLOCK
				);
				this.kill();
				return InteractionResult.CONSUME;
			}
			if(player.startRiding(this)) {
				ret = InteractionResult.CONSUME;
				if(player instanceof IHasVehicleRespawnPosition) {
					((IHasVehicleRespawnPosition)player).setBedVehicleUUID(this.uuid);
				}
			} else {
				ret = InteractionResult.PASS;
			}
			return ret;
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean hurt(DamageSource damageSource, float value) {
		if (!this.level.isClientSide && !this.isRemoved()) {
			if (this.isInvulnerableTo(damageSource)) {
				return false;
			}
			this.setHurtDir(-this.getHurtDir());
			this.setHurtTime(10);
			this.markHurt();
			this.setDamage(this.getDamage() + value * 10.0F);
			boolean flag = damageSource.getEntity() instanceof Player player && player.getAbilities().instabuild;
			if (flag || this.getDamage() > 40.0F) {
				this.ejectPassengers();
				if (flag && !this.hasCustomName()) {
					this.kill();
				} else {
					this.destroy(damageSource);
				}
			}
		}
		return true;
	}

	@Override
	public void destroy(DamageSource damageSource) {
		this.kill();
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			ItemStack itemstack = new ItemStack(Items.MINECART);
			if (this.hasCustomName()) {
				itemstack.setHoverName(this.getCustomName());
			}

			this.spawnAtLocation(itemstack);
			this.spawnAtLocation(IBedVehicle.getBedBlock(this.getBedColor()));
		}
	}

	@Override
	public Type getMinecartType() {
		return BED;
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(STItems.BedMinecarts.byColor(this.getBedColor()));
	}

	@Override
	protected Item getDropItem() {
		return STItems.BedMinecarts.byColor(this.getBedColor());
	}

	@Override
	public boolean canBeRidden() {
		return true;
	}

	@Override
	public void setColor(DyeColor color) {
		this.entityData.set(DATA_ID_DYE_COLOR, color.ordinal());
	}

	@Override
	public DyeColor getBedColor() {
		return DyeColor.byId(this.entityData.get(DATA_ID_DYE_COLOR));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putString("DyeColor", this.getBedColor().getName());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		if (nbt.contains("DyeColor", Tag.TAG_STRING)) {
			this.setColor(DyeColor.byName(nbt.getString("DyeColor"), DyeColor.WHITE));
		}
	}

	@Override
	public BlockState getDefaultDisplayBlockState() {
		return STBlocks.Technical.getMinecartBedBlockState(this.getBedColor());
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void kill() {
		ChunkPos chunkPos = STSavedData.removeBedVehicle(this.uuid);
		if(chunkPos != null && this.level instanceof ServerLevel serverLevel) {
			STSavedData.updateForceChunk(chunkPos, serverLevel, false);
		}
		super.kill();
	}

	@Override
	public float getBedVehicleRotY() {
		return this.getYRot() + 90.0f;
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

	public static BedMinecartEntity createBedMinecart(Level level, double x, double y, double z) {
		return new BedMinecartEntity(level, x, y, z);
	}
}
