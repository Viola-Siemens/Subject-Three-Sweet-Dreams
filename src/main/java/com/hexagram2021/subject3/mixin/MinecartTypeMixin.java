package com.hexagram2021.subject3.mixin;

import com.hexagram2021.subject3.common.entities.BedMinecartEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(AbstractMinecartEntity.Type.class)
public class MinecartTypeMixin {
	@Final @Shadow @Mutable
	private static AbstractMinecartEntity.Type[] $VALUES;

	MinecartTypeMixin(String name, int ord) {
		throw new UnsupportedOperationException("Replaced by Mixin");
	}

	@Inject(method = "<clinit>()V", at = @At(value = "FIELD", shift = At.Shift.AFTER, target = "Lnet/minecraft/entity/item/minecart/AbstractMinecartEntity$Type;$VALUES:[Lnet/minecraft/entity/item/minecart/AbstractMinecartEntity$Type;"))
	private static void st_addBedMinecarts(CallbackInfo ci) {
		int ordinal = $VALUES.length;
		$VALUES = Arrays.copyOf($VALUES, ordinal + 1);
		BedMinecartEntity.BED = $VALUES[ordinal] = (AbstractMinecartEntity.Type)(Object) new MinecartTypeMixin("BED", ordinal);
	}
}
