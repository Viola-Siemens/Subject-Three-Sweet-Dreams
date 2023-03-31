package com.hexagram2021.subject3.mixin;

import com.hexagram2021.subject3.common.entities.IHasVehicleRespawnPosition;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin implements IHasVehicleRespawnPosition {
	@Nullable
	private UUID respawnBedVehicle;

	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	public void readBedVehicleRespawnUUID(CompoundTag nbt, CallbackInfo ci) {
		if(nbt.contains("BedVehicle", Tag.TAG_INT_ARRAY)) {
			this.respawnBedVehicle = nbt.getUUID("BedVehicle");
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	public void addBedVehicleRespawnUUID(CompoundTag nbt, CallbackInfo ci) {
		if(this.respawnBedVehicle != null) {
			nbt.putUUID("BedVehicle", this.respawnBedVehicle);
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
			((ServerPlayer)(Object)this).sendMessage(new TranslatableComponent("block.minecraft.set_spawn"), Util.NIL_UUID);
		}
	}
}
