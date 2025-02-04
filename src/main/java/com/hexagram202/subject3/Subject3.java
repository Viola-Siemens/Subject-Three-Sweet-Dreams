package com.hexagram202.subject3;

import com.hexagram202.subject3.common.capability.IBedVehicle;
import com.hexagram202.subject3.common.capability.IHasVehicleRespawnPosition;
import com.hexagram202.subject3.common.entities.EntityBedBoat;
import com.hexagram202.subject3.common.entities.EntityBedMinecart;
import com.hexagram202.subject3.common.item.ItemBedBoat;
import com.hexagram202.subject3.common.item.ItemBedMinecart;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod(modid = "subject3")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Subject3 {
    public static final Logger LOGGER = LogManager.getLogger();

    @Mod.Instance
    public static Subject3 instance;

    @GameRegistry.ObjectHolder("subject3:bed_boat")
    public static Item ITEM_BOAT;

    @GameRegistry.ObjectHolder("subject3:bed_minecart")
    public static Item ITEM_MINECART;

    public static final CreativeTabs TAB = new CreativeTabs("subject3") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ITEM_BOAT);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(IHasVehicleRespawnPosition.class, new IHasVehicleRespawnPosition.Storage(),
                IHasVehicleRespawnPosition.Implementation::new);
        CapabilityManager.INSTANCE.register(IBedVehicle.class, new IBedVehicle.Storage(),
                IBedVehicle.Implementation::new);
    }

    public static ItemStack createItem(EntityBedMinecart entityBedMinecart) {
        return new ItemStack(ITEM_MINECART, 1, ItemBedMinecart.makeData(entityBedMinecart.getBedColor()));
    }

    public static ItemStack createItem(EntityBedBoat entityBedBoat) {
        return new ItemStack(ITEM_BOAT, 1, ItemBedBoat.makeData(entityBedBoat.getBoatType(), entityBedBoat.getBedColor()));
    }

    public static ItemStack createItem(IBedVehicle bedVehicle) {
        return new ItemStack(Items.BED, 1, bedVehicle.getBedColor().getMetadata());
    }
}
