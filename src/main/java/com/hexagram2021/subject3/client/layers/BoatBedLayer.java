package com.hexagram2021.subject3.client.layers;

import com.hexagram2021.subject3.client.models.BedBoatModel;
import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import com.hexagram2021.subject3.register.STBlocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BoatBedLayer extends LayerRenderer<BedBoatEntity, BedBoatModel> {
	public BoatBedLayer(IEntityRenderer<BedBoatEntity, BedBoatModel> renderer) {
		super(renderer);
	}

	public void render(@Nonnull MatrixStack transform, @Nonnull IRenderTypeBuffer buffer, int uv2, BedBoatEntity entity, float f1, float f2, float ticks, float f3, float f4, float xRot) {
		if (!entity.isInvisible()) {
			renderColoredCutoutModel(this.getParentModel(), STBlocks.Technical.getBoatBedBlockState(entity.getBedColor()), transform, buffer, uv2);
		}
	}

	protected static void renderColoredCutoutModel(BedBoatModel model, BlockState technicalBlock, MatrixStack transform, IRenderTypeBuffer buffer, int uv2) {
		transform.pushPose();
		ModelRenderer modelrenderer = model.bottom;
		modelrenderer.translateAndRotate(transform);
		transform.translate(-0.5D, -0.5D, -0.5D);
		Minecraft.getInstance().getBlockRenderer().renderBlock(technicalBlock, transform, buffer, uv2, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
		transform.popPose();
	}
}
