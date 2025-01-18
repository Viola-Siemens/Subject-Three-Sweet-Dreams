package com.hexagram202.subject3.client.models;

import com.hexagram202.subject3.client.models.layer.LayerBed;
import com.hexagram202.subject3.common.entities.EntityBedBoat;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.entity.Entity;

import net.minecraft.client.renderer.GlStateManager;

public class ModelBedBoat extends ModelBoat{
    @Override
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
        super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.875f, 0.875f, 0.875f);
        GlStateManager.rotate(180f, 1, 0, -1);
        GlStateManager.translate(0, 1/8f, -3/8);
        LayerBed.renderBed(((EntityBedBoat)p_78088_1_).getDataColor());
        GlStateManager.rotate(-180f, 1, 0, -1);
        GlStateManager.translate(0, -1/8f, 3/8);
        GlStateManager.scale(1 / 0.875f, 1 / 0.875f, 1 / 0.875f);
        GlStateManager.popMatrix();
    }
}