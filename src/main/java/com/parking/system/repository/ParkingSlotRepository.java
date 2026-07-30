package com.parking.system.repository;

import com.parking.system.entity.ParkingSlot;
import com.parking.system.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    Optional<ParkingSlot> findBySlotNumber(String slotNumber);
    List<ParkingSlot> findByZone(String zone);
    List<ParkingSlot> findByOccupiedFalseAndVehicleType(VehicleType vehicleType);
}
