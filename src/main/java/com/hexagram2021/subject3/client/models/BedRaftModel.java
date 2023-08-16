package com.hexagram2021.subject3.client.models;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class BedRaftModel extends AbstractBedBoatModel {
	private final ImmutableList<ModelPart> parts;

	public BedRaftModel(ModelPart root) {
		super(root);

		this.parts = ImmutableList.of(this.bottom, this.leftPaddle, this.rightPaddle);
	}

	public static LayerDefinition createBodyModel() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();
		root.addOrReplaceChild("bottom",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-14.0F, -11.0F, -3.0F, 28.0F, 20.0F, 4.0F)
						.texOffs(0, 0).addBox(-14.0F, -9.0F, -8.0F, 28.0F, 16.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, Mth.HALF_PI, 0.0F, 0.0F));
		root.addOrReplaceChild("left_paddle",
				CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
				PartPose.offsetAndRotation(3.0F, -5.0F, 9.0F, 0.0F, 0.0F, Mth.PI / 16F));
		root.addOrReplaceChild("right_paddle",
				CubeListBuilder.create().texOffs(40, 24).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
				PartPose.offsetAndRotation(3.0F, -5.0F, -9.0F, 0.0F, Mth.PI, Mth.PI / 16F));
		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public Iterable<ModelPart> parts() {
		return this.parts;
	}
}
