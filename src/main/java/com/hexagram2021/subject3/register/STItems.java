package com.hexagram2021.subject3.register;

import com.hexagram2021.subject3.Subject3;
import com.hexagram2021.subject3.common.items.BedBoatItem;
import com.hexagram2021.subject3.common.items.BedMinecartItem;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.hexagram2021.subject3.Subject3.MODID;

public class STItems {
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	@SuppressWarnings("unchecked")
	public static final class BedBoats {
		public static final ItemEntry<BedBoatItem>[][] BED_BOATS;

		public static Item byTypeAndColor(Boat.Type type, DyeColor color) {
			return BED_BOATS[type.ordinal()][color.ordinal()].get();
		}

		private static void init() {}

		static {
			BED_BOATS = new ItemEntry[Boat.Type.values().length][DyeColor.values().length];
			for(Boat.Type type: Boat.Type.values()) {
				for(DyeColor color: DyeColor.values()) {
					BED_BOATS[type.ordinal()][color.ordinal()] = ItemEntry.register(
							type.getName() + "_" + color.getName() + "_bed_boat", () -> new BedBoatItem(type, color, new Item.Properties().tab(Subject3.ITEM_GROUP).stacksTo(1))
					);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final class BedMinecarts {
		public static final ItemEntry<BedMinecartItem>[] BED_MINECARTS;
		
		public static BedMinecartItem byColor(DyeColor color) {
			return BED_MINECARTS[color.ordinal()].get();
		}

		private static void init() {}

		static {
			BED_MINECARTS = new ItemEntry[DyeColor.values().length];

			for(DyeColor color: DyeColor.values()) {
				BED_MINECARTS[color.ordinal()] = ItemEntry.register(
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
	public static class ItemEntry<T extends Item> implements Supplier<T>, ItemLike {
		private final RegistryObject<T> regObject;

		private static ItemEntry<Item> simple(String name, Consumer<Item.Properties> makeProps, Consumer<Item> processItem) {
			return register(name, () -> Util.make(new Item(Util.make(new Item.Properties(), makeProps)), processItem));
		}

		private static <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> make) {
			return new ItemEntry<>(REGISTER.register(name, make));
		}

		private ItemEntry(RegistryObject<T> regObject) {
			this.regObject = regObject;
		}

		@Override
		@Nonnull
		public T get() {
			return this.regObject.get();
		}

		@Nonnull
		@Override
		public Item asItem() {
			return this.regObject.get();
		}

		public ResourceLocation getId() {
			return this.regObject.getId();
		}
	}
}
