package com.parking.system.service.impl;

import com.parking.system.dto.PaymentDto;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.Payment;
import com.parking.system.enums.PaymentStatus;
import com.parking.system.enums.PaymentType;
import com.parking.system.exception.EntityNotFoundException;
import com.parking.system.exception.PaymentException;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.PaymentRepository;
import com.parking.system.service.PaymentService;
import com.parking.system.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ParkingSessionRepository sessionRepository;

    @Autowired
    private WalletService walletService;

    @Override
    @Transactional
    public PaymentDto processPayment(Long sessionId, String paymentTypeStr) {
        ParkingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));

        if (session.getPaymentStatus() == PaymentStatus.PAID) {
            throw new PaymentException("Payment has already been made for this session.");
        }

        if (session.getTotalAmount() == null) {
            throw new PaymentException("Bill has not been generated for this session yet.");
        }

        PaymentType type;
        try {
            type = PaymentType.valueOf(paymentTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new PaymentException("Invalid payment type: " + paymentTypeStr);
        }

        Payment payment = Payment.builder()
                .user(session.getUser())
                .parkingSession(session)
                .amount(session.getTotalAmount())
                .paymentType(type)
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId(UUID.randomUUID().toString())
                .paymentDate(LocalDateTime.now())
                .build();

        if (type == PaymentType.WALLET) {
            try {
                walletService.deductMoney(session.getUser().getId(), session.getTotalAmount(), 
                        "Paid for parking session " + sessionId);
                payment.setPaymentStatus(PaymentStatus.PAID);
                session.setPaymentStatus(PaymentStatus.PAID);
                // Also free the slot automatically after payment
                session.getParkingSlot().setOccupied(false);
            } catch (Exception e) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment = paymentRepository.save(payment);
                throw new PaymentException("Wallet payment failed: " + e.getMessage());
            }
        } else if (type == PaymentType.PAY_NOW) {
            // Assume external gateway integration success
            payment.setPaymentStatus(PaymentStatus.PAID);
            session.setPaymentStatus(PaymentStatus.PAID);
            session.getParkingSlot().setOccupied(false);
        } else if (type == PaymentType.PAY_LATER) {
            // Pay later keeps it pending and slot might remain occupied or become freed depending on policy
            // SRS says: Pay later - Release slot.
            session.getParkingSlot().setOccupied(false);
        }

        sessionRepository.save(session);
        payment = paymentRepository.save(payment);

        return mapToDto(payment);
    }

    @Override
    public PaymentDto getPaymentDetails(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with ID: " + paymentId));
        return mapToDto(payment);
    }

    @Override
    public List<PaymentDto> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getPendingPayments() {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PENDING)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private PaymentDto mapToDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setUserId(payment.getUser().getId());
        dto.setSessionId(payment.getParkingSession().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymentType());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setTransactionId(payment.getTransactionId());
        return dto;
    }
}
