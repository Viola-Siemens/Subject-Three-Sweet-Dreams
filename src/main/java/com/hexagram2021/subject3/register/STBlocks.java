package com.hexagram2021.subject3.register;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
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
		private static final Supplier<BlockBehaviour.Properties> PROPERTIES = () ->
				BlockBehaviour.Properties.of(Material.STRUCTURAL_AIR).noCollission().noDrops().instabreak();

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
			return switch (color) {
				case BLACK -> Technical.BLACK_BOAT_BED.defaultBlockState();
				case BLUE -> Technical.BLUE_BOAT_BED.defaultBlockState();
				case BROWN -> Technical.BROWN_BOAT_BED.defaultBlockState();
				case CYAN -> Technical.CYAN_BOAT_BED.defaultBlockState();
				case GRAY -> Technical.GRAY_BOAT_BED.defaultBlockState();
				case GREEN -> Technical.GREEN_BOAT_BED.defaultBlockState();
				case LIGHT_BLUE -> Technical.LIGHT_BLUE_BOAT_BED.defaultBlockState();
				case LIGHT_GRAY -> Technical.LIGHT_GRAY_BOAT_BED.defaultBlockState();
				case LIME -> Technical.LIME_BOAT_BED.defaultBlockState();
				case MAGENTA -> Technical.MAGENTA_BOAT_BED.defaultBlockState();
				case ORANGE -> Technical.ORANGE_BOAT_BED.defaultBlockState();
				case PINK -> Technical.PINK_BOAT_BED.defaultBlockState();
				case PURPLE -> Technical.PURPLE_BOAT_BED.defaultBlockState();
				case RED -> Technical.RED_BOAT_BED.defaultBlockState();
				case WHITE -> Technical.WHITE_BOAT_BED.defaultBlockState();
				case YELLOW -> Technical.YELLOW_BOAT_BED.defaultBlockState();
			};
		}

		public static BlockState getMinecartBedBlockState(DyeColor color) {
			return switch (color) {
				case BLACK -> Technical.BLACK_MINECART_BED.defaultBlockState();
				case BLUE -> Technical.BLUE_MINECART_BED.defaultBlockState();
				case BROWN -> Technical.BROWN_MINECART_BED.defaultBlockState();
				case CYAN -> Technical.CYAN_MINECART_BED.defaultBlockState();
				case GRAY -> Technical.GRAY_MINECART_BED.defaultBlockState();
				case GREEN -> Technical.GREEN_MINECART_BED.defaultBlockState();
				case LIGHT_BLUE -> Technical.LIGHT_BLUE_MINECART_BED.defaultBlockState();
				case LIGHT_GRAY -> Technical.LIGHT_GRAY_MINECART_BED.defaultBlockState();
				case LIME -> Technical.LIME_MINECART_BED.defaultBlockState();
				case MAGENTA -> Technical.MAGENTA_MINECART_BED.defaultBlockState();
				case ORANGE -> Technical.ORANGE_MINECART_BED.defaultBlockState();
				case PINK -> Technical.PINK_MINECART_BED.defaultBlockState();
				case PURPLE -> Technical.PURPLE_MINECART_BED.defaultBlockState();
				case RED -> Technical.RED_MINECART_BED.defaultBlockState();
				case WHITE -> Technical.WHITE_MINECART_BED.defaultBlockState();
				case YELLOW -> Technical.YELLOW_MINECART_BED.defaultBlockState();
			};
		}

		public static void init() {}
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		Technical.init();
	}

	@SuppressWarnings("unused")
	public static final class BlockEntry<T extends Block> implements Supplier<T>, ItemLike {
		private final RegistryObject<T> regObject;
		private final Supplier<BlockBehaviour.Properties> properties;

		public static BlockEntry<Block> simple(String name, Supplier<BlockBehaviour.Properties> properties, Consumer<Block> extra) {
			return new BlockEntry<>(name, properties, p -> Util.make(new Block(p), extra));
		}

		public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
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

		public BlockBehaviour.Properties getProperties()
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
