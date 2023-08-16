package com.hexagram2021.subject3.client.models;

import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public abstract class AbstractBedBoatModel extends ListModel<BedBoatEntity> {
	protected final ModelPart leftPaddle;
	protected final ModelPart rightPaddle;
	public final ModelPart bottom;

	public AbstractBedBoatModel(ModelPart root) {
		this.leftPaddle = root.getChild("left_paddle");
		this.rightPaddle = root.getChild("right_paddle");
		this.bottom = root.getChild("bottom");
	}

	@Override
	public void setupAnim(BedBoatEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		animatePaddle(entity, 0, this.leftPaddle, limbSwing);
		animatePaddle(entity, 1, this.rightPaddle, limbSwing);
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
