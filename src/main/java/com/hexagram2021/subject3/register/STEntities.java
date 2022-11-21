package com.hexagram2021.subject3.register;

import com.hexagram2021.subject3.common.entities.BedMinecartEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.hexagram2021.subject3.Subject3.MODID;

public class STEntities {
	private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

	public static final RegistryObject<EntityType<?>> BED_MINECART = REGISTER.register(
			"bed_minecart", () ->
					EntityType.Builder.of(BedMinecartEntity::new, EntityClassification.MISC)
							.sized(0.98F, 0.7F).clientTrackingRange(8)
							.build(new ResourceLocation(MODID, "bed_minecart").toString())
	);

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
