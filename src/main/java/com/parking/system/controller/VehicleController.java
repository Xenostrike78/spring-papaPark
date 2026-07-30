package com.parking.system.controller;

import com.parking.system.dto.VehicleDto;
import com.parking.system.security.UserDetailsImpl;
import com.parking.system.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * Add a vehicle for the currently authenticated user.
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<VehicleDto> addVehicle(@Valid @RequestBody VehicleDto vehicleDto,
                                                  @RequestParam(required = false) Long userId) {
        if (userId == null) {
            userId = getCurrentUserDetails().getId();
        }
        VehicleDto vehicle = vehicleService.addVehicle(vehicleDto, userId);
        return ResponseEntity.ok(vehicle);
    }

    /**
     * Get all vehicles for the currently authenticated user.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<VehicleDto>> getMyVehicles() {
        Long userId = getCurrentUserDetails().getId();
        List<VehicleDto> vehicles = vehicleService.getVehiclesByUserId(userId);
        return ResponseEntity.ok(vehicles);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<VehicleDto> updateVehicle(@PathVariable Long id,
                                                     @Valid @RequestBody VehicleDto vehicleDto) {
        VehicleDto vehicle = vehicleService.updateVehicle(id, vehicleDto);
        return ResponseEntity.ok(vehicle);
    }

    /**
     * Update vehicle - also support PUT to /{id} directly (used by frontend).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<VehicleDto> updateVehicleById(@PathVariable Long id,
                                                         @Valid @RequestBody VehicleDto vehicleDto) {
        VehicleDto vehicle = vehicleService.updateVehicle(id, vehicleDto);
        return ResponseEntity.ok(vehicle);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok("Vehicle deleted successfully");
    }

    /**
     * Delete vehicle - also support DELETE to /{id} directly (used by frontend).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> deleteVehicleById(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok("Vehicle deleted successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Long id) {
        VehicleDto vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<VehicleDto>> getVehiclesByUserId(@PathVariable Long userId) {
        List<VehicleDto> vehicles = vehicleService.getVehiclesByUserId(userId);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<VehicleDto>> searchVehicles(@RequestParam String keyword) {
        List<VehicleDto> vehicles = vehicleService.searchVehicles(keyword);
        return ResponseEntity.ok(vehicles);
    }

    private UserDetailsImpl getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }
}
