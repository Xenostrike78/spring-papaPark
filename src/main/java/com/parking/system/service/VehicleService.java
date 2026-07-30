package com.parking.system.service;

import com.parking.system.dto.VehicleDto;

import java.util.List;

public interface VehicleService {
    VehicleDto addVehicle(VehicleDto vehicleDto, Long userId);
    VehicleDto updateVehicle(Long id, VehicleDto vehicleDto);
    void deleteVehicle(Long id);
    VehicleDto getVehicleById(Long id);
    List<VehicleDto> getVehiclesByUserId(Long userId);
    List<VehicleDto> searchVehicles(String keyword);
}
