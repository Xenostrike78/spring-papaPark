package com.parking.system.controller;

import com.parking.system.dto.ParkingSessionDto;
import com.parking.system.enums.ParkingStatus;
import com.parking.system.security.UserDetailsImpl;
import com.parking.system.service.ParkingService;
import com.parking.system.service.ParkingSlotService;
import com.parking.system.dto.ParkingSlotDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private ParkingSlotService parkingSlotService;

    /**
     * Start a parking session. Accepts vehicleId and slotId as request params.
     */
    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ParkingSessionDto> startParkingSession(@RequestParam Long vehicleId,
                                                                  @RequestParam Long slotId) {
        ParkingSessionDto session = parkingService.startParkingSession(vehicleId, slotId);
        return ResponseEntity.ok(session);
    }

    /**
     * End a parking session by its ID.
     */
    @PostMapping("/end/{sessionId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ParkingSessionDto> endParkingSession(@PathVariable Long sessionId) {
        ParkingSessionDto session = parkingService.endParkingSession(sessionId);
        return ResponseEntity.ok(session);
    }

    /**
     * Get a specific session by ID.
     */
    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ParkingSessionDto> getSessionById(@PathVariable Long sessionId) {
        ParkingSessionDto session = parkingService.getSessionById(sessionId);
        return ResponseEntity.ok(session);
    }

    /**
     * Get all active sessions for current user.
     */
    @GetMapping("/sessions/active")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ParkingSessionDto>> getMyActiveSessions() {
        UserDetailsImpl userDetails = getCurrentUserDetails();
        List<ParkingSessionDto> sessions = parkingService.getUserSessions(userDetails.getId())
                .stream()
                .filter(s -> s.getStatus() == ParkingStatus.ACTIVE)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sessions);
    }

    /**
     * Get parking history (past sessions) for current user.
     */
    @GetMapping("/sessions/history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ParkingSessionDto>> getMySessionHistory() {
        UserDetailsImpl userDetails = getCurrentUserDetails();
        List<ParkingSessionDto> sessions = parkingService.getUserSessions(userDetails.getId());
        return ResponseEntity.ok(sessions);
    }

    /**
     * Get all sessions for a user (admin use).
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ParkingSessionDto>> getUserSessions(@PathVariable Long userId) {
        List<ParkingSessionDto> sessions = parkingService.getUserSessions(userId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Admin: Get all currently active sessions.
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ParkingSessionDto>> getAllActiveSessions() {
        List<ParkingSessionDto> sessions = parkingService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Check if a user has pending dues.
     */
    @GetMapping("/has-dues/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Boolean> hasPendingDues(@PathVariable Long userId) {
        boolean hasDues = parkingService.hasPendingDues(userId);
        return ResponseEntity.ok(hasDues);
    }

    /**
     * Get all available parking slots (not occupied).
     */
    @GetMapping("/slots/available")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ParkingSlotDto>> getAvailableSlots() {
        List<ParkingSlotDto> slots = parkingSlotService.getAvailableSlots();
        return ResponseEntity.ok(slots);
    }

    private UserDetailsImpl getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }
}
