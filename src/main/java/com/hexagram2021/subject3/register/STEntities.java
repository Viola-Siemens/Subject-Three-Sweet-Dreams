package com.hexagram2021.subject3.register;

import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import com.hexagram2021.subject3.common.entities.BedMinecartEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.hexagram2021.subject3.Subject3.MODID;

public class STEntities {
	private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

	public static final RegistryObject<EntityType<BedMinecartEntity>> BED_MINECART = REGISTER.register(
			"bed_minecart", () ->
					EntityType.Builder.<BedMinecartEntity>of(BedMinecartEntity::new, MobCategory.MISC)
							.sized(0.98F, 0.7F).clientTrackingRange(8)
							.build(new ResourceLocation(MODID, "bed_minecart").toString())
	);
	public static final RegistryObject<EntityType<BedBoatEntity>> BED_BOAT = REGISTER.register(
			"bed_boat", () ->
					EntityType.Builder.<BedBoatEntity>of(BedBoatEntity::new, MobCategory.MISC)
							.sized(1.375F, 0.5625F).clientTrackingRange(10)
							.build(new ResourceLocation(MODID, "bed_boat").toString())
	);

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
