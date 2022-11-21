package com.hexagram2021.subject3.client.models;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class BedBoatModel extends SegmentedModel<BedBoatEntity> {
	private final ModelRenderer[] paddles = new ModelRenderer[2];
	private final ModelRenderer waterPatch;
	private final ImmutableList<ModelRenderer> parts;

	public BedBoatModel() {
		ModelRenderer[] modelRenderers = new ModelRenderer[]{
				new ModelRenderer(this, 0, 0).setTexSize(128, 64),
				new ModelRenderer(this, 0, 19).setTexSize(128, 64),
				new ModelRenderer(this, 0, 27).setTexSize(128, 64),
				new ModelRenderer(this, 0, 35).setTexSize(128, 64),
				new ModelRenderer(this, 0, 43).setTexSize(128, 64)
		};
		modelRenderers[0].addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
		modelRenderers[0].setPos(0.0F, 3.0F, 1.0F);
		modelRenderers[1].addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, 0.0F);
		modelRenderers[1].setPos(-15.0F, 4.0F, 4.0F);
		modelRenderers[2].addBox(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F, 0.0F);
		modelRenderers[2].setPos(15.0F, 4.0F, 0.0F);
		modelRenderers[3].addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
		modelRenderers[3].setPos(0.0F, 4.0F, -9.0F);
		modelRenderers[4].addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
		modelRenderers[4].setPos(0.0F, 4.0F, 9.0F);
		modelRenderers[0].xRot = ((float)Math.PI / 2.0F);
		modelRenderers[1].yRot = ((float)Math.PI * 1.5F);
		modelRenderers[2].yRot = ((float)Math.PI / 2.0F);
		modelRenderers[3].yRot = (float)Math.PI;
		this.paddles[0] = this.makePaddle(true);
		this.paddles[0].setPos(3.0F, -5.0F, 9.0F);
		this.paddles[1] = this.makePaddle(false);
		this.paddles[1].setPos(3.0F, -5.0F, -9.0F);
		this.paddles[1].yRot = (float)Math.PI;
		this.paddles[0].zRot = 0.19634955F;
		this.paddles[1].zRot = 0.19634955F;
		this.waterPatch = new ModelRenderer(this, 0, 0).setTexSize(128, 64);
		this.waterPatch.addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
		this.waterPatch.setPos(0.0F, -3.0F, 1.0F);
		this.waterPatch.xRot = (float)Math.PI / 2.0F;
		ImmutableList.Builder<ModelRenderer> builder = ImmutableList.builder();
		builder.addAll(Arrays.asList(modelRenderers));
		builder.addAll(Arrays.asList(this.paddles));
		this.parts = builder.build();
	}

	@Override
	public void setupAnim(@Nonnull BedBoatEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.animatePaddle(entity, 0, limbSwing);
		this.animatePaddle(entity, 1, limbSwing);
	}

	@Override @Nonnull
	public ImmutableList<ModelRenderer> parts() {
		return this.parts;
	}

	public ModelRenderer waterPatch() {
		return this.waterPatch;
	}

	protected ModelRenderer makePaddle(boolean index) {
		ModelRenderer modelrenderer = (new ModelRenderer(this, 62, index ? 0 : 20)).setTexSize(128, 64);
		modelrenderer.addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F);
		modelrenderer.addBox(index ? -1.001F : 0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F);
		return modelrenderer;
	}

	protected void animatePaddle(BedBoatEntity bedBoatEntity, int index, float time) {
		float f = bedBoatEntity.getRowingTime(index, time);
		ModelRenderer modelrenderer = this.paddles[index];
		modelrenderer.xRot = (float) MathHelper.clampedLerp(-Math.PI / 3.0D, -0.2617994D, (MathHelper.sin(-f) + 1.0D) / 2.0D);
		modelrenderer.yRot = (float)MathHelper.clampedLerp(-Math.PI / 4.0D, Math.PI / 4.0D, (MathHelper.sin(-f + 1.0F) + 1.0D) / 2.0D);
		if (index == 1) {
			modelrenderer.yRot = (float)Math.PI - modelrenderer.yRot;
		}
	}
}
