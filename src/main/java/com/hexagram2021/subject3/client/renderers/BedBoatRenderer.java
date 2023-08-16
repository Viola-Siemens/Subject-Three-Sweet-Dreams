package com.hexagram2021.subject3.client.renderers;

import com.google.common.collect.ImmutableMap;
import com.hexagram2021.subject3.client.ClientEntityEventSubscriber;
import com.hexagram2021.subject3.client.layers.BoatBedLayer;
import com.hexagram2021.subject3.client.models.AbstractBedBoatModel;
import com.hexagram2021.subject3.client.models.BedBoatModel;
import com.hexagram2021.subject3.client.models.BedRaftModel;
import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

import java.util.Map;
import java.util.stream.Stream;

public class BedBoatRenderer extends EntityRenderer<BedBoatEntity> {
	private final BoatBedLayer bedLayer;
	private final Map<Boat.Type, Pair<ResourceLocation, AbstractBedBoatModel>> boatResources;

	public BedBoatRenderer(EntityRendererProvider.Context manager) {
		super(manager);
		this.shadowRadius = 0.8F;
		this.bedLayer = new BoatBedLayer(this);
		this.boatResources = Stream.of(Boat.Type.values()).collect(ImmutableMap.toImmutableMap(
				type -> type,
				type -> {
					if(type == Boat.Type.BAMBOO) {
						return Pair.of(
								new ResourceLocation("textures/entity/boat/" + type.getName() + ".png"),
								new BedRaftModel(manager.bakeLayer(ClientEntityEventSubscriber.createBedBoatModelName(type)))
						);
					}
					return Pair.of(
							new ResourceLocation("textures/entity/boat/" + type.getName() + ".png"),
							new BedBoatModel(manager.bakeLayer(ClientEntityEventSubscriber.createBedBoatModelName(type)))
					);
				}
		));
	}

	@Override
	public void render(BedBoatEntity bedBoatEntity, float y, float ticks, PoseStack transform, MultiBufferSource buffer, int h) {
		transform.pushPose();
		transform.translate(0.0D, 0.375D, 0.0D);
		transform.mulPose(Axis.YP.rotationDegrees(180.0F - y));
		float f = (float)bedBoatEntity.getHurtTime() - ticks;
		float f1 = bedBoatEntity.getDamage() - ticks;
		if (f1 < 0.0F) {
			f1 = 0.0F;
		}

		if (f > 0.0F) {
			transform.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float)bedBoatEntity.getHurtDir()));
		}

		float f2 = bedBoatEntity.getBubbleAngle(ticks);
		if (!Mth.equal(f2, 0.0F)) {
			transform.mulPose(new Quaternionf().setAngleAxis(bedBoatEntity.getBubbleAngle(ticks) * (Mth.PI / 180F), 1.0F, 0.0F, 1.0F));
		}

		Pair<ResourceLocation, AbstractBedBoatModel> pair = this.getModelWithLocation(bedBoatEntity);
		ResourceLocation resourcelocation = pair.getFirst();
		AbstractBedBoatModel model = pair.getSecond();
		transform.scale(-1.0F, -1.0F, 1.0F);
		transform.mulPose(Axis.YP.rotationDegrees(90.0F));
		model.setupAnim(bedBoatEntity, ticks, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer vertexConsumer = buffer.getBuffer(model.renderType(resourcelocation));
		model.renderToBuffer(transform, vertexConsumer, h, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		if (!bedBoatEntity.isUnderWater()) {
			VertexConsumer builder = buffer.getBuffer(RenderType.waterMask());
			if (model instanceof WaterPatchModel waterPatchModel) {
				waterPatchModel.waterPatch().render(transform, builder, h, OverlayTexture.NO_OVERLAY);
			}
		}

		this.bedLayer.render(transform, buffer, h, bedBoatEntity);

		transform.popPose();
		super.render(bedBoatEntity, y, ticks, transform, buffer, h);
	}

	public Pair<ResourceLocation, AbstractBedBoatModel> getModelWithLocation(BedBoatEntity boat) { return this.boatResources.get(boat.getVariant()); }

	@Override
	public ResourceLocation getTextureLocation(BedBoatEntity boat) {
		return this.getModelWithLocation(boat).getFirst();
	}

	public AbstractBedBoatModel getModel(BedBoatEntity boat) {
		return this.boatResources.get(boat.getVariant()).getSecond();
	}
}
