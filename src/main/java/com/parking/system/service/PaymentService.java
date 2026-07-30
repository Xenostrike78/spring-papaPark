package com.parking.system.service;

import com.parking.system.dto.PaymentDto;

import java.util.List;

public interface PaymentService {
    PaymentDto processPayment(Long sessionId, String paymentType);
    PaymentDto getPaymentDetails(Long paymentId);
    List<PaymentDto> getUserPayments(Long userId);
    List<PaymentDto> getPendingPayments();
}
