package com.hexagram2021.subject3.register;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
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
