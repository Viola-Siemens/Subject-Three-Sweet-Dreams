package com.hexagram2021.subject3.client.layers;

import com.hexagram2021.subject3.client.models.AbstractBedBoatModel;
import com.hexagram2021.subject3.client.models.BedRaftModel;
import com.hexagram2021.subject3.client.renderers.BedBoatRenderer;
import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import com.hexagram2021.subject3.register.STBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class BoatBedLayer {
	private final BedBoatRenderer renderer;

	public BoatBedLayer(BedBoatRenderer renderer) {
		this.renderer = renderer;
	}

	public void render(PoseStack transform, MultiBufferSource buffer, int uv2, BedBoatEntity entity) {
		if (!entity.isInvisible()) {
			renderColoredCutoutModel(this.renderer.getModel(entity), STBlocks.Technical.getBoatBedBlockState(entity.getBedColor()), transform, buffer, uv2);
		}
	}

	protected static void renderColoredCutoutModel(AbstractBedBoatModel model, BlockState technicalBlock, PoseStack transform, MultiBufferSource buffer, int uv2) {
		transform.pushPose();
		ModelPart modelPart = model.bottom;
		modelPart.translateAndRotate(transform);
		transform.mulPose(Axis.XP.rotationDegrees(-90.0F));
		transform.mulPose(Axis.YP.rotationDegrees(90.0F));
		transform.translate(-0.4375D, -0.5D, -0.5D);
		if(model instanceof BedRaftModel) {
			transform.scale(0.9375F, 0.9375F, 0.9375F);
		}
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(technicalBlock, transform, buffer, uv2, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.cutout());
		transform.popPose();
	}
}
