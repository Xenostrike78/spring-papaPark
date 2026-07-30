package com.parking.system.service.impl;

import com.parking.system.dto.ParkingSessionDto;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.enums.ParkingStatus;
import com.parking.system.enums.PaymentStatus;
import com.parking.system.exception.EntityNotFoundException;
import com.parking.system.exception.ParkingException;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.repository.VehicleRepository;
import com.parking.system.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParkingServiceImpl implements ParkingService {

    @Autowired
    private ParkingSessionRepository sessionRepository;

    @Autowired
    private ParkingSlotRepository slotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    private static final BigDecimal FIRST_HOUR_RATE = new BigDecimal("30");
    private static final BigDecimal ADDITIONAL_HOUR_RATE = new BigDecimal("20");

    @Override
    @Transactional
    public ParkingSessionDto startParkingSession(Long vehicleId, Long slotId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + vehicleId));

        User user = vehicle.getUser();

        // Prevent duplicate active parking
        Optional<ParkingSession> activeSession = sessionRepository.findByVehicleIdAndStatus(vehicleId,
                ParkingStatus.ACTIVE);
        if (activeSession.isPresent()) {
            throw new ParkingException("This vehicle already has an active parking session!");
        }

        // Check pending dues
        if (hasPendingDues(user.getId())) {
            throw new ParkingException("Cannot start session. User has overdue payments!");
        }

        ParkingSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found with ID: " + slotId));

        if (slot.isOccupied()) {
            throw new ParkingException("Selected slot is already occupied.");
        }

        if (slot.getVehicleType() != vehicle.getVehicleType()) {
            throw new ParkingException("Slot type does not match vehicle type.");
        }

        slot.setOccupied(true);
        slotRepository.save(slot);

        ParkingSession session = ParkingSession.builder()
                .user(user)
                .vehicle(vehicle)
                .parkingSlot(slot)
                .entryTime(LocalDateTime.now())
                .status(ParkingStatus.ACTIVE)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        session = sessionRepository.save(session);
        return mapToDto(session);
    }

    @Override
    @Transactional
    public ParkingSessionDto endParkingSession(Long sessionId) {
        ParkingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));

        if (session.getStatus() == ParkingStatus.COMPLETED) {
            throw new ParkingException("Session is already completed.");
        }

        session.setExitTime(LocalDateTime.now());
        session.setStatus(ParkingStatus.COMPLETED);

        // Calculate hours
        long minutes = Duration.between(session.getEntryTime(), session.getExitTime()).toMinutes();
        int hours = (int) Math.ceil(minutes / 60.0);
        if (hours == 0)
            hours = 1; // Minimum 1 hour
        session.setTotalHours(hours);

        // Calculate Amount
        BigDecimal totalAmount = FIRST_HOUR_RATE;
        if (hours > 1) {
            BigDecimal additionalAmount = ADDITIONAL_HOUR_RATE.multiply(new BigDecimal(hours - 1));
            totalAmount = totalAmount.add(additionalAmount);
        }
        session.setTotalAmount(totalAmount);

        session = sessionRepository.save(session);
        return mapToDto(session);
    }

    @Override
    public ParkingSessionDto getSessionById(Long sessionId) {
        ParkingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));
        return mapToDto(session);
    }

    @Override
    public List<ParkingSessionDto> getActiveSessions() {
        return sessionRepository.findAll().stream()
                .filter(s -> s.getStatus() == ParkingStatus.ACTIVE)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParkingSessionDto> getUserSessions(Long userId) {
        return sessionRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasPendingDues(Long userId) {
        // Simplified check: any unpaid sessions older than 7 days
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        return sessionRepository.findByUserId(userId).stream()
                .anyMatch(s -> s.getPaymentStatus() == PaymentStatus.PENDING
                        && s.getExitTime() != null
                        && s.getExitTime().isBefore(oneWeekAgo));
    }

    private ParkingSessionDto mapToDto(ParkingSession session) {
        ParkingSessionDto dto = new ParkingSessionDto();
        dto.setId(session.getId());
        dto.setUserId(session.getUser().getId());
        dto.setUserName(session.getUser().getFullName());
        dto.setVehicleId(session.getVehicle().getId());
        dto.setVehicleNumber(session.getVehicle().getVehicleNumber());
        dto.setSlotId(session.getParkingSlot().getId());
        dto.setSlotNumber(session.getParkingSlot().getSlotNumber());
        dto.setEntryTime(session.getEntryTime());
        dto.setExitTime(session.getExitTime());
        dto.setTotalHours(session.getTotalHours());
        dto.setTotalAmount(session.getTotalAmount());
        dto.setStatus(session.getStatus());
        dto.setPaymentStatus(session.getPaymentStatus());
        return dto;
    }
}
