package com.hexagram202.subject3.common.recipe;

import com.hexagram202.subject3.Subject3;
import com.hexagram202.subject3.common.entities.EntityBedBoat;
import com.hexagram202.subject3.common.item.ItemBedBoat;
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

public class DyeBedBoatRecipe extends ShapelessOreRecipe {
    public DyeBedBoatRecipe() {
        super(new ResourceLocation("subject3", "dye_bed_boat_recipe"), new ItemStack(Subject3.ITEM_BED),
            Ingredient.fromItem(Items.BED), Ingredient.fromItem(Items.DYE), Ingredient.fromItems(Items.BOAT, Items.ACACIA_BOAT, Items.BIRCH_BOAT, Items.DARK_OAK_BOAT, Items.JUNGLE_BOAT, Items.SPRUCE_BOAT));
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
        EnumDyeColor color = null;
        EntityBoat.Type type = null;
        for (int i = 0; i < p_77572_1_.getSizeInventory(); i++) {
            ItemStack stack = p_77572_1_.getStackInSlot(i);
            if (stack.getItem() == Items.DYE) {
                color = EnumDyeColor.byMetadata(stack.getMetadata());
            } else if (stack.getItem() == Items.ACACIA_BOAT) {
                type = EntityBoat.Type.ACACIA;
            } else if (stack.getItem() == Items.BOAT) {
                type = EntityBoat.Type.OAK;
            } else if (stack.getItem() == Items.BIRCH_BOAT) {
                type = EntityBoat.Type.BIRCH;
            } else if (stack.getItem() == Items.DARK_OAK_BOAT) {
                type = EntityBoat.Type.DARK_OAK;
            } else if (stack.getItem() == Items.JUNGLE_BOAT) {
                type = EntityBoat.Type.JUNGLE;
            }  else if (stack.getItem() == Items.SPRUCE_BOAT) {
                type = EntityBoat.Type.SPRUCE;
            }
        }
        if (type == null) type = EntityBoat.Type.OAK;
        if (color == null) color = EnumDyeColor.WHITE;
        return new ItemStack(Subject3.ITEM_BED, 1, ItemBedBoat.makeData(type, color));
    }
}
