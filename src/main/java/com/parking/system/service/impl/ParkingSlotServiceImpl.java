package com.parking.system.service.impl;

import com.parking.system.dto.ParkingSlotDto;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.enums.VehicleType;
import com.parking.system.exception.EntityNotFoundException;
import com.parking.system.exception.ValidationException;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.service.ParkingSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingSlotServiceImpl implements ParkingSlotService {

    @Autowired
    private ParkingSlotRepository slotRepository;

    @Override
    public ParkingSlotDto createSlot(ParkingSlotDto slotDto) {
        if (slotRepository.findBySlotNumber(slotDto.getSlotNumber()).isPresent()) {
            throw new ValidationException("Slot number already exists!");
        }

        ParkingSlot slot = ParkingSlot.builder()
                .slotNumber(slotDto.getSlotNumber())
                .zone(slotDto.getZone())
                .vehicleType(slotDto.getVehicleType())
                .occupied(false)
                .build();

        slot = slotRepository.save(slot);
        return mapToDto(slot);
    }

    @Override
    public ParkingSlotDto updateSlot(Long id, ParkingSlotDto slotDto) {
        ParkingSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found with ID: " + id));

        slot.setSlotNumber(slotDto.getSlotNumber());
        slot.setZone(slotDto.getZone());
        slot.setVehicleType(slotDto.getVehicleType());
        
        slot = slotRepository.save(slot);
        return mapToDto(slot);
    }

    @Override
    public void deleteSlot(Long id) {
        if (!slotRepository.existsById(id)) {
            throw new EntityNotFoundException("Slot not found with ID: " + id);
        }
        slotRepository.deleteById(id);
    }

    @Override
    public List<ParkingSlotDto> getAllSlots() {
        return slotRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParkingSlotDto> getSlotsByZone(String zone) {
        return slotRepository.findByZone(zone).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParkingSlotDto getNearestAvailableSlot(VehicleType vehicleType) {
        List<ParkingSlot> availableSlots = slotRepository.findByOccupiedFalseAndVehicleType(vehicleType);
        if (availableSlots.isEmpty()) {
            throw new ValidationException("No available slots found for vehicle type: " + vehicleType);
        }
        // Simplified "nearest" logic: just take the first one available
        return mapToDto(availableSlots.get(0));
    }

    @Override
    public List<ParkingSlotDto> getAvailableSlots() {
        return slotRepository.findAll().stream()
                .filter(s -> !s.isOccupied())
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ParkingSlotDto mapToDto(ParkingSlot slot) {
        ParkingSlotDto dto = new ParkingSlotDto();
        dto.setId(slot.getId());
        dto.setSlotNumber(slot.getSlotNumber());
        dto.setZone(slot.getZone());
        dto.setVehicleType(slot.getVehicleType());
        dto.setOccupied(slot.isOccupied());
        return dto;
    }
}
