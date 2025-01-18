package com.hexagram202.subject3;

import com.hexagram202.subject3.common.capability.IBedVehicle;
import com.hexagram202.subject3.common.capability.IHasVehicleRespawnPosition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "subject3")
@Mod.EventBusSubscriber
public class Subject3 {
    public static final Logger LOGGER = LogManager.getLogger();

    @Mod.Instance
    public static Subject3 instance;

    @GameRegistry.ObjectHolder("subject3:bed_boat")
    public static Item ITEM_BED;

    @GameRegistry.ObjectHolder("subject3:bed_minecart")
    public static Item ITEM_MINECART;

    public static final CreativeTabs TAB = new CreativeTabs("subject3") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ITEM_BED);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(IHasVehicleRespawnPosition.class, new IHasVehicleRespawnPosition.Storage(),
                IHasVehicleRespawnPosition.Implementation::new);
        CapabilityManager.INSTANCE.register(IBedVehicle.class, new IBedVehicle.Storage(),
                IBedVehicle.Implementation::new);
    }
}
