package com.parking.system.service;

import com.parking.system.dto.ParkingSessionDto;

import java.util.List;

public interface ParkingService {
    ParkingSessionDto startParkingSession(Long vehicleId, Long slotId);
    ParkingSessionDto endParkingSession(Long sessionId);
    ParkingSessionDto getSessionById(Long sessionId);
    List<ParkingSessionDto> getActiveSessions();
    List<ParkingSessionDto> getUserSessions(Long userId);
    boolean hasPendingDues(Long userId);
}
