package com.hexagram2021.subject3.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.hexagram2021.subject3.Subject3.MODID;

public class STCreativeModeTabs {
	private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

	public static final RegistryObject<CreativeModeTab> ITEM_GROUP = register(
			"item_group", Component.translatable("itemGroup.subject3"), () -> new ItemStack(STItems.BedBoats.byTypeAndColor(Boat.Type.OAK, DyeColor.RED)),
			(parameters, output) -> STItems.ItemEntry.ALL_ITEMS.forEach(output::accept)
	);

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	@SuppressWarnings("SameParameterValue")
	private static RegistryObject<CreativeModeTab> register(String name, Component title, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator generator) {
		return REGISTER.register(name, () -> CreativeModeTab.builder().title(title).icon(icon).displayItems(generator).build());
	}
}
