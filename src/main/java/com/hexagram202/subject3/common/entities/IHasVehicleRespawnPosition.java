package com.hexagram202.subject3.common.entities;

import jakarta.annotation.Nullable;

import java.util.UUID;

public interface IHasVehicleRespawnPosition {
    @Nullable
    UUID getBedVehicleUUID();
    void setBedVehicleUUID(@Nullable UUID uuid);
}
