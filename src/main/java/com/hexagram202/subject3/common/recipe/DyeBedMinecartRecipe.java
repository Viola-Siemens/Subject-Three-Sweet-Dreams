package com.hexagram202.subject3.common.recipe;

import com.hexagram202.subject3.Subject3;
import com.hexagram202.subject3.common.item.ItemBedMinecart;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DyeBedMinecartRecipe extends ShapelessOreRecipe {
    public DyeBedMinecartRecipe() {
        super(new ResourceLocation("subject3", "dye_bed_minecart_recipe"), new ItemStack(Subject3.ITEM_MINECART),
                Ingredient.fromItem(Items.BED), Ingredient.fromItem(Items.MINECART), Ingredient.fromItem(Items.DYE));
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
        EnumDyeColor color = null;
        for (int i = 0; i < p_77572_1_.getSizeInventory(); i++) {
            ItemStack stack = p_77572_1_.getStackInSlot(i);
            if (stack.getItem() == Items.DYE) {
                color = EnumDyeColor.byMetadata(stack.getMetadata());
            }
        }
        if (color == null) color = EnumDyeColor.WHITE;
        return new ItemStack(Subject3.ITEM_MINECART, 1, ItemBedMinecart.makeData(color));
    }
}
