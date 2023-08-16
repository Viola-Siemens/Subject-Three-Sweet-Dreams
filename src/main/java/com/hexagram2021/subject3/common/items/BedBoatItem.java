package com.hexagram2021.subject3.common.items;

import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class BedBoatItem extends Item {
	private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

	private final Boat.Type type;
	private final DyeColor color;

	public BedBoatItem(Boat.Type type, DyeColor color, Item.Properties props) {
		super(props);
		this.type = type;
		this.color = color;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		HitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
		if (hitResult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(itemstack);
		}
		Vec3 vec3 = player.getViewVector(1.0F);
		List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
		if (!list.isEmpty()) {
			Vec3 vector3d1 = player.getEyePosition(1.0F);

			for(Entity entity : list) {
				AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
				if (aabb.contains(vector3d1)) {
					return InteractionResultHolder.pass(itemstack);
				}
			}
		}

		if (hitResult.getType() == HitResult.Type.BLOCK) {
			BedBoatEntity boatEntity = new BedBoatEntity(level, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z);
			boatEntity.setVariant(this.type);
			boatEntity.setColor(this.color);
			boatEntity.setYRot(player.getYRot());
			if (!level.noCollision(boatEntity, boatEntity.getBoundingBox().inflate(-0.1D))) {
				return InteractionResultHolder.fail(itemstack);
			}
			if (!level.isClientSide) {
				level.addFreshEntity(boatEntity);
				if (!player.getAbilities().instabuild) {
					itemstack.shrink(1);
				}
			}

			player.awardStat(Stats.ITEM_USED.get(this));
			return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
		}
		return InteractionResultHolder.pass(itemstack);
	}
}
