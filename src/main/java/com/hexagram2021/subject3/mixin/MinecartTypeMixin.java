package com.hexagram2021.subject3.mixin;

import com.hexagram2021.subject3.common.entities.BedMinecartEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(AbstractMinecart.Type.class)
public class MinecartTypeMixin {
	@Final @Shadow @Mutable
	private static AbstractMinecart.Type[] $VALUES;

	@SuppressWarnings("unused")
	MinecartTypeMixin(String name, int ord) {
		throw new UnsupportedOperationException("Replaced by Mixin");
	}

	@Inject(method = "<clinit>()V", at = @At(value = "FIELD", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart$Type;$VALUES:[Lnet/minecraft/world/entity/vehicle/AbstractMinecart$Type;"))
	private static void st_addBedMinecarts(CallbackInfo ci) {
		int ordinal = $VALUES.length;
		$VALUES = Arrays.copyOf($VALUES, ordinal + 1);
		BedMinecartEntity.BED = $VALUES[ordinal] = (AbstractMinecart.Type)(Object) new MinecartTypeMixin("BED", ordinal);
	}
}
