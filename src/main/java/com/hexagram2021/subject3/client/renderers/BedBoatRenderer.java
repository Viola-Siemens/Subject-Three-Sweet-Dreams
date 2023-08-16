package com.hexagram2021.subject3.client.renderers;

import com.google.common.collect.ImmutableMap;
import com.hexagram2021.subject3.client.layers.BoatBedLayer;
import com.hexagram2021.subject3.client.models.BedBoatModel;
import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

import java.util.Map;
import java.util.stream.Stream;

import static com.hexagram2021.subject3.Subject3.MODID;

public class BedBoatRenderer extends EntityRenderer<BedBoatEntity> implements RenderLayerParent<BedBoatEntity, BedBoatModel> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(MODID, "bed_boat"), "main");

	private final RenderLayer<BedBoatEntity, BedBoatModel> bedLayer;
	private final Map<Boat.Type, Pair<ResourceLocation, BedBoatModel>> boatResources;
	private final BedBoatModel defaultModel;

	public BedBoatRenderer(EntityRendererProvider.Context manager) {
		super(manager);
		this.shadowRadius = 0.8F;
		this.bedLayer = new BoatBedLayer(this);
		this.boatResources = Stream.of(Boat.Type.values()).collect(ImmutableMap.toImmutableMap(
				type -> type,
				type -> Pair.of(
						new ResourceLocation("textures/entity/boat/" + type.getName() + ".png"),
						new BedBoatModel(manager.bakeLayer(ModelLayers.createBoatModelName(type)))
				)
		));
		this.defaultModel = new BedBoatModel(manager.bakeLayer(LAYER_LOCATION));
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
			transform.mulPose(new Quaternionf().setAngleAxis(bedBoatEntity.getBubbleAngle(ticks) * ((float)Math.PI / 180F), 1.0F, 0.0F, 1.0F));
		}

		Pair<ResourceLocation, BedBoatModel> pair = this.getModelWithLocation(bedBoatEntity);
		ResourceLocation resourcelocation = pair.getFirst();
		BedBoatModel model = pair.getSecond();
		transform.scale(-1.0F, -1.0F, 1.0F);
		transform.mulPose(Axis.YP.rotationDegrees(90.0F));
		model.setupAnim(bedBoatEntity, ticks, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer vertexConsumer = buffer.getBuffer(model.renderType(resourcelocation));
		model.renderToBuffer(transform, vertexConsumer, h, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		if (!bedBoatEntity.isUnderWater()) {
			VertexConsumer builder = buffer.getBuffer(RenderType.waterMask());
			model.waterPatch().render(transform, builder, h, OverlayTexture.NO_OVERLAY);
		}

		this.bedLayer.render(transform, buffer, h, bedBoatEntity, 0.0F, 0.0F, ticks, 0.0F, 0.0F, Mth.lerp(ticks, bedBoatEntity.xRotO, bedBoatEntity.getXRot()));

		transform.popPose();
		super.render(bedBoatEntity, y, ticks, transform, buffer, h);
	}

	public Pair<ResourceLocation, BedBoatModel> getModelWithLocation(BedBoatEntity boat) { return this.boatResources.get(boat.getVariant()); }

	@Override
	public ResourceLocation getTextureLocation(BedBoatEntity boat) {
		return this.getModelWithLocation(boat).getFirst();
	}

	@Override
	public BedBoatModel getModel() {
		return this.defaultModel;
	}
}
