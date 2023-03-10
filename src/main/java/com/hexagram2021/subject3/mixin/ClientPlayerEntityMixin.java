package com.hexagram2021.subject3.mixin;

import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
	@Shadow
	private boolean handsBusy;

	@Inject(method = "startRiding", at = @At(value = "RETURN", ordinal = 1, shift = At.Shift.BEFORE))
	private void handleStartRidingBedBoat(Entity entity, boolean update, CallbackInfoReturnable<Boolean> cir) {
		ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
		if (entity instanceof BedBoatEntity) {
			player.yRotO = entity.yRot;
			player.yRot = entity.yRot;
			player.setYHeadRot(entity.yRot);
		}
	}

	@Inject(method = "rideTick", at = @At(value = "TAIL"))
	private void handleRideBedBoat(CallbackInfo ci) {
		ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
		if (player.getVehicle() instanceof BedBoatEntity) {
			BedBoatEntity bedBoat = (BedBoatEntity)player.getVehicle();
			bedBoat.setInput(player.input.left, player.input.right, player.input.up, player.input.down);
			this.handsBusy |= player.input.left || player.input.right || player.input.up || player.input.down;
		}
	}
}
