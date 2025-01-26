package com.hexagram202.subject3.client.models;

import com.hexagram202.subject3.client.models.layer.LayerBed;
import com.hexagram202.subject3.common.entities.EntityBedMinecart;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.entity.Entity;

import net.minecraft.client.renderer.GlStateManager;

public class ModelBedMinecart extends ModelMinecart{
    public static final TestLoggingLevel levelY = new TestLoggingLevel("ModelMinecartYLevel");
    public static final TestLoggingLevel levelZ = new TestLoggingLevel("ModelMinecartZLevel");
    public static final TestLoggingLevel scale = new TestLoggingLevel("ModelMinecartScale");

    @Override
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
        super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
        GlStateManager.pushMatrix();

        GlStateManager.scale(scale.getValue(p_78088_4_ % 10 ==0, 2), scale.getValue(p_78088_4_ % 10 ==0, 2), scale.getValue(p_78088_4_ % 10 ==0, 2));
        GlStateManager.rotate(180f, 1, 0, -1);
        GlStateManager.translate(0, levelY.getValue(p_78088_4_ % 10 ==0, 2), levelZ.getValue(p_78088_4_ % 10 ==0, 2));
        
        LayerBed.renderBed(((EntityBedMinecart)p_78088_1_).getDataColor());

        GlStateManager.popMatrix();
    }
}