package com.hexagram2021.subject3.mixin;

import com.hexagram2021.subject3.common.entities.IBedVehicle;
import com.hexagram2021.subject3.common.entities.IHasVehicleRespawnPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "getBedOrientation", at = @At(value = "HEAD"), cancellable = true)
	public void getBedVehicleOrientation(CallbackInfoReturnable<Direction> cir) {
		if(this instanceof IHasVehicleRespawnPosition) {
			IHasVehicleRespawnPosition player = (IHasVehicleRespawnPosition)this;
			if(player.getRidingBedVehicle() != null) {
				cir.setReturnValue(Direction.fromYRot(((Entity) player.getRidingBedVehicle()).yRot));
				cir.cancel();
			}
		}
	}

	@Inject(method = "stopRiding", at = @At(value = "HEAD"))
	public void stopRidingBedVehicle(CallbackInfo ci) {
		if(this instanceof IHasVehicleRespawnPosition) {
			Entity vehicle = ((LivingEntity)(Object)this).getVehicle();
			if(vehicle instanceof IBedVehicle) {
				IHasVehicleRespawnPosition player = (IHasVehicleRespawnPosition) this;
				player.setRidingBedVehicleUUID(null);
			}
		}
	}
}
