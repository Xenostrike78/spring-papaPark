package com.parking.system.controller;

import com.parking.system.dto.ParkingSessionDto;
import com.parking.system.dto.UserDto;
import com.parking.system.enums.PaymentStatus;
import com.parking.system.enums.ParkingStatus;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.PaymentRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.security.UserDetailsImpl;
import com.parking.system.service.ParkingService;
import com.parking.system.service.UserService;
import com.parking.system.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingSessionRepository sessionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ParkingSlotRepository slotRepository;

    /**
     * Get the currently authenticated user's profile.
     */
    @GetMapping("/users/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDetailsImpl userDetails = getCurrentUserDetails();
        UserDto userDto = userService.getUserProfile(userDetails.getId());
        return ResponseEntity.ok(userDto);
    }

    /**
     * User dashboard stats.
     */
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserDashboardStats() {
        UserDetailsImpl userDetails = getCurrentUserDetails();
        Long userId = userDetails.getId();

        List<ParkingSessionDto> sessions = parkingService.getUserSessions(userId);
        long activeSessions = sessions.stream()
                .filter(s -> s.getStatus() == ParkingStatus.ACTIVE)
                .count();

        BigDecimal totalSpent = paymentRepository.findByUserId(userId).stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PAID)
                .map(p -> p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal walletBalance = walletService.getWalletBalance(userId);

        long vehicleCount = userRepository.findById(userId)
                .map(u -> (u.getVehicles() != null ? (long) u.getVehicles().size() : 0L))
                .orElse(0L);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVehicles", vehicleCount);
        stats.put("activeSessions", activeSessions);
        stats.put("totalSpent", totalSpent);
        stats.put("walletBalance", walletBalance);
        return ResponseEntity.ok(stats);
    }

    /**
     * Admin dashboard stats.
     */
    @GetMapping("/admin/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminDashboardStats() {
        long totalUsers = userRepository.count();
        long totalSlots = slotRepository.count();

        long activeSessions = sessionRepository.findAll().stream()
                .filter(s -> s.getStatus() == ParkingStatus.ACTIVE)
                .count();

        BigDecimal totalRevenue = paymentRepository.findAll().stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PAID)
                .map(p -> p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalSessions = sessionRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalSlots", totalSlots);
        stats.put("totalSessions", totalSessions);
        stats.put("activeSessions", activeSessions);
        stats.put("totalRevenue", totalRevenue);
        return ResponseEntity.ok(stats);
    }

    /**
     * Admin recent activity feed - returns recent parking sessions as activity.
     */
    @GetMapping("/admin/dashboard/activity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAdminActivity() {
        List<Map<String, Object>> activity = sessionRepository.findAll().stream()
                .sorted((a, b) -> b.getEntryTime().compareTo(a.getEntryTime()))
                .limit(10)
                .map(s -> {
                    Map<String, Object> act = new HashMap<>();
                    act.put("type", s.getStatus() == ParkingStatus.ACTIVE ? "PARKING" : "COMPLETED");
                    act.put("description", "Vehicle " + s.getVehicle().getVehicleNumber()
                            + " parked at slot " + s.getParkingSlot().getSlotNumber()
                            + " by " + s.getUser().getFullName());
                    act.put("timestamp", s.getEntryTime());
                    return act;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(activity);
    }

    /**
     * Admin: Get all active sessions (for admin sessions page).
     */
    @GetMapping("/admin/sessions/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ParkingSessionDto>> getAdminActiveSessions() {
        List<ParkingSessionDto> sessions = parkingService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Admin: Force end a session.
     */
    @PostMapping("/admin/sessions/{sessionId}/end")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingSessionDto> forceEndSession(@PathVariable Long sessionId) {
        ParkingSessionDto session = parkingService.endParkingSession(sessionId);
        return ResponseEntity.ok(session);
    }

    /**
     * Admin: Get all users.
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsersAdmin() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Admin: Block or unblock a user.
     */
    @PostMapping("/admin/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> setUserStatus(@PathVariable Long id, @RequestParam boolean enable) {
        if (enable) {
            userService.enableUser(id);
            return ResponseEntity.ok("User enabled successfully");
        } else {
            userService.blockUserIfDuesPending(id);
            return ResponseEntity.ok("User blocked successfully");
        }
    }

    private UserDetailsImpl getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }
}
