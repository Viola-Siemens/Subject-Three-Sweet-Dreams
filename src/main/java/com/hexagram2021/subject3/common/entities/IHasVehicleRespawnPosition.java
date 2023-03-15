package com.hexagram2021.subject3.common.entities;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IHasVehicleRespawnPosition {
	@Nullable
	UUID getBedVehicleUUID();
	void setBedVehicleUUID(@Nullable UUID uuid);

	@Nullable
	IBedVehicle getRidingBedVehicle();
	void setRidingBedVehicleUUID(@Nullable IBedVehicle entity);
}
