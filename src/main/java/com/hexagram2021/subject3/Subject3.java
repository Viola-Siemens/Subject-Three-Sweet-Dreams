package com.hexagram2021.subject3;

import com.hexagram2021.subject3.register.STItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(Subject3.MODID)
public class Subject3 {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "subject3";

    public Subject3() {


        MinecraftForge.EVENT_BUS.register(this);
    }

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MODID) {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return new ItemStack(STItems.BedBoats.OAK_BED_BOAT);
        }
    };
}
