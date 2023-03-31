package com.hexagram2021.subject3.client;

import com.hexagram2021.subject3.client.models.BedBoatModel;
import com.hexagram2021.subject3.client.renderers.BedBoatRenderer;
import com.hexagram2021.subject3.register.STEntities;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.hexagram2021.subject3.Subject3.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEntityEventSubscriber {
	private static final ModelLayerLocation BED_MINECART = new ModelLayerLocation(new ResourceLocation(MODID, "bed_minecart"), "main");

	@SubscribeEvent
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(BedBoatRenderer.LAYER_LOCATION, BedBoatModel::createBodyModel);
		event.registerLayerDefinition(BED_MINECART, MinecartModel::createBodyLayer);
	}

	@SubscribeEvent
	public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(STEntities.BED_MINECART.get(), context -> new MinecartRenderer<>(context, BED_MINECART));
		event.registerEntityRenderer(STEntities.BED_BOAT.get(), BedBoatRenderer::new);
	}
}
