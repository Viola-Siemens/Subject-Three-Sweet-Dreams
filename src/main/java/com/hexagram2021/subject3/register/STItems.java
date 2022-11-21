package com.hexagram2021.subject3.register;

import com.hexagram2021.subject3.Subject3;
import net.minecraft.block.WoodType;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.hexagram2021.subject3.Subject3.MODID;

public class STItems {
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static final class BedBoats {
		public static final ItemRegObject<BedBoatItem> OAK_BED_BOAT = ItemRegObject.register(
				"oak_bed_boat", () -> new BedBoatItem(new Item.Properties().tab(Subject3.ITEM_GROUP), WoodType.OAK)
		);
	}


	public static class ItemRegObject<T extends Item> implements Supplier<T>, IItemProvider {
		private final RegistryObject<T> regObject;

		private static ItemRegObject<Item> simple(String name) {
			return simple(name, $ -> { }, $ -> { });
		}

		private static ItemRegObject<Item> simple(String name, Consumer<Item.Properties> makeProps, Consumer<Item> processItem) {
			return register(name, () -> Util.make(new Item(Util.make(new Item.Properties(), makeProps)), processItem));
		}

		private static <T extends Item> ItemRegObject<T> register(String name, Supplier<? extends T> make) {
			return new ItemRegObject<>(REGISTER.register(name, make));
		}

		private static <T extends Item> ItemRegObject<T> of(T existing) {
			return new ItemRegObject<>(RegistryObject.of(existing.getRegistryName(), ForgeRegistries.ITEMS));
		}

		private ItemRegObject(RegistryObject<T> regObject)
		{
			this.regObject = regObject;
		}

		@Override
		@Nonnull
		public T get()
		{
			return regObject.get();
		}

		@Nonnull
		@Override
		public Item asItem()
		{
			return regObject.get();
		}

		public ResourceLocation getId()
		{
			return regObject.getId();
		}
	}
}
