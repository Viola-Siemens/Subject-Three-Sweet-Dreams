package com.hexagram2021.subject3.client.models;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class BedBoatModel extends AbstractBedBoatModel implements WaterPatchModel {
	private final ModelPart waterPatch;
	private final ImmutableList<ModelPart> parts;

	public BedBoatModel(ModelPart root) {
		super(root);
		this.waterPatch = root.getChild("water_patch");

		this.parts = ImmutableList.of(
				this.bottom, root.getChild("back"), root.getChild("front"),
				root.getChild("left"), root.getChild("right"),
				this.leftPaddle, this.rightPaddle
		);
	}

	public static LayerDefinition createBodyModel() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();
		root.addOrReplaceChild("bottom",
				CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
				PartPose.offsetAndRotation(0.0F, 3.0F, 1.0F, Mth.HALF_PI, 0.0F, 0.0F));
		root.addOrReplaceChild("back",
				CubeListBuilder.create().texOffs(0, 19).addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F),
				PartPose.offsetAndRotation(-15.0F, 4.0F, 4.0F, 0.0F, Mth.PI * 1.5F, 0.0F));
		root.addOrReplaceChild("front",
				CubeListBuilder.create().texOffs(0, 27).addBox(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F),
				PartPose.offsetAndRotation(15.0F, 4.0F, 0.0F, 0.0F, Mth.HALF_PI, 0.0F));
		root.addOrReplaceChild("right",
				CubeListBuilder.create().texOffs(0, 35).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F),
				PartPose.offsetAndRotation(0.0F, 4.0F, -9.0F, 0.0F, Mth.PI, 0.0F));
		root.addOrReplaceChild("left",
				CubeListBuilder.create().texOffs(0, 43).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F),
				PartPose.offset(0.0F, 4.0F, 9.0F));
		root.addOrReplaceChild("left_paddle",
				CubeListBuilder.create().texOffs(62, 0).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
				PartPose.offsetAndRotation(3.0F, -5.0F, 9.0F, 0.0F, 0.0F, Mth.PI / 16F));
		root.addOrReplaceChild("right_paddle",
				CubeListBuilder.create().texOffs(62, 20).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
				PartPose.offsetAndRotation(3.0F, -5.0F, -9.0F, 0.0F, Mth.PI, Mth.PI / 16F));
		root.addOrReplaceChild("water_patch",
				CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
				PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, Mth.HALF_PI, 0.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(BedBoatEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		animatePaddle(entity, 0, this.leftPaddle, limbSwing);
		animatePaddle(entity, 1, this.rightPaddle, limbSwing);
	}

	@Override
	public Iterable<ModelPart> parts() {
		return this.parts;
	}

	public ModelPart waterPatch() {
		return this.waterPatch;
	}

	private static void animatePaddle(BedBoatEntity boat, int index, ModelPart paddle, float limbSwing) {
		float f = boat.getRowingTime(index, limbSwing);
		paddle.xRot = Mth.clampedLerp(-Mth.PI / 3.0F, -Mth.PI / 12F, (Mth.sin(-f) + 1.0F) / 2.0F);
		paddle.yRot = Mth.clampedLerp(-Mth.PI / 4.0F, Mth.PI / 4.0F, (Mth.sin(-f + 1.0F) + 1.0F) / 2.0F);
		if (index == 1) {
			paddle.yRot = Mth.PI - paddle.yRot;
		}
	}
}
