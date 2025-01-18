package com.hexagram202.subject3.common.item;

import com.hexagram202.subject3.Subject3;
import com.hexagram202.subject3.common.entities.EntityBedBoat;
import com.hexagram202.subject3.common.utils.Pair;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemBedBoat extends ItemBed{
    // enum Color might not be extended.
    public static final int COLOR_COUNT = EnumDyeColor.values().length;

    public static final int MAX = EnumDyeColor.values().length * EntityBoat.Type.values().length;

    public static Pair<EntityBoat.Type, EnumDyeColor> getData(int damage){
        int color = damage % COLOR_COUNT;
        int type = (damage - color) / COLOR_COUNT;
        return Pair.of(EntityBoat.Type.byId(type), EnumDyeColor.byMetadata(color));
    }

    public static int makeData(Pair<EntityBoat.Type, EnumDyeColor> colorPair){
        return colorPair.left().ordinal() * COLOR_COUNT + colorPair.right().ordinal();
    }

    public static int makeData(EntityBoat.Type type, EnumDyeColor colorPair){
        return type.ordinal() * COLOR_COUNT + colorPair.ordinal();
    }

    @Override
    public Entity createBed(World world, EntityPlayer player, ItemStack stack) {
        return new EntityBedBoat(world, player, stack);
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getItemStackDisplayName(ItemStack p_77653_1_) {
        if (p_77653_1_.hasDisplayName()) return p_77653_1_.getDisplayName();
        else {
            Pair<EntityBoat.Type, EnumDyeColor> pair = getData(p_77653_1_.getMetadata());
            // Color + Boat + Sleep
            return I18n.translateToLocal(pair.right().getTranslationKey())
                    + " " + I18n.translateToLocal("item.boat." + pair.left().getName() + ".name")
                    + " " + I18n.translateToLocal("item.subject3.bed_boat.name");
        }
    }

    @Override
    public void getSubItems(CreativeTabs p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
        if (this.isInCreativeTab(p_150895_1_)) {
            for (EnumDyeColor color : EnumDyeColor.values()){
                for (EntityBoat.Type type : EntityBoat.Type.values()) {
                    p_150895_2_.add(new ItemStack(this, 1, makeData(type, color)));
                }
            }
        }
    }
}
