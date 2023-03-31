package com.hexagram2021.subject3.mixin;

import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin {
	@Shadow
	private boolean handsBusy;

	@Inject(method = "startRiding", at = @At(value = "RETURN", ordinal = 1, shift = At.Shift.BEFORE))
	private void handleStartRidingBedBoat(Entity entity, boolean update, CallbackInfoReturnable<Boolean> cir) {
		LocalPlayer player = (LocalPlayer)(Object)this;
		if (entity instanceof BedBoatEntity) {
			player.yRotO = entity.getYRot();
			player.setYRot(entity.getYRot());
			player.setYHeadRot(entity.getYRot());
		}
	}

	@Inject(method = "rideTick", at = @At(value = "TAIL"))
	private void handleRideBedBoat(CallbackInfo ci) {
		LocalPlayer player = (LocalPlayer)(Object)this;
		if (player.getVehicle() instanceof BedBoatEntity bedBoat) {
			bedBoat.setInput(player.input.left, player.input.right, player.input.up, player.input.down);
			this.handsBusy |= player.input.left || player.input.right || player.input.up || player.input.down;
		}
	}
}
