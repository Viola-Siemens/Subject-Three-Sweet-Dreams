package com.hexagram2021.subject3.mixin;

import com.hexagram2021.subject3.common.entities.IBedVehicle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(LivingEntityRenderer.class)
public class LivingRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
	@Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasPose(Lnet/minecraft/world/entity/Pose;)Z"))
	private boolean st_renderLayOnBedVehicle(T instance, Pose pose) {
		if (instance.getVehicle() instanceof IBedVehicle) {
			return Pose.SLEEPING == pose;
		}
		return instance.getPose() == pose;
	}

	@Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 0, shift = At.Shift.AFTER))
	public void st_renderLayHeightPosition(T entity, float y, float ticks, PoseStack transform, MultiBufferSource buffer, int h, CallbackInfo ci) {
		if (entity.getVehicle() instanceof IBedVehicle) {
			Vec3 direction = Vec3.directionFromRotation(0.0F, ((IBedVehicle)entity.getVehicle()).getBedVehicleRotY()).reverse();
			double movement = -entity.getBbHeight() / 2.0D;
			transform.translate(movement * direction.x(), ((IBedVehicle)entity.getVehicle()).getBedVehicleOffsetY() - entity.getMyRidingOffset(), movement * direction.z());
		}
	}

	@Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"))
	private <E extends Entity, EM extends EntityModel<E>> void st_disableAnimIfOnBedVehicles(EM instance, E entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if(entity.getVehicle() instanceof IBedVehicle) {
			instance.setupAnim(entity, 0.0F, 0.0F, ageInTicks, netHeadYaw, headPitch);
		} else {
			instance.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}
	}

	@Redirect(method = "setupRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasPose(Lnet/minecraft/world/entity/Pose;)Z"))
	private boolean st_setupRotationsLayOnBedVehicle(T instance, Pose pose) {
		if (instance.getVehicle() instanceof IBedVehicle) {
			return Pose.SLEEPING == pose;
		}
		return instance.getPose() == pose;
	}

	@Inject(method = "setupRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getBedOrientation()Lnet/minecraft/core/Direction;", shift = At.Shift.AFTER))
	public void st_setupRotationsLayRotation(T entity, PoseStack transform, float bob, float bodyYRot, float ticks, CallbackInfo ci) {
		if (entity.getVehicle() instanceof IBedVehicle) {
			transform.mulPose(Axis.YP.rotationDegrees(270.0F - ((IBedVehicle)entity.getVehicle()).getBedVehicleRotY()));
		}
	}
}
