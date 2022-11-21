package com.hexagram202.subject3.common.entities;

import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityBedBoat extends EntityBoat implements IBedVehicle {
    private static final DataParameter<Integer> DATA_ID_DYE_COLOR = EntityDataManager.createKey(EntityBedBoat.class, DataSerializers.VARINT);

    public EntityBedBoat(World level) {
        super(level);
        //this.blocksBuilding = true;
    }

    public EntityBedBoat(World level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(DATA_ID_DYE_COLOR, 0);
    }

    public void setColor(DyeColor color) {
        this.dataManager.set(DATA_ID_DYE_COLOR, color.ordinal());
    }

    @Override
    public DyeColor getBedColor() {
        return DyeColor.byId(this.entityData.get(DATA_ID_DYE_COLOR));
    }

    @Override
    public int passengersCount() {
        return this.getPassengers().size();
    }

    @Override
    public float getBedVehicleRotY() {
        return this.getYRot();
    }
    @Override
    public double getBedVehicleOffsetY() {
        if(this.getVariant() == Type.BAMBOO) {
            return 0.75D;
        }
        return 0.875D;
    }
\

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(p_70107_1_, p_70107_3_, p_70107_5_);
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);

        if(this.level() instanceof ServerLevel serverlevel) {
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
    protected int getMaxPassengers() {
        return 1;
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
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!BedBlock.canSetSpawn(this.level())) {
            if (this.level().isClientSide) {
                return InteractionResult.SUCCESS;
            }
            Vec3 vec3 = new Vec3(this.getX() + 0.5D, this.getY() + 0.125D, this.getZ() + 0.5D);
            this.level().explode(
                    this, this.damageSources().badRespawnPointExplosion(vec3), null,
                    vec3, 5.0F, true, Level.ExplosionInteraction.BLOCK
            );
            this.kill();
            return InteractionResult.CONSUME;
        }
        InteractionResult ret = super.interact(player, hand);
        if(ret == InteractionResult.CONSUME && player instanceof IHasVehicleRespawnPosition) {
            ((IHasVehicleRespawnPosition)player).setBedVehicleUUID(this.uuid);
        }
        return ret;
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide && reason.shouldDestroy()) {
            ChunkPos chunkPos = STSavedData.removeBedVehicle(this.uuid);
            if(chunkPos != null && this.level() instanceof ServerLevel) {
                STSavedData.updateForceChunk(chunkPos, (ServerLevel)this.level(), false);
            }
        }

        super.remove(reason);
    }

    @Override
    public Item getDropItem() {
        return STItems.BedBoats.byTypeAndColor(this.getVariant(), this.getBedColor());
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
