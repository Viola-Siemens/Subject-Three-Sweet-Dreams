package com.hexagram202.subject3.common.recipe;

import com.hexagram202.subject3.Subject3;
import com.hexagram202.subject3.common.item.ItemBedBoat;
import com.hexagram202.subject3.common.item.ItemBedMinecart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraft.util.NonNullList;

public class BedMinecartRecipe extends ShapelessOreRecipe {
    public BedMinecartRecipe() {
        super(new ResourceLocation("subject3", "bed_minecart_recipe"), new ItemStack(Subject3.ITEM_MINECART),
                Ingredient.fromItem(Items.BED), Ingredient.fromItem(Items.MINECART));
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
        EnumDyeColor color = null;
        for (int i = 0; i < p_77572_1_.getSizeInventory(); i++) {
            ItemStack stack = p_77572_1_.getStackInSlot(i);
            if (stack.getItem() == Items.BED) {
                color = EnumDyeColor.byMetadata(stack.getMetadata());
            }
        }
        if (color == null) color = EnumDyeColor.WHITE;
        return new ItemStack(Subject3.ITEM_MINECART, 1, ItemBedMinecart.makeData(color));
    }
}
