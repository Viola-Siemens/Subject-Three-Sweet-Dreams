package com.hexagram202.subject3.client.models.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class LayerBed{
    public static void renderBed(EnumDyeColor color){
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(Items.BED, 1,  color.getMetadata()), ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();
    }
}
