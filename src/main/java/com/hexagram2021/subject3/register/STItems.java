package com.hexagram2021.subject3.register;

import com.hexagram2021.subject3.Subject3;
import com.hexagram2021.subject3.common.items.BedBoatItem;
import com.hexagram2021.subject3.common.items.BedMinecartItem;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.hexagram2021.subject3.Subject3.MODID;

public class STItems {
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	@SuppressWarnings("unchecked")
	public static final class BedBoats {
		public static final ItemRegObject<BedBoatItem>[][] BED_BOATS;

		public static Item byTypeAndColor(BoatEntity.Type type, DyeColor color) {
			return BED_BOATS[type.ordinal()][color.ordinal()].get();
		}

		private static void init() {}

		static {
			BED_BOATS = new ItemRegObject[BoatEntity.Type.values().length][DyeColor.values().length];
			for(BoatEntity.Type type: BoatEntity.Type.values()) {
				for(DyeColor color: DyeColor.values()) {
					BED_BOATS[type.ordinal()][color.ordinal()] = ItemRegObject.register(
							type.getName() + "_" + color.getName() + "_bed_boat", () -> new BedBoatItem(type, color, new Item.Properties().tab(Subject3.ITEM_GROUP).stacksTo(1))
					);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final class BedMinecarts {
		public static final ItemRegObject<BedMinecartItem>[] BED_MINECARTS;
		
		public static BedMinecartItem byColor(DyeColor color) {
			return BED_MINECARTS[color.ordinal()].get();
		}

		private static void init() {}

		static {
			BED_MINECARTS = new ItemRegObject[DyeColor.values().length];

			for(DyeColor color: DyeColor.values()) {
				BED_MINECARTS[color.ordinal()] = ItemRegObject.register(
						color.getName() + "_bed_minecart", () -> new BedMinecartItem(color, new Item.Properties().tab(Subject3.ITEM_GROUP).stacksTo(1))
				);
			}
		}
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		BedBoats.init();
		BedMinecarts.init();
	}


	@SuppressWarnings("unused")
	public static class ItemRegObject<T extends Item> implements Supplier<T>, IItemProvider {
		private final RegistryObject<T> regObject;

		private static ItemRegObject<Item> simple(String name, Consumer<Item.Properties> makeProps, Consumer<Item> processItem) {
			return register(name, () -> Util.make(new Item(Util.make(new Item.Properties(), makeProps)), processItem));
		}

		private static <T extends Item> ItemRegObject<T> register(String name, Supplier<? extends T> make) {
			return new ItemRegObject<>(REGISTER.register(name, make));
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
