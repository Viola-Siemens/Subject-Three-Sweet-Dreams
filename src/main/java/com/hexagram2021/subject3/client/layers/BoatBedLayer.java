package com.hexagram2021.subject3.client.layers;

import com.hexagram2021.subject3.client.models.BedBoatModel;
import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import com.hexagram2021.subject3.register.STBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BoatBedLayer extends RenderLayer<BedBoatEntity, BedBoatModel> {
	public BoatBedLayer(RenderLayerParent<BedBoatEntity, BedBoatModel> renderer) {
		super(renderer);
	}

	public void render(@Nonnull PoseStack transform, @Nonnull MultiBufferSource buffer, int uv2, BedBoatEntity entity, float f1, float f2, float ticks, float f3, float f4, float xRot) {
		if (!entity.isInvisible()) {
			renderColoredCutoutModel(this.getParentModel(), STBlocks.Technical.getBoatBedBlockState(entity.getBedColor()), transform, buffer, uv2);
		}
	}

	protected static void renderColoredCutoutModel(BedBoatModel model, BlockState technicalBlock, PoseStack transform, MultiBufferSource buffer, int uv2) {
		transform.pushPose();
		ModelPart modelPart = model.bottom;
		modelPart.translateAndRotate(transform);
		transform.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
		transform.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		transform.translate(-0.4375D, -0.5D, -0.5D);
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(technicalBlock, transform, buffer, uv2, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
		transform.popPose();
	}
}
