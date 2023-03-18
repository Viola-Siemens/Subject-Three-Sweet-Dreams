package com.hexagram2021.subject3.register;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
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
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.subject3.Subject3.MODID;

public class STBlocks {
	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static class Technical {
		private static final Supplier<AbstractBlock.Properties> PROPERTIES = () ->
				AbstractBlock.Properties.of(Material.STRUCTURAL_AIR).noCollission().noDrops().instabreak();

		public static final BlockEntry<Block> BLACK_BOAT_BED = new BlockEntry<>("black_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> BLUE_BOAT_BED = new BlockEntry<>("blue_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> BROWN_BOAT_BED = new BlockEntry<>("brown_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> CYAN_BOAT_BED = new BlockEntry<>("cyan_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> GRAY_BOAT_BED = new BlockEntry<>("gray_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> GREEN_BOAT_BED = new BlockEntry<>("green_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> LIGHT_BLUE_BOAT_BED = new BlockEntry<>("light_blue_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> LIGHT_GRAY_BOAT_BED = new BlockEntry<>("light_gray_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> LIME_BOAT_BED = new BlockEntry<>("lime_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> MAGENTA_BOAT_BED = new BlockEntry<>("magenta_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> ORANGE_BOAT_BED = new BlockEntry<>("orange_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> PINK_BOAT_BED = new BlockEntry<>("pink_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> PURPLE_BOAT_BED = new BlockEntry<>("purple_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> RED_BOAT_BED = new BlockEntry<>("red_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> WHITE_BOAT_BED = new BlockEntry<>("white_boat_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> YELLOW_BOAT_BED = new BlockEntry<>("yellow_boat_bed", PROPERTIES, Block::new);

		public static final BlockEntry<Block> BLACK_MINECART_BED = new BlockEntry<>("black_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> BLUE_MINECART_BED = new BlockEntry<>("blue_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> BROWN_MINECART_BED = new BlockEntry<>("brown_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> CYAN_MINECART_BED = new BlockEntry<>("cyan_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> GRAY_MINECART_BED = new BlockEntry<>("gray_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> GREEN_MINECART_BED = new BlockEntry<>("green_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> LIGHT_BLUE_MINECART_BED = new BlockEntry<>("light_blue_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> LIGHT_GRAY_MINECART_BED = new BlockEntry<>("light_gray_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> LIME_MINECART_BED = new BlockEntry<>("lime_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> MAGENTA_MINECART_BED = new BlockEntry<>("magenta_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> ORANGE_MINECART_BED = new BlockEntry<>("orange_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> PINK_MINECART_BED = new BlockEntry<>("pink_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> PURPLE_MINECART_BED = new BlockEntry<>("purple_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> RED_MINECART_BED = new BlockEntry<>("red_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> WHITE_MINECART_BED = new BlockEntry<>("white_minecart_bed", PROPERTIES, Block::new);
		public static final BlockEntry<Block> YELLOW_MINECART_BED = new BlockEntry<>("yellow_minecart_bed", PROPERTIES, Block::new);

		public static BlockState getBoatBedBlockState(DyeColor color) {
			switch (color) {
				case BLACK:
					return Technical.BLACK_BOAT_BED.defaultBlockState();
				case BLUE:
					return Technical.BLUE_BOAT_BED.defaultBlockState();
				case BROWN:
					return Technical.BROWN_BOAT_BED.defaultBlockState();
				case CYAN:
					return Technical.CYAN_BOAT_BED.defaultBlockState();
				case GRAY:
					return Technical.GRAY_BOAT_BED.defaultBlockState();
				case GREEN:
					return Technical.GREEN_BOAT_BED.defaultBlockState();
				case LIGHT_BLUE:
					return Technical.LIGHT_BLUE_BOAT_BED.defaultBlockState();
				case LIGHT_GRAY:
					return Technical.LIGHT_GRAY_BOAT_BED.defaultBlockState();
				case LIME:
					return Technical.LIME_BOAT_BED.defaultBlockState();
				case MAGENTA:
					return Technical.MAGENTA_BOAT_BED.defaultBlockState();
				case ORANGE:
					return Technical.ORANGE_BOAT_BED.defaultBlockState();
				case PINK:
					return Technical.PINK_BOAT_BED.defaultBlockState();
				case PURPLE:
					return Technical.PURPLE_BOAT_BED.defaultBlockState();
				case RED:
					return Technical.RED_BOAT_BED.defaultBlockState();
				case WHITE:
				default:
					return Technical.WHITE_BOAT_BED.defaultBlockState();
				case YELLOW:
					return Technical.YELLOW_BOAT_BED.defaultBlockState();
			}
		}

		public static BlockState getMinecartBedBlockState(DyeColor color) {
			switch (color) {
				case BLACK:
					return Technical.BLACK_MINECART_BED.defaultBlockState();
				case BLUE:
					return Technical.BLUE_MINECART_BED.defaultBlockState();
				case BROWN:
					return Technical.BROWN_MINECART_BED.defaultBlockState();
				case CYAN:
					return Technical.CYAN_MINECART_BED.defaultBlockState();
				case GRAY:
					return Technical.GRAY_MINECART_BED.defaultBlockState();
				case GREEN:
					return Technical.GREEN_MINECART_BED.defaultBlockState();
				case LIGHT_BLUE:
					return Technical.LIGHT_BLUE_MINECART_BED.defaultBlockState();
				case LIGHT_GRAY:
					return Technical.LIGHT_GRAY_MINECART_BED.defaultBlockState();
				case LIME:
					return Technical.LIME_MINECART_BED.defaultBlockState();
				case MAGENTA:
					return Technical.MAGENTA_MINECART_BED.defaultBlockState();
				case ORANGE:
					return Technical.ORANGE_MINECART_BED.defaultBlockState();
				case PINK:
					return Technical.PINK_MINECART_BED.defaultBlockState();
				case PURPLE:
					return Technical.PURPLE_MINECART_BED.defaultBlockState();
				case RED:
					return Technical.RED_MINECART_BED.defaultBlockState();
				case WHITE:
				default:
					return Technical.WHITE_MINECART_BED.defaultBlockState();
				case YELLOW:
					return Technical.YELLOW_MINECART_BED.defaultBlockState();
			}
		}

		public static void init() {}
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		Technical.init();
	}

	@SuppressWarnings("unused")
	public static final class BlockEntry<T extends Block> implements Supplier<T>, IItemProvider {
		private final RegistryObject<T> regObject;
		private final Supplier<AbstractBlock.Properties> properties;

		public static BlockEntry<Block> simple(String name, Supplier<AbstractBlock.Properties> properties, Consumer<Block> extra) {
			return new BlockEntry<>(name, properties, p -> Util.make(new Block(p), extra));
		}

		public BlockEntry(String name, Supplier<AbstractBlock.Properties> properties, Function<AbstractBlock.Properties, T> make) {
			this.properties = properties;
			this.regObject = REGISTER.register(name, () -> make.apply(properties.get()));
		}

		@Override
		public T get()
		{
			return regObject.get();
		}

		public BlockState defaultBlockState() {
			return get().defaultBlockState();
		}

		public ResourceLocation getId() {
			return regObject.getId();
		}

		public AbstractBlock.Properties getProperties()
		{
			return properties.get();
		}

		@Nonnull
		@Override
		public Item asItem()
		{
			return get().asItem();
		}
	}
}
