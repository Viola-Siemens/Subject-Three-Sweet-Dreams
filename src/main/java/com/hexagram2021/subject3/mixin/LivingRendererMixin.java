package com.hexagram2021.subject3.mixin;

import com.hexagram2021.subject3.common.entities.IBedVehicle;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
@Mixin(LivingRenderer.class)
public class LivingRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
	@Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPose()Lnet/minecraft/entity/Pose;"))
	private Pose st_renderLayOnBedVehicle(T instance) {
		if (instance.getVehicle() instanceof IBedVehicle) {
			return Pose.SLEEPING;
		}
		return instance.getPose();
	}

	@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;translate(DDD)V", ordinal = 0, shift = At.Shift.AFTER))
	public void st_renderLayHeightPosition(T entity, float y, float ticks, MatrixStack transform, @Nonnull IRenderTypeBuffer buffer, int h, CallbackInfo ci) {
		if (entity.getVehicle() instanceof IBedVehicle) {
			Direction direction = Direction.fromYRot(((IBedVehicle)entity.getVehicle()).getBedVehicleRotY()).getOpposite();
			double movement = entity.getEyeHeight(Pose.STANDING) - 0.1D;
			transform.translate(movement * direction.getStepX(), ((IBedVehicle)entity.getVehicle()).getBedVehicleOffsetY(), movement * direction.getStepZ());
		}
	}

	@Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getBedOrientation()Lnet/minecraft/util/Direction;"))
	private Direction st_renderGetBedOrientation(LivingEntity instance) {
		if (instance.getVehicle() instanceof IBedVehicle) {
			return Direction.fromYRot(((IBedVehicle)instance.getVehicle()).getBedVehicleRotY()).getOpposite();
		}
		return instance.getBedOrientation();
	}

	@Redirect(method = "setupRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPose()Lnet/minecraft/entity/Pose;"))
	private Pose st_setupRotationsLayOnBedVehicle(T instance) {
		if (instance.getVehicle() instanceof IBedVehicle) {
			return Pose.SLEEPING;
		}
		return instance.getPose();
	}

	@Redirect(method = "setupRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getBedOrientation()Lnet/minecraft/util/Direction;"))
	private Direction st_setupRotationsGetBedOrientation(LivingEntity instance) {
		if (instance.getVehicle() instanceof IBedVehicle) {
			return Direction.fromYRot(((IBedVehicle)instance.getVehicle()).getBedVehicleRotY()).getOpposite();
		}
		return instance.getBedOrientation();
	}
}
