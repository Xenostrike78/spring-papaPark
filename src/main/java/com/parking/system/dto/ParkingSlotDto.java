package com.parking.system.dto;

import com.parking.system.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ParkingSlotDto {
    private Long id;

    @NotBlank(message = "Slot number is required")
    private String slotNumber;

    @NotBlank(message = "Zone is required")
    private String zone;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    private boolean occupied;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
}
