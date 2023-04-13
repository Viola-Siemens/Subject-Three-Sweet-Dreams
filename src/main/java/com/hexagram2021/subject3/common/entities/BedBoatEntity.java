package com.hexagram2021.subject3.common.entities;

import com.google.common.collect.Lists;
import com.hexagram2021.subject3.common.STSavedData;
import com.hexagram2021.subject3.register.STEntities;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BedBoatEntity extends Entity implements IBedVehicle {
	private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(BedBoatEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_ID_HURT_DIR = SynchedEntityData.defineId(BedBoatEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(BedBoatEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> DATA_ID_DYE_COLOR = SynchedEntityData.defineId(BedBoatEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(BedBoatEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_LEFT = SynchedEntityData.defineId(BedBoatEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_RIGHT = SynchedEntityData.defineId(BedBoatEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> DATA_ID_BUBBLE_TIME = SynchedEntityData.defineId(BedBoatEntity.class, EntityDataSerializers.INT);
	private final double[] paddlePositions = new double[2];
	private float outOfControlTicks;
	private float deltaRotation;
	private int lerpSteps;
	private double lerpX;
	private double lerpY;
	private double lerpZ;
	private double lerpYRot;
	private double lerpXRot;
	private boolean inputLeft;
	private boolean inputRight;
	private boolean inputUp;
	private boolean inputDown;
	private double waterLevel;
	private float landFriction;
	private Boat.Status status;
	private Boat.Status oldStatus;
	private double lastYd;
	private boolean isAboveBubbleColumn;
	private boolean bubbleColumnDirectionIsDown;
	private float bubbleMultiplier;
	private float bubbleAngle;
	private float bubbleAngleO;

	public BedBoatEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
		this.blocksBuilding = true;
	}

	public BedBoatEntity(Level level, double x, double y, double z) {
		this(STEntities.BED_BOAT.get(), level);
		this.setPos(x, y, z);
		this.setDeltaMovement(Vec3.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
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
	protected float getEyeHeight(@Nonnull Pose pose, EntityDimensions dimensions) {
		return dimensions.height;
	}

	@Override @Nonnull
	protected Entity.MovementEmission getMovementEmission() {
		return Entity.MovementEmission.NONE;
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_ID_HURT, 0);
		this.entityData.define(DATA_ID_HURT_DIR, 1);
		this.entityData.define(DATA_ID_DAMAGE, 0.0F);
		this.entityData.define(DATA_ID_DYE_COLOR, DyeColor.WHITE.ordinal());
		this.entityData.define(DATA_ID_TYPE, Boat.Type.OAK.ordinal());
		this.entityData.define(DATA_ID_PADDLE_LEFT, false);
		this.entityData.define(DATA_ID_PADDLE_RIGHT, false);
		this.entityData.define(DATA_ID_BUBBLE_TIME, 0);
	}

	@Override
	public boolean canCollideWith(@Nonnull Entity entity) {
		return canVehicleCollide(this, entity);
	}

	public static boolean canVehicleCollide(Entity vehicle, Entity entity) {
		return (entity.canBeCollidedWith() || entity.isPushable()) && !vehicle.isPassengerOfSameVehicle(vehicle);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override @Nonnull
	protected Vec3 getRelativePortalPosition(@Nonnull Direction.Axis axis, @Nonnull BlockUtil.FoundRectangle result) {
		return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(axis, result));
	}

	@Override
	public double getPassengersRidingOffset() {
		return -0.1D;
	}

	@Override
	public boolean hurt(@Nonnull DamageSource damageSource, float damage) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		}
		if (!this.level.isClientSide && !this.isRemoved()) {
			this.setHurtDir(-this.getHurtDir());
			this.setHurtTime(10);
			this.setDamage(this.getDamage() + damage * 10.0F);
			this.markHurt();
			this.gameEvent(GameEvent.ENTITY_DAMAGED, damageSource.getEntity());
			boolean flag = damageSource.getEntity() instanceof Player player && player.getAbilities().instabuild;
			if (flag || this.getDamage() > 40.0F) {
				if (!flag && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
					this.spawnAtLocation(this.getDropBoatItem());
					this.spawnAtLocation(IBedVehicle.getBedBlock(this.getBedColor()));
				}

				this.kill();
			}
		}
		return true;
	}

	@Override
	public void onAboveBubbleCol(boolean isDown) {
		if (!this.level.isClientSide) {
			this.isAboveBubbleColumn = true;
			this.bubbleColumnDirectionIsDown = isDown;
			if (this.getBubbleTime() == 0) {
				this.setBubbleTime(60);
			}
		}

		this.level.addParticle(ParticleTypes.SPLASH, this.getX() + this.random.nextDouble(), this.getY() + 0.7D, this.getZ() + this.random.nextDouble(), 0.0D, 0.0D, 0.0D);
		if (this.random.nextInt(20) == 0) {
			this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), this.getSwimSplashSound(), this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat(), false);
		}

		this.gameEvent(GameEvent.SPLASH, this.getControllingPassenger());
	}

	@Override
	public void push(@Nonnull Entity entity) {
		if (entity instanceof BedBoatEntity) {
			if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
				super.push(entity);
			}
		} else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
			super.push(entity);
		}
	}

	@Nonnull
	public Item getDropBoatItem() {
		return switch (this.getBoatType()) {
			case OAK -> Items.OAK_BOAT;
			case SPRUCE -> Items.SPRUCE_BOAT;
			case BIRCH -> Items.BIRCH_BOAT;
			case JUNGLE -> Items.JUNGLE_BOAT;
			case ACACIA -> Items.ACACIA_BOAT;
			case DARK_OAK -> Items.DARK_OAK_BOAT;
		};
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateHurt() {
		this.setHurtDir(-this.getHurtDir());
		this.setHurtTime(10);
		this.setDamage(this.getDamage() * 11.0F);
	}

	@Override
	public boolean isPickable() {
		return !this.isRemoved();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void lerpTo(double lerpX, double lerpY, double lerpZ, float lerpYRot, float lerpXRot, int steps, boolean value) {
		this.lerpX = lerpX;
		this.lerpY = lerpY;
		this.lerpZ = lerpZ;
		this.lerpYRot = lerpYRot;
		this.lerpXRot = lerpXRot;
		this.lerpSteps = 10;
	}

	@Override @Nonnull
	public Direction getMotionDirection() {
		return this.getDirection().getClockWise();
	}

	@Override
	public void tick() {
		this.oldStatus = this.status;
		this.status = this.getStatus();
		if (this.status != Boat.Status.UNDER_WATER && this.status != Boat.Status.UNDER_FLOWING_WATER) {
			this.outOfControlTicks = 0.0F;
		} else {
			++this.outOfControlTicks;
		}

		if (!this.level.isClientSide && this.outOfControlTicks >= 60.0F) {
			this.ejectPassengers();
		}

		if (this.getHurtTime() > 0) {
			this.setHurtTime(this.getHurtTime() - 1);
		}

		if (this.getDamage() > 0.0F) {
			this.setDamage(this.getDamage() - 1.0F);
		}

		super.tick();
		this.tickLerp();
		if (this.isControlledByLocalInstance()) {
			if (!(this.getFirstPassenger() instanceof Player)) {
				this.setPaddleState(false, false);
			}

			this.floatBoat();
			if (this.level.isClientSide) {
				this.controlBoat();
				this.level.sendPacketToServer(new ServerboundPaddleBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
			}

			this.move(MoverType.SELF, this.getDeltaMovement());
		} else {
			this.setDeltaMovement(Vec3.ZERO);
		}

		this.tickBubbleColumn();

		for(int i = 0; i <= 1; ++i) {
			if (this.getPaddleState(i)) {
				if (!this.isSilent() && (this.paddlePositions[i] % (Math.PI * 2.0D)) <= Math.PI / 4.0D && (this.paddlePositions[i] + (Math.PI / 8.0D)) % (Math.PI * 2.0D) >= (Math.PI / 4.0D)) {
					SoundEvent soundevent = this.getPaddleSound();
					if (soundevent != null) {
						Vec3 vec3 = this.getViewVector(1.0F);
						double d0 = i == 1 ? -vec3.z : vec3.z;
						double d1 = i == 1 ? vec3.x : -vec3.x;
						this.level.playSound(null, this.getX() + d0, this.getY(), this.getZ() + d1, soundevent, this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
						this.level.gameEvent(this.getControllingPassenger(), GameEvent.SPLASH, new BlockPos(this.getX() + d0, this.getY(), this.getZ() + d1));
					}
				}

				this.paddlePositions[i] = this.paddlePositions[i] + (Math.PI / 8.0D);
			} else {
				this.paddlePositions[i] = 0.0F;
			}
		}

		this.checkInsideBlocks();
		List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(0.2D, -0.01D, 0.2D), EntitySelector.pushableBy(this));
		if (!list.isEmpty()) {
			boolean flag = !this.level.isClientSide && !(this.getControllingPassenger() instanceof Player);

			for (Entity entity : list) {
				if (!entity.hasPassenger(this)) {
					if (flag && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getBbWidth() < this.getBbWidth() &&
							entity instanceof LivingEntity && !(entity instanceof WaterAnimal) && !(entity instanceof Player)) {
						entity.startRiding(this);
					} else {
						this.push(entity);
					}
				}
			}
		}

	}

	private void tickBubbleColumn() {
		if (this.level.isClientSide) {
			int i = this.getBubbleTime();
			if (i > 0) {
				this.bubbleMultiplier += 0.05F;
			} else {
				this.bubbleMultiplier -= 0.1F;
			}

			this.bubbleMultiplier = Mth.clamp(this.bubbleMultiplier, 0.0F, 1.0F);
			this.bubbleAngleO = this.bubbleAngle;
			this.bubbleAngle = 10.0F * (float)Math.sin(0.5F * this.level.getGameTime()) * this.bubbleMultiplier;
		} else {
			if (!this.isAboveBubbleColumn) {
				this.setBubbleTime(0);
			}

			int k = this.getBubbleTime();
			if (k > 0) {
				--k;
				this.setBubbleTime(k);
				int j = 60 - k - 1;
				if (j > 0 && k == 0) {
					this.setBubbleTime(0);
					Vec3 vec3 = this.getDeltaMovement();
					if (this.bubbleColumnDirectionIsDown) {
						this.setDeltaMovement(vec3.add(0.0D, -0.7D, 0.0D));
						this.ejectPassengers();
					} else {
						this.setDeltaMovement(vec3.x, this.hasPassenger((entity) -> entity instanceof Player) ? 2.7D : 0.6D, vec3.z);
					}
				}

				this.isAboveBubbleColumn = false;
			}
		}
	}

	@Nullable
	protected SoundEvent getPaddleSound() {
		return switch (this.getStatus()) {
			case IN_WATER, UNDER_WATER, UNDER_FLOWING_WATER -> SoundEvents.BOAT_PADDLE_WATER;
			case ON_LAND -> SoundEvents.BOAT_PADDLE_LAND;
			case IN_AIR -> null;
		};
	}

	private void tickLerp() {
		if (this.isControlledByLocalInstance()) {
			this.lerpSteps = 0;
			this.setPacketCoordinates(this.getX(), this.getY(), this.getZ());
		}

		if (this.lerpSteps > 0) {
			double d0 = this.getX() + (this.lerpX - this.getX()) / this.lerpSteps;
			double d1 = this.getY() + (this.lerpY - this.getY()) / this.lerpSteps;
			double d2 = this.getZ() + (this.lerpZ - this.getZ()) / this.lerpSteps;
			double d3 = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
			this.setYRot(this.getYRot() + (float)d3 / (float)this.lerpSteps);
			this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / this.lerpSteps);
			--this.lerpSteps;
			this.setPos(d0, d1, d2);
			this.setRot(this.getYRot(), this.getXRot());
		}
	}

	public void setPaddleState(boolean left, boolean right) {
		this.entityData.set(DATA_ID_PADDLE_LEFT, left);
		this.entityData.set(DATA_ID_PADDLE_RIGHT, right);
	}

	@OnlyIn(Dist.CLIENT)
	public float getRowingTime(int index, float time) {
		return this.getPaddleState(index) ? (float)Mth.clampedLerp(
				this.paddlePositions[index] - (Math.PI / 8.0D), this.paddlePositions[index], time
		) : 0.0F;
	}

	private Boat.Status getStatus() {
		Boat.Status boatStatus = this.isUnderwater();
		if (boatStatus != null) {
			this.waterLevel = this.getBoundingBox().maxY;
			return boatStatus;
		}
		if (this.checkInWater()) {
			return Boat.Status.IN_WATER;
		}
		float f = this.getGroundFriction();
		if (f > 0.0F) {
			this.landFriction = f;
			return Boat.Status.ON_LAND;
		}
		return Boat.Status.IN_AIR;
	}

	public float getWaterLevelAbove() {
		AABB aabb = this.getBoundingBox();
		int minX = Mth.floor(aabb.minX);
		int maxX = Mth.ceil(aabb.maxX);
		int minY = Mth.floor(aabb.maxY);
		int maxY = Mth.ceil(aabb.maxY - this.lastYd);
		int minZ = Mth.floor(aabb.minZ);
		int maxZ = Mth.ceil(aabb.maxZ);
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		fluidHeightExceeded:
		for(int k1 = minY; k1 < maxY; ++k1) {
			float f = 0.0F;

			for(int x = minX; x < maxX; ++x) {
				for(int z = minZ; z < maxZ; ++z) {
					mutable.set(x, k1, z);
					FluidState fluidstate = this.level.getFluidState(mutable);
					if (fluidstate.is(FluidTags.WATER)) {
						f = Math.max(f, fluidstate.getHeight(this.level, mutable));
					}

					if (f >= 1.0F) {
						continue fluidHeightExceeded;
					}
				}
			}

			if (f < 1.0F) {
				return mutable.getY() + f;
			}
		}

		return maxY + 1;
	}

	public float getGroundFriction() {
		AABB aabb = this.getBoundingBox();
		AABB aabb1 = new AABB(aabb.minX, aabb.minY - 0.001D, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ);
		int minX = Mth.floor(aabb1.minX) - 1;
		int maxX = Mth.ceil(aabb1.maxX) + 1;
		int minY = Mth.floor(aabb1.minY) - 1;
		int maxY = Mth.ceil(aabb1.maxY) + 1;
		int minZ = Mth.floor(aabb1.minZ) - 1;
		int maxZ = Mth.ceil(aabb1.maxZ) + 1;
		VoxelShape voxelshape = Shapes.create(aabb1);
		float f = 0.0F;
		int k1 = 0;
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for(int x = minX; x < maxX; ++x) {
			for(int z = minZ; z < maxZ; ++z) {
				int cnt = (x != minX && x != maxX - 1 ? 0 : 1) + (z != minZ && z != maxZ - 1 ? 0 : 1);
				if (cnt != 2) {
					for(int k2 = minY; k2 < maxY; ++k2) {
						if (cnt <= 0 || k2 != minY && k2 != maxY - 1) {
							mutable.set(x, k2, z);
							BlockState blockstate = this.level.getBlockState(mutable);
							if (!(blockstate.getBlock() instanceof WaterlilyBlock) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level, mutable).move(x, k2, z), voxelshape, BooleanOp.AND)) {
								f += blockstate.getFriction(this.level, mutable, this);
								++k1;
							}
						}
					}
				}
			}
		}

		return f / k1;
	}

	private boolean checkInWater() {
		AABB aabb = this.getBoundingBox();
		int minX = Mth.floor(aabb.minX);
		int maxX = Mth.ceil(aabb.maxX);
		int minY = Mth.floor(aabb.minY);
		int maxY = Mth.ceil(aabb.minY + 0.001D);
		int minZ = Mth.floor(aabb.minZ);
		int maxZ = Mth.ceil(aabb.maxZ);
		boolean flag = false;
		this.waterLevel = -Double.MAX_VALUE;
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for(int x = minX; x < maxX; ++x) {
			for(int y = minY; y < maxY; ++y) {
				for(int z = minZ; z < maxZ; ++z) {
					mutable.set(x, y, z);
					FluidState fluidstate = this.level.getFluidState(mutable);
					if (fluidstate.is(FluidTags.WATER)) {
						float f = (float)y + fluidstate.getHeight(this.level, mutable);
						this.waterLevel = Math.max(f, this.waterLevel);
						flag |= aabb.minY < (double)f;
					}
				}
			}
		}

		return flag;
	}

	@Nullable
	private Boat.Status isUnderwater() {
		AABB aabb = this.getBoundingBox();
		double d0 = aabb.maxY + 0.001D;
		int minX = Mth.floor(aabb.minX);
		int maxX = Mth.ceil(aabb.maxX);
		int minY = Mth.floor(aabb.maxY);
		int maxY = Mth.ceil(d0);
		int minZ = Mth.floor(aabb.minZ);
		int maxZ = Mth.ceil(aabb.maxZ);
		boolean flag = false;
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for(int k1 = minX; k1 < maxX; ++k1) {
			for(int l1 = minY; l1 < maxY; ++l1) {
				for(int i2 = minZ; i2 < maxZ; ++i2) {
					mutable.set(k1, l1, i2);
					FluidState fluidstate = this.level.getFluidState(mutable);
					if (fluidstate.is(FluidTags.WATER) && d0 < mutable.getY() + fluidstate.getHeight(this.level, mutable)) {
						if (!fluidstate.isSource()) {
							return Boat.Status.UNDER_FLOWING_WATER;
						}

						flag = true;
					}
				}
			}
		}

		return flag ? Boat.Status.UNDER_WATER : null;
	}

	private void floatBoat() {
		double d1 = this.isNoGravity() ? 0.0D : -0.04D;
		double d2 = 0.0D;
		double invFriction = 0.05D;
		if (this.oldStatus == Boat.Status.IN_AIR && this.status != Boat.Status.IN_AIR && this.status != Boat.Status.ON_LAND) {
			this.waterLevel = this.getY(1.0D);
			this.setPos(this.getX(), this.getWaterLevelAbove() - this.getBbHeight() + 0.101D, this.getZ());
			this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
			this.lastYd = 0.0D;
			this.status = Boat.Status.IN_WATER;
		} else {
			if (this.status == Boat.Status.IN_WATER) {
				d2 = (this.waterLevel - this.getY()) / this.getBbHeight();
				invFriction = 0.9D;
			} else if (this.status == Boat.Status.UNDER_FLOWING_WATER) {
				d1 = -7.0E-4D;
				invFriction = 0.9D;
			} else if (this.status == Boat.Status.UNDER_WATER) {
				d2 = 0.01D;
				invFriction = 0.45D;
			} else if (this.status == Boat.Status.IN_AIR) {
				invFriction = 0.9D;
			} else if (this.status == Boat.Status.ON_LAND) {
				invFriction = this.landFriction;
				if (this.getControllingPassenger() instanceof Player) {
					this.landFriction /= 2.0F;
				}
			}

			Vec3 vec3 = this.getDeltaMovement();
			this.setDeltaMovement(vec3.x * invFriction, vec3.y + d1, vec3.z * invFriction);
			this.deltaRotation *= invFriction;
			if (d2 > 0.0D) {
				Vec3 vec31 = this.getDeltaMovement();
				this.setDeltaMovement(vec31.x, (vec31.y + d2 * 0.06153846016296973D) * 0.75D, vec31.z);
			}
		}

	}

	private void controlBoat() {
		if (this.isVehicle()) {
			float f = 0.0F;
			if (this.inputLeft) {
				--this.deltaRotation;
			}

			if (this.inputRight) {
				++this.deltaRotation;
			}

			if (this.inputRight != this.inputLeft && !this.inputUp && !this.inputDown) {
				f += 0.005F;
			}

			this.setYRot(this.getYRot() + this.deltaRotation);
			if (this.inputUp) {
				f += 0.04F;
			}

			if (this.inputDown) {
				f -= 0.005F;
			}

			this.setDeltaMovement(this.getDeltaMovement().add(
					Mth.sin(-this.getYRot() * ((float)Math.PI / 180F)) * f,
					0.0D,
					Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * f)
			);
			this.setPaddleState(this.inputRight && !this.inputLeft || this.inputUp, this.inputLeft && !this.inputRight || this.inputUp);
		}
	}

	@Override
	public void positionRider(@Nonnull Entity entity) {
		if (this.hasPassenger(entity)) {
			float f = 0.0F;
			float f1 = (this.isRemoved() ? 0.01F : (float)this.getPassengersRidingOffset()) + (float)entity.getMyRidingOffset();
			if (this.getPassengers().size() > 1) {
				int i = this.getPassengers().indexOf(entity);
				if (i == 0) {
					f = 0.2F;
				} else {
					f = -0.6F;
				}

				if (entity instanceof Animal) {
					f = f + 0.2F;
				}
			}

			Vec3 vec3 = (new Vec3(f, 0.0D, 0.0D)).yRot(-this.getYRot() * ((float)Math.PI / 180F) - ((float)Math.PI / 2F));
			entity.setPos(this.getX() + vec3.x, this.getY() + (double)f1, this.getZ() + vec3.z);
			entity.setYRot(entity.getYRot() + this.deltaRotation);
			entity.setYHeadRot(entity.getYHeadRot() + this.deltaRotation);
			this.clampRotation(entity);
			if (entity instanceof Animal && this.getPassengers().size() > 1) {
				int j = entity.getId() % 2 == 0 ? 90 : 270;
				entity.setYBodyRot(((Animal)entity).yBodyRot + j);
				entity.setYHeadRot(entity.getYHeadRot() + j);
			}

		}
	}

	@Override @Nonnull
	public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
		Vec3 vec3 = getCollisionHorizontalEscapeVector(this.getBbWidth() * Mth.SQRT_OF_TWO, livingEntity.getBbWidth(), livingEntity.getYRot());
		double d0 = this.getX() + vec3.x;
		double d1 = this.getZ() + vec3.z;
		BlockPos blockpos = new BlockPos(d0, this.getBoundingBox().maxY, d1);
		BlockPos below = blockpos.below();
		if (!this.level.isWaterAt(below)) {
			List<Vec3> floors = Lists.newArrayList();
			double d2 = this.level.getBlockFloorHeight(blockpos);
			if (DismountHelper.isBlockFloorValid(d2)) {
				floors.add(new Vec3(d0, (double)blockpos.getY() + d2, d1));
			}

			double d3 = this.level.getBlockFloorHeight(below);
			if (DismountHelper.isBlockFloorValid(d3)) {
				floors.add(new Vec3(d0, (double)below.getY() + d3, d1));
			}

			for(Pose pose : livingEntity.getDismountPoses()) {
				for(Vec3 floor : floors) {
					if (DismountHelper.canDismountTo(this.level, floor, livingEntity, pose)) {
						livingEntity.setPose(pose);
						return floor;
					}
				}
			}
		}

		return super.getDismountLocationForPassenger(livingEntity);
	}

	protected void clampRotation(Entity entity) {
		entity.setYBodyRot(this.getYRot());
		float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
		float f1 = Mth.clamp(f, -105.0F, 105.0F);
		entity.yRotO += f1 - f;
		entity.setYRot(entity.getYRot() + f1 - f);
		entity.setYHeadRot(entity.getYRot());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onPassengerTurned(@Nonnull Entity entity) {
		this.clampRotation(entity);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt) {
		nbt.putString("DyeColor", this.getBedColor().getName());
		nbt.putString("Type", this.getBoatType().getName());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt) {
		if (nbt.contains("Type", Tag.TAG_STRING)) {
			this.setType(Boat.Type.byName(nbt.getString("Type")));
		}
		if (nbt.contains("DyeColor", Tag.TAG_STRING)) {
			this.setColor(DyeColor.byName(nbt.getString("DyeColor"), DyeColor.WHITE));
		}
	}

	@Override @Nonnull
	public InteractionResult interact(Player player, @Nonnull InteractionHand hand) {
		if (player.isSecondaryUseActive()) {
			return InteractionResult.PASS;
		}
		if (this.outOfControlTicks < 60.0F) {
			if (!this.level.isClientSide) {
				if (!BedBlock.canSetSpawn(this.level)) {
					this.level.explode(
							this, DamageSource.badRespawnPointExplosion(), null,
							this.getX() + 0.5D, this.getY() + 0.125D, this.getZ() + 0.5D,
							5.0F, true, Explosion.BlockInteraction.DESTROY
					);
					this.kill();
					return InteractionResult.SUCCESS;
				}
				InteractionResult ret;
				if(player.startRiding(this)) {
					ret = InteractionResult.CONSUME;
					if(player instanceof IHasVehicleRespawnPosition hasVehicleRespawnPosition) {
						hasVehicleRespawnPosition.setBedVehicleUUID(this.uuid);
					}
				} else {
					ret = InteractionResult.PASS;
				}
				return ret;
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, @Nonnull BlockState state, @Nonnull BlockPos pos) {
		this.lastYd = this.getDeltaMovement().y;
		if (!this.isPassenger()) {
			if (onGround) {
				if (this.fallDistance > 3.0F) {
					if (this.status != Boat.Status.ON_LAND) {
						this.fallDistance = 0.0F;
						return;
					}

					this.causeFallDamage(this.fallDistance, 1.0F, DamageSource.FALL);
					if (!this.level.isClientSide && !this.isRemoved()) {
						this.kill();
						if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
							for(int i = 0; i < 3; ++i) {
								this.spawnAtLocation(this.getBoatType().getPlanks());
							}

							for(int j = 0; j < 2; ++j) {
								this.spawnAtLocation(Items.STICK);
							}
						}
					}
				}

				this.fallDistance = 0.0F;
			} else if (!this.level.getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && y < 0.0D) {
				this.fallDistance = (float)(this.fallDistance - y);
			}
		}
	}

	@Override
	public void kill() {
		ChunkPos chunkPos = STSavedData.removeBedVehicle(this.uuid);
		if(chunkPos != null && this.level instanceof ServerLevel) {
			STSavedData.updateForceChunk(chunkPos, (ServerLevel)this.level, false);
		}
		super.kill();
	}

	@Override
	public float getBedVehicleRotY() {
		return this.getYRot();
	}
	@Override
	public double getBedVehicleOffsetY() {
		return 0.875D;
	}

	public boolean getPaddleState(int index) {
		return this.entityData.<Boolean>get(index == 0 ? DATA_ID_PADDLE_LEFT : DATA_ID_PADDLE_RIGHT) && this.getControllingPassenger() != null;
	}

	public void setDamage(float damage) {
		this.entityData.set(DATA_ID_DAMAGE, damage);
	}

	public float getDamage() {
		return this.entityData.get(DATA_ID_DAMAGE);
	}

	public void setHurtTime(int hurtTime) {
		this.entityData.set(DATA_ID_HURT, hurtTime);
	}

	public int getHurtTime() {
		return this.entityData.get(DATA_ID_HURT);
	}

	private void setBubbleTime(int bubbleTime) {
		this.entityData.set(DATA_ID_BUBBLE_TIME, bubbleTime);
	}

	private int getBubbleTime() {
		return this.entityData.get(DATA_ID_BUBBLE_TIME);
	}

	@OnlyIn(Dist.CLIENT)
	public float getBubbleAngle(float rate) {
		return Mth.lerp(rate, this.bubbleAngleO, this.bubbleAngle);
	}

	public void setHurtDir(int hurtDir) {
		this.entityData.set(DATA_ID_HURT_DIR, hurtDir);
	}

	public int getHurtDir() {
		return this.entityData.get(DATA_ID_HURT_DIR);
	}

	public void setType(Boat.Type type) {
		this.entityData.set(DATA_ID_TYPE, type.ordinal());
	}

	public Boat.Type getBoatType() {
		return Boat.Type.byId(this.entityData.get(DATA_ID_TYPE));
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
	protected boolean canAddPassenger(@Nonnull Entity entity) {
		return this.getPassengers().size() < 1 && !this.isEyeInFluid(FluidTags.WATER);
	}

	@Override @Nullable
	public Entity getControllingPassenger() {
		return this.getFirstPassenger();
	}

	@OnlyIn(Dist.CLIENT)
	public void setInput(boolean inputLeft, boolean inputRight, boolean inputUp, boolean inputDown) {
		this.inputLeft = inputLeft;
		this.inputRight = inputRight;
		this.inputUp = inputUp;
		this.inputDown = inputDown;
	}

	@Override @Nonnull
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean isUnderWater() {
		return this.status == Boat.Status.UNDER_WATER || this.status == Boat.Status.UNDER_FLOWING_WATER;
	}

	@Override
	protected void addPassenger(@Nonnull Entity passenger) {
		super.addPassenger(passenger);
		if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
			this.lerpSteps = 0;
			this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float)this.lerpYRot, (float)this.lerpXRot);
		}
	}

	@Override
	public int passengersCount() {
		return this.getPassengers().size();
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(this.getDropBoatItem());
	}
}
