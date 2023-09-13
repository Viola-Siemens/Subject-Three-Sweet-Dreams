package com.hexagram2021.subject3;

import com.hexagram2021.subject3.common.STContent;
import com.hexagram2021.subject3.common.STSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Subject3.MODID)
public class Subject3 {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "subject3";

	public Subject3() {
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);
		STContent.modConstruction(bus);

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(STContent::init);
	}

	public void serverStarted(ServerStartedEvent event) {
		ServerLevel world = event.getServer().getLevel(Level.OVERWORLD);
		assert world != null;
		if (!world.isClientSide) {
			STSavedData worldData = world.getDataStorage().computeIfAbsent(STSavedData::new, STSavedData::new, STSavedData.SAVED_DATA_NAME);
			STSavedData.setInstance(worldData);
			STSavedData.markAllRelatedChunks(event.getServer());
		}
	}
}
