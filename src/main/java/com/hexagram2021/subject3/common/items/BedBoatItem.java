package com.hexagram2021.subject3.common.items;

import com.hexagram2021.subject3.common.entities.BedBoatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public class BedBoatItem extends Item {
	private static final Predicate<Entity> ENTITY_PREDICATE = EntityPredicates.NO_SPECTATORS.and(Entity::isPickable);

	private final BoatEntity.Type type;
	private final DyeColor color;

	public BedBoatItem(BoatEntity.Type type, DyeColor color, Item.Properties props) {
		super(props);
		this.type = type;
		this.color = color;
	}

	@Override @Nonnull
	public ActionResult<ItemStack> use(@Nonnull World level, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		RayTraceResult raytraceresult = getPlayerPOVHitResult(level, player, RayTraceContext.FluidMode.ANY);
		if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
			return ActionResult.pass(itemstack);
		} else {
			Vector3d vector3d = player.getViewVector(1.0F);
			List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vector3d.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
			if (!list.isEmpty()) {
				Vector3d vector3d1 = player.getEyePosition(1.0F);

				for(Entity entity : list) {
					AxisAlignedBB axisalignedbb = entity.getBoundingBox().inflate(entity.getPickRadius());
					if (axisalignedbb.contains(vector3d1)) {
						return ActionResult.pass(itemstack);
					}
				}
			}

			if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
				BedBoatEntity boatEntity = new BedBoatEntity(level, raytraceresult.getLocation().x, raytraceresult.getLocation().y, raytraceresult.getLocation().z);
				boatEntity.setType(this.type);
				boatEntity.setColor(this.color);
				boatEntity.yRot = player.yRot;
				if (!level.noCollision(boatEntity, boatEntity.getBoundingBox().inflate(-0.1D))) {
					return ActionResult.fail(itemstack);
				} else {
					if (!level.isClientSide) {
						level.addFreshEntity(boatEntity);
						if (!player.abilities.instabuild) {
							itemstack.shrink(1);
						}
					}

					player.awardStat(Stats.ITEM_USED.get(this));
					return ActionResult.sidedSuccess(itemstack, level.isClientSide());
				}
			} else {
				return ActionResult.pass(itemstack);
			}
		}
	}
}
