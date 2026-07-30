package com.parking.system.service.impl;

import com.parking.system.dto.VehicleDto;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.exception.EntityNotFoundException;
import com.parking.system.exception.ValidationException;
import com.parking.system.repository.UserRepository;
import com.parking.system.repository.VehicleRepository;
import com.parking.system.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public VehicleDto addVehicle(VehicleDto vehicleDto, Long userId) {
        if (vehicleRepository.existsByVehicleNumber(vehicleDto.getVehicleNumber())) {
            throw new ValidationException("Vehicle with this number already exists!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(vehicleDto.getVehicleNumber())
                .vehicleType(vehicleDto.getVehicleType())
                .model(vehicleDto.getModel())
                .color(vehicleDto.getColor())
                .user(user)
                .build();

        vehicle = vehicleRepository.save(vehicle);
        return mapToDto(vehicle);
    }

    @Override
    public VehicleDto updateVehicle(Long id, VehicleDto vehicleDto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + id));

        // Check if updating to an existing number
        if (!vehicle.getVehicleNumber().equals(vehicleDto.getVehicleNumber()) && 
            vehicleRepository.existsByVehicleNumber(vehicleDto.getVehicleNumber())) {
            throw new ValidationException("Another vehicle with this number already exists!");
        }

        vehicle.setVehicleNumber(vehicleDto.getVehicleNumber());
        vehicle.setVehicleType(vehicleDto.getVehicleType());
        vehicle.setModel(vehicleDto.getModel());
        vehicle.setColor(vehicleDto.getColor());

        vehicle = vehicleRepository.save(vehicle);
        return mapToDto(vehicle);
    }

    @Override
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new EntityNotFoundException("Vehicle not found with ID: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    @Override
    public VehicleDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + id));
        return mapToDto(vehicle);
    }

    @Override
    public List<VehicleDto> getVehiclesByUserId(Long userId) {
        return vehicleRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDto> searchVehicles(String keyword) {
        // Basic search can be implemented or we can just fetch all and filter for now
        // For production, we'd add a custom JPQL query in the repository
        return vehicleRepository.findAll().stream()
                .filter(v -> v.getVehicleNumber().contains(keyword))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private VehicleDto mapToDto(Vehicle vehicle) {
        VehicleDto dto = new VehicleDto();
        dto.setId(vehicle.getId());
        dto.setVehicleNumber(vehicle.getVehicleNumber());
        dto.setVehicleType(vehicle.getVehicleType());
        dto.setModel(vehicle.getModel());
        dto.setColor(vehicle.getColor());
        dto.setUserId(vehicle.getUser().getId());
        return dto;
    }
}
