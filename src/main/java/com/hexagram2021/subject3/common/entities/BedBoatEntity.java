package com.hexagram2021.subject3.common.entities;

import com.hexagram2021.subject3.register.STEntities;
import com.hexagram2021.subject3.register.STItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CSteerBoatPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class BedBoatEntity extends Entity implements IBedVehicle {
	private static final DataParameter<Integer> DATA_ID_HURT = EntityDataManager.defineId(BedBoatEntity.class, DataSerializers.INT);
	private static final DataParameter<Integer> DATA_ID_HURT_DIR = EntityDataManager.defineId(BedBoatEntity.class, DataSerializers.INT);
	private static final DataParameter<Float> DATA_ID_DAMAGE = EntityDataManager.defineId(BedBoatEntity.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> DATA_ID_DYE_COLOR = EntityDataManager.defineId(BedBoatEntity.class, DataSerializers.INT);
	private static final DataParameter<Integer> DATA_ID_TYPE = EntityDataManager.defineId(BedBoatEntity.class, DataSerializers.INT);
	private static final DataParameter<Boolean> DATA_ID_PADDLE_LEFT = EntityDataManager.defineId(BedBoatEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> DATA_ID_PADDLE_RIGHT = EntityDataManager.defineId(BedBoatEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> DATA_ID_BUBBLE_TIME = EntityDataManager.defineId(BedBoatEntity.class, DataSerializers.INT);
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
	private BoatEntity.Status status;
	private BoatEntity.Status oldStatus;
	private double lastYd;
	private boolean isAboveBubbleColumn;
	private boolean bubbleColumnDirectionIsDown;
	private float bubbleMultiplier;
	private float bubbleAngle;
	private float bubbleAngleO;

	public BedBoatEntity(EntityType<?> entityType, World level) {
		super(entityType, level);
		this.blocksBuilding = true;
		this.forcedLoading = true;
	}

	public BedBoatEntity(World level, double x, double y, double z) {
		this(STEntities.BED_MINECART.get(), level);
		this.setPos(x, y, z);
		this.setDeltaMovement(Vector3d.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	@Override
	protected float getEyeHeight(@Nonnull Pose pos, EntitySize entitySize) {
		return entitySize.height;
	}

	@Override
	protected boolean isMovementNoisy() {
		return false;
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_ID_HURT, 0);
		this.entityData.define(DATA_ID_HURT_DIR, 1);
		this.entityData.define(DATA_ID_DAMAGE, 0.0F);
		this.entityData.define(DATA_ID_DYE_COLOR, DyeColor.WHITE.ordinal());
		this.entityData.define(DATA_ID_TYPE, BoatEntity.Type.OAK.ordinal());
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
	protected Vector3d getRelativePortalPosition(@Nonnull Direction.Axis axis, @Nonnull TeleportationRepositioner.Result result) {
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
		if (!this.level.isClientSide && !this.removed) {
			this.setHurtDir(-this.getHurtDir());
			this.setHurtTime(10);
			this.setDamage(this.getDamage() + damage * 10.0F);
			this.markHurt();
			boolean flag = damageSource.getEntity() instanceof PlayerEntity && ((PlayerEntity)damageSource.getEntity()).abilities.instabuild;
			if (flag || this.getDamage() > 40.0F) {
				if (!flag && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
					this.spawnAtLocation(this.getDropItem());
				}

				this.remove();
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
	public Item getDropItem() {
		return STItems.BedBoats.byTypeAndColor(this.getBoatType(), this.getBedColor());
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
		return !this.removed;
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
		if (this.status != BoatEntity.Status.UNDER_WATER && this.status != BoatEntity.Status.UNDER_FLOWING_WATER) {
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
			if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof PlayerEntity)) {
				this.setPaddleState(false, false);
			}

			this.floatBoat();
			if (this.level.isClientSide) {
				this.controlBoat();
				this.level.sendPacketToServer(new CSteerBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
			}

			this.move(MoverType.SELF, this.getDeltaMovement());
		} else {
			this.setDeltaMovement(Vector3d.ZERO);
		}

		this.tickBubbleColumn();

		for(int i = 0; i <= 1; ++i) {
			if (this.getPaddleState(i)) {
				if (!this.isSilent() && (this.paddlePositions[i] % (Math.PI * 2.0D)) <= Math.PI / 4.0D && (this.paddlePositions[i] + (Math.PI / 8.0D)) % (Math.PI * 2.0D) >= (Math.PI / 4.0D)) {
					SoundEvent soundevent = this.getPaddleSound();
					if (soundevent != null) {
						Vector3d vector3d = this.getViewVector(1.0F);
						double d0 = i == 1 ? -vector3d.z : vector3d.z;
						double d1 = i == 1 ? vector3d.x : -vector3d.x;
						this.level.playSound(null, this.getX() + d0, this.getY(), this.getZ() + d1, soundevent, this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
					}
				}

				this.paddlePositions[i] = this.paddlePositions[i] + (Math.PI / 8.0D);
			} else {
				this.paddlePositions[i] = 0.0F;
			}
		}

		this.checkInsideBlocks();
		List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(0.2D, -0.01D, 0.2D), EntityPredicates.pushableBy(this));
		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.hasPassenger(this)) {
					this.push(entity);
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

			this.bubbleMultiplier = MathHelper.clamp(this.bubbleMultiplier, 0.0F, 1.0F);
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
					Vector3d vector3d = this.getDeltaMovement();
					if (this.bubbleColumnDirectionIsDown) {
						this.setDeltaMovement(vector3d.add(0.0D, -0.7D, 0.0D));
						this.ejectPassengers();
					} else {
						this.setDeltaMovement(vector3d.x, this.hasPassenger(PlayerEntity.class) ? 2.7D : 0.6D, vector3d.z);
					}
				}

				this.isAboveBubbleColumn = false;
			}
		}

	}

	@Nullable
	protected SoundEvent getPaddleSound() {
		switch(this.getStatus()) {
			case IN_WATER:
			case UNDER_WATER:
			case UNDER_FLOWING_WATER:
				return SoundEvents.BOAT_PADDLE_WATER;
			case ON_LAND:
				return SoundEvents.BOAT_PADDLE_LAND;
			case IN_AIR:
			default:
				return null;
		}
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
			double d3 = MathHelper.wrapDegrees(this.lerpYRot - this.yRot);
			this.yRot = (float)(this.yRot + d3 / this.lerpSteps);
			this.xRot = (float)(this.xRot + (this.lerpXRot - this.xRot) / this.lerpSteps);
			--this.lerpSteps;
			this.setPos(d0, d1, d2);
			this.setRot(this.yRot, this.xRot);
		}
	}

	public void setPaddleState(boolean left, boolean right) {
		this.entityData.set(DATA_ID_PADDLE_LEFT, left);
		this.entityData.set(DATA_ID_PADDLE_RIGHT, right);
	}

	@OnlyIn(Dist.CLIENT)
	public float getRowingTime(int index, float time) {
		return this.getPaddleState(index) ? (float)MathHelper.clampedLerp(this.paddlePositions[index] - (Math.PI / 8.0D), this.paddlePositions[index], time) : 0.0F;
	}

	private BoatEntity.Status getStatus() {
		BoatEntity.Status boatStatus = this.isUnderwater();
		if (boatStatus != null) {
			this.waterLevel = this.getBoundingBox().maxY;
			return boatStatus;
		}
		if (this.checkInWater()) {
			return BoatEntity.Status.IN_WATER;
		}
		float f = this.getGroundFriction();
		if (f > 0.0F) {
			this.landFriction = f;
			return BoatEntity.Status.ON_LAND;
		}
		return BoatEntity.Status.IN_AIR;
	}

	public float getWaterLevelAbove() {
		AxisAlignedBB axisalignedbb = this.getBoundingBox();
		int i = MathHelper.floor(axisalignedbb.minX);
		int j = MathHelper.ceil(axisalignedbb.maxX);
		int k = MathHelper.floor(axisalignedbb.maxY);
		int l = MathHelper.ceil(axisalignedbb.maxY - this.lastYd);
		int i1 = MathHelper.floor(axisalignedbb.minZ);
		int j1 = MathHelper.ceil(axisalignedbb.maxZ);
		BlockPos.Mutable mutablePos = new BlockPos.Mutable();

		label39:
		for(int k1 = k; k1 < l; ++k1) {
			float f = 0.0F;

			for(int l1 = i; l1 < j; ++l1) {
				for(int i2 = i1; i2 < j1; ++i2) {
					mutablePos.set(l1, k1, i2);
					FluidState fluidstate = this.level.getFluidState(mutablePos);
					if (fluidstate.is(FluidTags.WATER)) {
						f = Math.max(f, fluidstate.getHeight(this.level, mutablePos));
					}

					if (f >= 1.0F) {
						continue label39;
					}
				}
			}

			if (f < 1.0F) {
				return mutablePos.getY() + f;
			}
		}

		return l + 1;
	}

	public float getGroundFriction() {
		AxisAlignedBB originAABB = this.getBoundingBox();
		AxisAlignedBB aabb = new AxisAlignedBB(originAABB.minX, originAABB.minY - 0.001D, originAABB.minZ, originAABB.maxX, originAABB.minY, originAABB.maxZ);
		int i = MathHelper.floor(aabb.minX) - 1;
		int j = MathHelper.ceil(aabb.maxX) + 1;
		int k = MathHelper.floor(aabb.minY) - 1;
		int l = MathHelper.ceil(aabb.maxY) + 1;
		int i1 = MathHelper.floor(aabb.minZ) - 1;
		int j1 = MathHelper.ceil(aabb.maxZ) + 1;
		VoxelShape voxelshape = VoxelShapes.create(aabb);
		float f = 0.0F;
		int k1 = 0;
		BlockPos.Mutable mutablePos = new BlockPos.Mutable();

		for(int l1 = i; l1 < j; ++l1) {
			for(int i2 = i1; i2 < j1; ++i2) {
				int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);
				if (j2 != 2) {
					for(int k2 = k; k2 < l; ++k2) {
						if (j2 <= 0 || k2 != k && k2 != l - 1) {
							mutablePos.set(l1, k2, i2);
							BlockState blockstate = this.level.getBlockState(mutablePos);
							if (!(blockstate.getBlock() instanceof LilyPadBlock) && VoxelShapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level, mutablePos).move(l1, k2, i2), voxelshape, IBooleanFunction.AND)) {
								f += blockstate.getSlipperiness(this.level, mutablePos, this);
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
		AxisAlignedBB axisalignedbb = this.getBoundingBox();
		int i = MathHelper.floor(axisalignedbb.minX);
		int j = MathHelper.ceil(axisalignedbb.maxX);
		int k = MathHelper.floor(axisalignedbb.minY);
		int l = MathHelper.ceil(axisalignedbb.minY + 0.001D);
		int i1 = MathHelper.floor(axisalignedbb.minZ);
		int j1 = MathHelper.ceil(axisalignedbb.maxZ);
		boolean flag = false;
		this.waterLevel = Double.MIN_VALUE;
		BlockPos.Mutable mutablePos = new BlockPos.Mutable();

		for(int k1 = i; k1 < j; ++k1) {
			for(int l1 = k; l1 < l; ++l1) {
				for(int i2 = i1; i2 < j1; ++i2) {
					mutablePos.set(k1, l1, i2);
					FluidState fluidstate = this.level.getFluidState(mutablePos);
					if (fluidstate.is(FluidTags.WATER)) {
						float f = l1 + fluidstate.getHeight(this.level, mutablePos);
						this.waterLevel = Math.max(f, this.waterLevel);
						flag |= axisalignedbb.minY < f;
					}
				}
			}
		}

		return flag;
	}

	@Nullable
	private BoatEntity.Status isUnderwater() {
		AxisAlignedBB axisalignedbb = this.getBoundingBox();
		double d0 = axisalignedbb.maxY + 0.001D;
		int i = MathHelper.floor(axisalignedbb.minX);
		int j = MathHelper.ceil(axisalignedbb.maxX);
		int k = MathHelper.floor(axisalignedbb.maxY);
		int l = MathHelper.ceil(d0);
		int i1 = MathHelper.floor(axisalignedbb.minZ);
		int j1 = MathHelper.ceil(axisalignedbb.maxZ);
		boolean flag = false;
		BlockPos.Mutable mutablePos = new BlockPos.Mutable();

		for(int k1 = i; k1 < j; ++k1) {
			for(int l1 = k; l1 < l; ++l1) {
				for(int i2 = i1; i2 < j1; ++i2) {
					mutablePos.set(k1, l1, i2);
					FluidState fluidstate = this.level.getFluidState(mutablePos);
					if (fluidstate.is(FluidTags.WATER) && d0 < mutablePos.getY() + fluidstate.getHeight(this.level, mutablePos)) {
						if (!fluidstate.isSource()) {
							return BoatEntity.Status.UNDER_FLOWING_WATER;
						}

						flag = true;
					}
				}
			}
		}

		return flag ? BoatEntity.Status.UNDER_WATER : null;
	}

	private void floatBoat() {
		double d1 = this.isNoGravity() ? 0.0D : -0.04D;
		double d2 = 0.0D;
		double invFriction = 0.05D;
		if (this.oldStatus == BoatEntity.Status.IN_AIR && this.status != BoatEntity.Status.IN_AIR && this.status != BoatEntity.Status.ON_LAND) {
			this.waterLevel = this.getY(1.0D);
			this.setPos(this.getX(), this.getWaterLevelAbove() - this.getBbHeight() + 0.101D, this.getZ());
			this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
			this.lastYd = 0.0D;
			this.status = BoatEntity.Status.IN_WATER;
		} else {
			if (this.status == BoatEntity.Status.IN_WATER) {
				d2 = (this.waterLevel - this.getY()) / this.getBbHeight();
				invFriction = 0.9D;
			} else if (this.status == BoatEntity.Status.UNDER_FLOWING_WATER) {
				d1 = -7.0E-4D;
				invFriction = 0.9D;
			} else if (this.status == BoatEntity.Status.UNDER_WATER) {
				d2 = 0.01F;
				invFriction = 0.45D;
			} else if (this.status == BoatEntity.Status.IN_AIR) {
				invFriction = 0.9D;
			} else if (this.status == BoatEntity.Status.ON_LAND) {
				invFriction = this.landFriction;
				if (this.getControllingPassenger() instanceof PlayerEntity) {
					this.landFriction /= 2.0F;
				}
			}

			Vector3d vector3d = this.getDeltaMovement();
			this.setDeltaMovement(vector3d.x * invFriction, vector3d.y + d1, vector3d.z * invFriction);
			this.deltaRotation *= invFriction;
			if (d2 > 0.0D) {
				Vector3d vector3d1 = this.getDeltaMovement();
				this.setDeltaMovement(vector3d1.x, (vector3d1.y + d2 * 0.06153846016296973D) * 0.75D, vector3d1.z);
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

			this.yRot += this.deltaRotation;
			if (this.inputUp) {
				f += 0.04F;
			}

			if (this.inputDown) {
				f -= 0.005F;
			}

			this.setDeltaMovement(this.getDeltaMovement().add(MathHelper.sin(-this.yRot * ((float)Math.PI / 180.0F)) * f, 0.0D, MathHelper.cos(this.yRot * ((float)Math.PI / 180.0F)) * f));
			this.setPaddleState(this.inputRight && !this.inputLeft || this.inputUp, this.inputLeft && !this.inputRight || this.inputUp);
		}
	}

	@Override
	public void positionRider(@Nonnull Entity entity) {
		if (this.hasPassenger(entity)) {
			float f = 0.0F;
			float f1 = (this.removed ? 0.01F : (float)this.getPassengersRidingOffset()) + (float)entity.getMyRidingOffset();
			if (this.getPassengers().size() > 1) {
				int i = this.getPassengers().indexOf(entity);
				if (i == 0) {
					f = 0.2F;
				} else {
					f = -0.6F;
				}

				if (entity instanceof AnimalEntity) {
					f = f + 0.2F;
				}
			}

			Vector3d vector3d = (new Vector3d(f, 0.0D, 0.0D)).yRot(-this.yRot * ((float)Math.PI / 180.0F) - ((float)Math.PI / 2.0F));
			entity.setPos(this.getX() + vector3d.x, this.getY() + f1, this.getZ() + vector3d.z);
			entity.yRot += this.deltaRotation;
			entity.setYHeadRot(entity.getYHeadRot() + this.deltaRotation);
			this.clampRotation(entity);
			if (entity instanceof AnimalEntity && this.getPassengers().size() > 1) {
				int j = entity.getId() % 2 == 0 ? 90 : 270;
				entity.setYBodyRot(((AnimalEntity)entity).yBodyRot + j);
				entity.setYHeadRot(entity.getYHeadRot() + j);
			}

		}
	}

	@Override @Nonnull
	public Vector3d getDismountLocationForPassenger(LivingEntity livingEntity) {
		Vector3d vector3d = getCollisionHorizontalEscapeVector(this.getBbWidth() * MathHelper.SQRT_OF_TWO, livingEntity.getBbWidth(), this.yRot);
		double d0 = this.getX() + vector3d.x;
		double d1 = this.getZ() + vector3d.z;
		BlockPos blockpos = new BlockPos(d0, this.getBoundingBox().maxY, d1);
		BlockPos below = blockpos.below();
		if (!this.level.isWaterAt(below)) {
			double d2 = blockpos.getY() + this.level.getBlockFloorHeight(blockpos);
			double d3 = blockpos.getY() + this.level.getBlockFloorHeight(below);

			for(Pose pose : livingEntity.getDismountPoses()) {
				Vector3d vector3d1 = TransportationHelper.findDismountLocation(this.level, d0, d2, d1, livingEntity, pose);
				if (vector3d1 != null) {
					livingEntity.setPose(pose);
					return vector3d1;
				}

				Vector3d vector3d2 = TransportationHelper.findDismountLocation(this.level, d0, d3, d1, livingEntity, pose);
				if (vector3d2 != null) {
					livingEntity.setPose(pose);
					return vector3d2;
				}
			}
		}

		return super.getDismountLocationForPassenger(livingEntity);
	}

	protected void clampRotation(Entity entity) {
		entity.setYBodyRot(this.yRot);
		float f = MathHelper.wrapDegrees(entity.yRot - this.yRot);
		float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
		entity.yRotO += f1 - f;
		entity.yRot += f1 - f;
		entity.setYHeadRot(entity.yRot);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onPassengerTurned(@Nonnull Entity entity) {
		this.clampRotation(entity);
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt) {
		nbt.putString("DyeColor", this.getBedColor().getName());
		nbt.putString("Type", this.getBoatType().getName());
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT nbt) {
		if (nbt.contains("Type", 8)) {
			this.setType(BoatEntity.Type.byName(nbt.getString("Type")));
		}
		if (nbt.contains("DyeColor", 8)) {
			this.setColor(DyeColor.byName(nbt.getString("DyeColor"), DyeColor.WHITE));
		}
	}

	@Override @Nonnull
	public ActionResultType interact(PlayerEntity player, @Nonnull Hand hand) {
		if (player.isSecondaryUseActive()) {
			return ActionResultType.PASS;
		}
		if (this.outOfControlTicks < 60.0F) {
			if (!this.level.isClientSide) {
				return player.startRiding(this) ? ActionResultType.CONSUME : ActionResultType.PASS;
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, @Nonnull BlockState state, @Nonnull BlockPos pos) {
		this.lastYd = this.getDeltaMovement().y;
		if (!this.isPassenger()) {
			if (onGround) {
				if (this.fallDistance > 3.0F) {
					if (this.status != BoatEntity.Status.ON_LAND) {
						this.fallDistance = 0.0F;
						return;
					}

					this.causeFallDamage(this.fallDistance, 1.0F);
					if (!this.level.isClientSide && !this.removed) {
						this.remove();
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
		return MathHelper.lerp(rate, this.bubbleAngleO, this.bubbleAngle);
	}

	public void setHurtDir(int hurtDir) {
		this.entityData.set(DATA_ID_HURT_DIR, hurtDir);
	}

	public int getHurtDir() {
		return this.entityData.get(DATA_ID_HURT_DIR);
	}

	public void setType(BoatEntity.Type type) {
		this.entityData.set(DATA_ID_TYPE, type.ordinal());
	}

	public BoatEntity.Type getBoatType() {
		return BoatEntity.Type.byId(this.entityData.get(DATA_ID_TYPE));
	}

	public void setColor(DyeColor color) {
		this.entityData.set(DATA_ID_DYE_COLOR, color.ordinal());
	}

	public DyeColor getBedColor() {
		return DyeColor.byId(this.entityData.get(DATA_ID_DYE_COLOR));
	}

	@Override
	protected boolean canAddPassenger(@Nonnull Entity entity) {
		return this.getPassengers().size() < 1 && !this.isEyeInFluid(FluidTags.WATER);
	}

	@Override @Nullable
	public Entity getControllingPassenger() {
		List<Entity> list = this.getPassengers();
		return list.isEmpty() ? null : list.get(0);
	}

	@OnlyIn(Dist.CLIENT)
	public void setInput(boolean inputLeft, boolean inputRight, boolean inputUp, boolean inputDown) {
		this.inputLeft = inputLeft;
		this.inputRight = inputRight;
		this.inputUp = inputUp;
		this.inputDown = inputDown;
	}

	@Override @Nonnull
	public IPacket<?> getAddEntityPacket() {
		return new SSpawnObjectPacket(this);
	}

	@Override
	public boolean isUnderWater() {
		return this.status == BoatEntity.Status.UNDER_WATER || this.status == BoatEntity.Status.UNDER_FLOWING_WATER;
	}

	@Override
	protected void addPassenger(@Nonnull Entity passenger) {
		super.addPassenger(passenger);
		if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
			this.lerpSteps = 0;
			this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float)this.lerpYRot, (float)this.lerpXRot);
		}
	}
}
