package com.parking.system.controller;

import com.parking.system.dto.ParkingSlotDto;
import com.parking.system.enums.VehicleType;
import com.parking.system.service.ParkingSlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ParkingSlotService parkingSlotService;

    // ========== Slots endpoints ==========

    @PostMapping("/slots")
    public ResponseEntity<ParkingSlotDto> createSlot(@Valid @RequestBody ParkingSlotDto slotDto) {
        ParkingSlotDto slot = parkingSlotService.createSlot(slotDto);
        return ResponseEntity.ok(slot);
    }

    @PostMapping("/slots/create")
    public ResponseEntity<ParkingSlotDto> createSlotAlt(@Valid @RequestBody ParkingSlotDto slotDto) {
        ParkingSlotDto slot = parkingSlotService.createSlot(slotDto);
        return ResponseEntity.ok(slot);
    }

    @PutMapping("/slots/{id}")
    public ResponseEntity<ParkingSlotDto> updateSlot(@PathVariable Long id,
                                                      @Valid @RequestBody ParkingSlotDto slotDto) {
        ParkingSlotDto slot = parkingSlotService.updateSlot(id, slotDto);
        return ResponseEntity.ok(slot);
    }

    @PutMapping("/slots/update/{id}")
    public ResponseEntity<ParkingSlotDto> updateSlotAlt(@PathVariable Long id,
                                                          @Valid @RequestBody ParkingSlotDto slotDto) {
        ParkingSlotDto slot = parkingSlotService.updateSlot(id, slotDto);
        return ResponseEntity.ok(slot);
    }

    @DeleteMapping("/slots/{id}")
    public ResponseEntity<String> deleteSlot(@PathVariable Long id) {
        parkingSlotService.deleteSlot(id);
        return ResponseEntity.ok("Parking slot deleted successfully");
    }

    @DeleteMapping("/slots/delete/{id}")
    public ResponseEntity<String> deleteSlotAlt(@PathVariable Long id) {
        parkingSlotService.deleteSlot(id);
        return ResponseEntity.ok("Parking slot deleted successfully");
    }

    /**
     * Get all slots. If ?zone=X is provided, filter by zone.
     */
    @GetMapping("/slots")
    public ResponseEntity<List<ParkingSlotDto>> getSlots(@RequestParam(required = false) String zone) {
        List<ParkingSlotDto> slots;
        if (zone != null && !zone.isEmpty()) {
            slots = parkingSlotService.getSlotsByZone(zone);
        } else {
            slots = parkingSlotService.getAllSlots();
        }
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/slots/all")
    public ResponseEntity<List<ParkingSlotDto>> getAllSlots() {
        List<ParkingSlotDto> slots = parkingSlotService.getAllSlots();
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/slots/zone/{zone}")
    public ResponseEntity<List<ParkingSlotDto>> getSlotsByZone(@PathVariable String zone) {
        List<ParkingSlotDto> slots = parkingSlotService.getSlotsByZone(zone);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/slots/available")
    public ResponseEntity<ParkingSlotDto> getNearestAvailableSlot(@RequestParam VehicleType vehicleType) {
        ParkingSlotDto slot = parkingSlotService.getNearestAvailableSlot(vehicleType);
        return ResponseEntity.ok(slot);
    }
}
