package com.hexagram2021.subject3.client.renderers.layout;

import com.hexagram2021.subject3.client.models.BedBoatModel;
import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class BedBoatBedLayer extends LayerRenderer<BedBoatEntity, BedBoatModel> {
	public BedBoatBedLayer(IEntityRenderer<BedBoatEntity, BedBoatModel> renderer) {
		super(renderer);
	}

	@Override
	public void render(MatrixStack transform, IRenderTypeBuffer buffer, int h, BedBoatEntity bedBoatEntity,
					   float limbSwing, float limbSwingAmount, float ticks, float ageInTicks, float netHeadYaw, float headPitch) {
		//TODO: render bed in the boat
	}
}
