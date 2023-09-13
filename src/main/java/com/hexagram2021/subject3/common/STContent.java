package com.hexagram2021.subject3.common;

import com.hexagram2021.subject3.register.STBlocks;
import com.hexagram2021.subject3.register.STCreativeModeTabs;
import com.hexagram2021.subject3.register.STEntities;
import com.hexagram2021.subject3.register.STItems;
import net.minecraftforge.eventbus.api.IEventBus;

public class STContent {
	public static void modConstruction(IEventBus bus) {
		STBlocks.init(bus);
		STItems.init(bus);
		STEntities.init(bus);
		STCreativeModeTabs.init(bus);
	}

	public static void init() {
	}
}
