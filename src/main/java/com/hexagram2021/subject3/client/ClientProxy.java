package com.hexagram2021.subject3.client;

import com.hexagram2021.subject3.register.STBlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.hexagram2021.subject3.Subject3.MODID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {
	@SubscribeEvent
	public static void setup(final FMLClientSetupEvent event) {
		ClientEntityEventSubscriber.onRegisterRenderers();
		event.enqueueWork(ClientProxy::registerRenderLayers);
	}

	private static void registerRenderLayers() {
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.BLACK_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.BLUE_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.BROWN_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.CYAN_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.GRAY_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.GREEN_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.LIGHT_BLUE_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.LIGHT_GRAY_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.LIME_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.MAGENTA_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.ORANGE_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.PINK_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.PURPLE_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.RED_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.WHITE_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.YELLOW_BOAT_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.BLACK_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.BLUE_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.BROWN_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.CYAN_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.GRAY_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.GREEN_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.LIGHT_BLUE_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.LIGHT_GRAY_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.LIME_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.MAGENTA_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.ORANGE_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.PINK_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.PURPLE_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.RED_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.WHITE_MINECART_BED.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(STBlocks.Technical.YELLOW_MINECART_BED.get(), RenderType.cutout());
	}
}
