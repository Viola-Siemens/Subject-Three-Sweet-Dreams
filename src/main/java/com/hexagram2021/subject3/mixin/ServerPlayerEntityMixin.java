package com.hexagram2021.subject3.mixin;

import com.hexagram2021.subject3.common.entities.IHasVehicleRespawnPosition;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
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
	private UUID bedVehicle;

	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	public void readBedVehicleRespawnUUID(CompoundNBT nbt, CallbackInfo ci) {
		if(nbt.contains("BedVehicle", Constants.NBT.TAG_INT_ARRAY)) {
			this.bedVehicle = nbt.getUUID("BedVehicle");
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	public void addBedVehicleRespawnUUID(CompoundNBT nbt, CallbackInfo ci) {
		if(this.bedVehicle != null) {
			nbt.putUUID("BedVehicle", this.bedVehicle);
		}
	}

	@Override
	public UUID getBedVehicleUUID() {
		return this.bedVehicle;
	}

	@Override
	public void setBedVehicleUUID(UUID uuid) {
		if(!Objects.equals(uuid, this.bedVehicle)) {
			this.bedVehicle = uuid;
			((ServerPlayerEntity)(Object)this).sendMessage(new TranslationTextComponent("block.minecraft.set_spawn"), Util.NIL_UUID);
		}
	}
}
