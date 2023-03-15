package com.hexagram2021.subject3.mixin;

import com.hexagram2021.subject3.common.entities.IBedVehicle;
import com.hexagram2021.subject3.common.entities.IHasVehicleRespawnPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IHasVehicleRespawnPosition {
	@Nullable
	private UUID respawnBedVehicle;

	@Nullable
	private IBedVehicle ridingBedVehicle;

	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	public void readBedVehicleRespawnUUID(CompoundNBT nbt, CallbackInfo ci) {
		if(nbt.contains("BedVehicle", Constants.NBT.TAG_INT_ARRAY)) {
			this.respawnBedVehicle = nbt.getUUID("BedVehicle");
		}
		if(nbt.contains("RidingBedVehicle", Constants.NBT.TAG_INT_ARRAY)) {
			Entity entity = ((ServerWorld)((ServerPlayerEntity)(Object)this).level).getEntity(nbt.getUUID("RidingBedVehicle"));
			if(entity instanceof IBedVehicle) {
				this.ridingBedVehicle = (IBedVehicle) entity;
			}
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	public void addBedVehicleRespawnUUID(CompoundNBT nbt, CallbackInfo ci) {
		if(this.respawnBedVehicle != null) {
			nbt.putUUID("BedVehicle", this.respawnBedVehicle);
		}
		if(this.ridingBedVehicle != null) {
			nbt.putUUID("RidingBedVehicle", ((Entity)this.ridingBedVehicle).getUUID());
		}
	}

	@Override @Nullable
	public UUID getBedVehicleUUID() {
		return this.respawnBedVehicle;
	}

	@Override
	public void setBedVehicleUUID(@Nullable UUID uuid) {
		if(!Objects.equals(uuid, this.respawnBedVehicle)) {
			this.respawnBedVehicle = uuid;
			((ServerPlayerEntity)(Object)this).sendMessage(new TranslationTextComponent("block.minecraft.set_spawn"), Util.NIL_UUID);
		}
	}

	@Override @Nullable
	public IBedVehicle getRidingBedVehicle() {
		return this.ridingBedVehicle;
	}

	@Override
	public void setRidingBedVehicleUUID(@Nullable IBedVehicle entity) {
		this.ridingBedVehicle = entity;
	}
}
