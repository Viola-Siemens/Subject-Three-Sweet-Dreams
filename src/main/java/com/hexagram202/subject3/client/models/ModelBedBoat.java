package com.hexagram202.subject3.client.models;

import com.hexagram202.subject3.client.models.layer.LayerBed;
import com.hexagram202.subject3.common.entities.EntityBedBoat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelBedBoat extends ModelBoat{
    public static final TestLoggingLevel levelZ = new TestLoggingLevel("ModelBoatZLevel");

    @Override
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
        super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.boatSides[0].offsetX, this.boatSides[0].offsetY + 1/8d, this.boatSides[0].offsetZ + levelZ.getValue(p_78088_4_ % 10 == 0, 5));

        GlStateManager.scale(0.875f, 0.875f, 0.875f);
        GlStateManager.rotate(180f, 1, 0, -1);

        LayerBed.renderBed(((EntityBedBoat)p_78088_1_).getDataColor());

        GlStateManager.scale(1/ 0.875f, 1/ 0.875f, 1/ 0.875f);
        GlStateManager.rotate(180f, -1, 0, 1);

        GlStateManager.translate(-this.boatSides[0].offsetX, -this.boatSides[0].offsetY + 1/8d, -this.boatSides[0].offsetZ);

        GlStateManager.popMatrix();
    }
}