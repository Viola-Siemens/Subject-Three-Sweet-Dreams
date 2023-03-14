package com.hexagram2021.subject3.client;

import com.hexagram2021.subject3.client.renderers.BedBoatRenderer;
import com.hexagram2021.subject3.register.STEntities;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientEntityEventSubscriber {
	public static void onRegisterRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(STEntities.BED_MINECART.get(), MinecartRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(STEntities.BED_BOAT.get(), BedBoatRenderer::new);
	}
}
