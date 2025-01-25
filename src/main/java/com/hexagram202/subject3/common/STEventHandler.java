package com.hexagram202.subject3.common;

import com.hexagram202.subject3.Subject3;
import com.hexagram202.subject3.client.renderer.RenderBedBoat;
import com.hexagram202.subject3.client.renderer.RenderBedMinecart;
import com.hexagram202.subject3.common.capability.Subject3Capabilities;
import com.hexagram202.subject3.common.entities.EntityBedBoat;
import com.hexagram202.subject3.common.entities.EntityBedMinecart;
import com.hexagram202.subject3.common.capability.IBedVehicle;
import com.hexagram202.subject3.common.capability.IHasVehicleRespawnPosition;
import com.hexagram202.subject3.common.item.ItemBedBoat;
import com.hexagram202.subject3.common.item.ItemBedMinecart;
import com.hexagram202.subject3.common.recipe.BedBoatRecipe;
import com.hexagram202.subject3.common.recipe.BedMinecartRecipe;
import com.hexagram202.subject3.common.recipe.DyeBedBoatRecipe;
import com.hexagram202.subject3.common.recipe.DyeBedMinecartRecipe;
import com.hexagram202.subject3.common.utils.STBedVehiclesChunkHandler;
import com.hexagram202.subject3.common.utils.Teleport;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@Mod.EventBusSubscriber
public class STEventHandler {

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemBedBoat().setRegistryName("subject3", "bed_boat")
                        .setTranslationKey("subject3.bed_boat")
                        .setCreativeTab(Subject3.TAB)
                        .setMaxStackSize(1).setMaxDamage(0).setHasSubtypes(true).setNoRepair(),
                new ItemBedMinecart().setRegistryName("subject3", "bed_minecart")
                        .setTranslationKey("subject3.bed_minecart")
                        .setCreativeTab(Subject3.TAB)
                        .setMaxStackSize(1).setMaxDamage(0).setHasSubtypes(true).setNoRepair()
        );
    }

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().registerAll(
                EntityEntryBuilder.create()
                        .entity(EntityBedBoat.class)
                        .factory(EntityBedBoat::new)
                        .id("bed_boat", 0)
                        .name("item.subject3.bed_boat.name")
                        .tracker(64, 3, true)
                        .build(),
                EntityEntryBuilder.create()
                        .entity(EntityBedMinecart.class)
                        .factory(EntityBedMinecart::new)
                        .id("bed_minecart", 1)
                        .name("item.subject3.bed_minecart.name")
                        .tracker(64, 3, true)
                        .build()
        );
    }

    @SubscribeEvent
    public static void registerRecipe(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().registerAll(
                new BedBoatRecipe().setRegistryName("subject3", "bed_boats"),
                new BedMinecartRecipe().setRegistryName("subject3", "bed_minecart"),
                new DyeBedBoatRecipe().setRegistryName("subject3", "dye_bed_boats"),
                new DyeBedMinecartRecipe().setRegistryName("subject3", "dye_bed_minecart")
        );
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event){
        for (EnumDyeColor color : EnumDyeColor.values()){
            for (EntityBoat.Type type : EntityBoat.Type.values()) {
                ModelLoader.setCustomModelResourceLocation(Subject3.ITEM_BED, ItemBedBoat.makeData(type, color),
                        new ModelResourceLocation(new ResourceLocation("subject3", type.getName() + '_' + color.getName() + "_bed_boat"), "inventory"));
            }
            ModelLoader.setCustomModelResourceLocation(Subject3.ITEM_MINECART, ItemBedMinecart.makeData(color),
                        new ModelResourceLocation(new ResourceLocation("subject3", color.getName() + "_bed_minecart"), "inventory")
            );
        }

        RenderingRegistry.registerEntityRenderingHandler(EntityBedBoat.class, RenderBedBoat::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBedMinecart.class, RenderBedMinecart::new);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        EntityPlayer player = event.player;
        if (player.hasCapability(Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION, null)) {
            IHasVehicleRespawnPosition iHasVehicleRespawnPosition = player.getCapability(Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION, null);
            WorldServer worldServer = (WorldServer) player.world;
            UUID bedVehicleUUID = iHasVehicleRespawnPosition.getBedVehicleUUID();
            if (bedVehicleUUID != null) {
                Entity bedVehicle = worldServer.getEntityFromUuid(bedVehicleUUID);
                if (bedVehicle.hasCapability(Subject3Capabilities.BED_VEHICLE, null)) {
                    IBedVehicle iBedVehicle = bedVehicle.getCapability(Subject3Capabilities.BED_VEHICLE, null);
                    if ((bedVehicle.getPassengers().isEmpty())) {
                        if (player.world == bedVehicle.world) {
                            player.setPosition(bedVehicle.posX, bedVehicle.posY, bedVehicle.posZ);
                        } else {
                            player.changeDimension(bedVehicle.dimension, new Teleport(bedVehicle));
                        }
                        iBedVehicle.onEntityStartToRide(player);
                        player.startRiding(bedVehicle);
                        return;
                    }
                } else {
                    ((IHasVehicleRespawnPosition) player).setBedVehicleUUID(null);
                }
                player.sendMessage(new TextComponentTranslation("message.subject3.bed_vehicle_occupied"));
            }
        }
    }

    // Copy the data
    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        EntityPlayer player = event.getEntityPlayer();
        EntityPlayer original = event.getOriginal();
        if (original.hasCapability(Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION, null)
                && player.hasCapability(Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION, null)) {
            player.getCapability(Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION, null)
                            .setBedVehicleUUID(
                                    original.getCapability(Subject3Capabilities.HAS_VEHICLE_RESPAWN_POSITION, null)
                                            .getBedVehicleUUID()
                            );
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            ICapabilitySerializable<NBTTagCompound> provider = new IHasVehicleRespawnPosition.ProviderPlayer();
            event.addCapability(new ResourceLocation("subject3", "respawn_position"), provider);
        }
    }

    @SubscribeEvent
    public static void onWorldUpdate(TickEvent.WorldTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) {
            if (event.world instanceof WorldServer) {
                STBedVehiclesChunkHandler.updateBedVehicles((WorldServer)event.world);
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
       STBedVehiclesChunkHandler.clearWhenClientLoggedOut();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    public static void preEntityRender(RenderLivingEvent.Pre event){
        if (event.getEntity().isRiding() && event.getEntity().getRidingEntity().hasCapability(Subject3Capabilities.BED_VEHICLE, null)) {
            GlStateManager.pushMatrix();
            if (event.getEntity() instanceof EntityPlayer) {
                EntityPlayer player = ((EntityPlayer)event.getEntity());
                player.sleeping = true; // Use the sleeps
                player.updateSize();
                GlStateManager.translate(- player.renderOffsetX, - player.renderOffsetY, - player.renderOffsetZ);
                GlStateManager.rotate(player.getBedOrientationInDegrees(), 0, - 1, 0);
                GlStateManager.rotate(270, 0, - 1, 0);
            }
            GlStateManager.translate(3/8, 1/4 , 0);
            GlStateManager.rotate(event.getEntity().getRidingEntity().rotationYaw, 0, - 1, 0);
            IBedVehicle bedVehicle = event.getEntity().getRidingEntity().getCapability(Subject3Capabilities.BED_VEHICLE, null);
            GlStateManager.rotate(bedVehicle.getBedVehicleRotY(), 0, 1, 0);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    public static void postEntityRender(RenderLivingEvent.Post event){
        if (event.getEntity().isRiding() && event.getEntity().getRidingEntity().hasCapability(Subject3Capabilities.BED_VEHICLE, null)) {
            IBedVehicle bedVehicle = event.getEntity().getRidingEntity().getCapability(Subject3Capabilities.BED_VEHICLE, null);     
            GlStateManager.rotate(event.getEntity().getRidingEntity().rotationYaw, 0, 1, 0);
            GlStateManager.rotate(bedVehicle.getBedVehicleRotY(), 0, - 1, 0);
            GlStateManager.translate(8/3, 4/1 , 0);
            if (event.getEntity() instanceof EntityPlayer) {
                EntityPlayer player = ((EntityPlayer)event.getEntity());
                player.sleeping = false; // close the sleep GUI
                player.updateSize();
                GlStateManager.rotate(270, 0, 1, 0);
                GlStateManager.rotate(player.getBedOrientationInDegrees(), 0, 1, 0);
                GlStateManager.translate(player.renderOffsetX, player.renderOffsetY, player.renderOffsetZ);
            }
            GlStateManager.popMatrix();
        }
    }

}
