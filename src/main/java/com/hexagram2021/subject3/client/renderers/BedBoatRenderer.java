package com.hexagram2021.subject3.client.renderers;

import com.hexagram2021.subject3.client.layers.BoatBedLayer;
import com.hexagram2021.subject3.client.models.BedBoatModel;
import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class BedBoatRenderer extends EntityRenderer<BedBoatEntity> implements IEntityRenderer<BedBoatEntity, BedBoatModel> {
	private final LayerRenderer<BedBoatEntity, BedBoatModel> bedLayer;

	private static final ResourceLocation[] BOAT_TEXTURE_LOCATIONS = new ResourceLocation[]{
			new ResourceLocation("textures/entity/boat/oak.png"),
			new ResourceLocation("textures/entity/boat/spruce.png"),
			new ResourceLocation("textures/entity/boat/birch.png"),
			new ResourceLocation("textures/entity/boat/jungle.png"),
			new ResourceLocation("textures/entity/boat/acacia.png"),
			new ResourceLocation("textures/entity/boat/dark_oak.png")
	};
	protected final BedBoatModel model = new BedBoatModel();

	public BedBoatRenderer(EntityRendererManager manager) {
		super(manager);
		this.shadowRadius = 0.8F;
		this.bedLayer = new BoatBedLayer(this);
	}

	@Override
	public void render(BedBoatEntity bedBoatEntity, float y, float ticks, MatrixStack transform, @Nonnull IRenderTypeBuffer buffer, int h) {
		transform.pushPose();
		transform.translate(0.0D, 0.375D, 0.0D);
		transform.mulPose(Vector3f.YP.rotationDegrees(180.0F - y));
		float f = (float)bedBoatEntity.getHurtTime() - ticks;
		float f1 = bedBoatEntity.getDamage() - ticks;
		if (f1 < 0.0F) {
			f1 = 0.0F;
		}

		if (f > 0.0F) {
			transform.mulPose(Vector3f.XP.rotationDegrees(MathHelper.sin(f) * f * f1 / 10.0F * (float)bedBoatEntity.getHurtDir()));
		}

		float f2 = bedBoatEntity.getBubbleAngle(ticks);
		if (!MathHelper.equal(f2, 0.0F)) {
			transform.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), bedBoatEntity.getBubbleAngle(ticks), true));
		}

		transform.scale(-1.0F, -1.0F, 1.0F);
		transform.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		this.model.setupAnim(bedBoatEntity, ticks, 0.0F, -0.1F, 0.0F, 0.0F);
		IVertexBuilder ivertexbuilder = buffer.getBuffer(this.model.renderType(this.getTextureLocation(bedBoatEntity)));
		this.model.renderToBuffer(transform, ivertexbuilder, h, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		if (!bedBoatEntity.isUnderWater()) {
			IVertexBuilder builder = buffer.getBuffer(RenderType.waterMask());
			this.model.waterPatch().render(transform, builder, h, OverlayTexture.NO_OVERLAY);
		}

		this.bedLayer.render(transform, buffer, h, bedBoatEntity, 0.0F, 0.0F, ticks, 0.0F, 0.0F, MathHelper.lerp(ticks, bedBoatEntity.xRotO, bedBoatEntity.xRot));

		transform.popPose();
		super.render(bedBoatEntity, y, ticks, transform, buffer, h);
	}

	@Override @Nonnull
	public BedBoatModel getModel() {
		return this.model;
	}

	@Override @Nonnull
	public ResourceLocation getTextureLocation(BedBoatEntity entity) {
		return BOAT_TEXTURE_LOCATIONS[entity.getBoatType().ordinal()];
	}
}
