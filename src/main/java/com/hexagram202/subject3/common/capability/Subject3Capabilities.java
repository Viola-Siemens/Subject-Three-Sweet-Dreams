package com.hexagram202.subject3.common.capability;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Subject3Capabilities {

    @CapabilityInject(IHasVehicleRespawnPosition.class)
    public static Capability<IHasVehicleRespawnPosition> HAS_VEHICLE_RESPAWN_POSITION = null;

    @CapabilityInject(IBedVehicle.class)
    public static Capability<IBedVehicle> BED_VEHICLE = null;

}
