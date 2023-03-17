package com.hexagram2021.subject3.mixin;

import com.hexagram2021.subject3.common.entities.IBedVehicle;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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
			Vector3d direction = Vector3d.directionFromRotation(0.0F, ((IBedVehicle)entity.getVehicle()).getBedVehicleRotY()).reverse();
			double movement = -entity.getBbHeight() / 2.0D;
			transform.translate(movement * direction.x(), ((IBedVehicle)entity.getVehicle()).getBedVehicleOffsetY() - entity.getMyRidingOffset(), movement * direction.z());
		}
	}

	@Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/EntityModel;setupAnim(Lnet/minecraft/entity/Entity;FFFFF)V"))
	private <E extends Entity, EM extends EntityModel<E>> void st_disableAnimIfOnBedVehicles(EM instance, E entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if(entity.getVehicle() instanceof IBedVehicle) {
			instance.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}
		instance.setupAnim(entity, 0.0F, 0.0F, ageInTicks, netHeadYaw, headPitch);
	}

	@Redirect(method = "setupRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPose()Lnet/minecraft/entity/Pose;"))
	private Pose st_setupRotationsLayOnBedVehicle(T instance) {
		if (instance.getVehicle() instanceof IBedVehicle) {
			return Pose.SLEEPING;
		}
		return instance.getPose();
	}

	@Inject(method = "setupRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getBedOrientation()Lnet/minecraft/util/Direction;", shift = At.Shift.AFTER))
	public void st_setupRotationsLayRotation(T entity, MatrixStack transform, float bob, float bodyYRot, float ticks, CallbackInfo ci) {
		if (entity.getVehicle() instanceof IBedVehicle) {
			transform.mulPose(Vector3f.YP.rotationDegrees(270.0F - ((IBedVehicle)entity.getVehicle()).getBedVehicleRotY()));
		}
	}
}
