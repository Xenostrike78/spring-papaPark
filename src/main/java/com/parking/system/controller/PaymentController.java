package com.parking.system.controller;

import com.parking.system.dto.PaymentDto;
import com.parking.system.enums.PaymentStatus;
import com.parking.system.repository.PaymentRepository;
import com.parking.system.security.UserDetailsImpl;
import com.parking.system.service.PaymentService;
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

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/process")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaymentDto> processPayment(@RequestParam Long sessionId,
                                                      @RequestParam String paymentType) {
        PaymentDto payment = paymentService.processPayment(sessionId, paymentType);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaymentDto> getPaymentDetails(@PathVariable Long id) {
        PaymentDto payment = paymentService.getPaymentDetails(id);
        return ResponseEntity.ok(payment);
    }

    /**
     * Get payment history for the currently authenticated user.
     */
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PaymentDto>> getMyPaymentHistory() {
        Long userId = getCurrentUserDetails().getId();
        List<PaymentDto> payments = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get payment stats for the current user: total paid, pending amount.
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getMyPaymentStats() {
        Long userId = getCurrentUserDetails().getId();
        List<PaymentDto> payments = paymentService.getUserPayments(userId);

        BigDecimal totalPaid = payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PAID)
                .map(PaymentDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingAmount = payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PENDING)
                .map(PaymentDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPaid", totalPaid);
        stats.put("pendingAmount", pendingAmount);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PaymentDto>> getUserPayments(@PathVariable Long userId) {
        List<PaymentDto> payments = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentDto>> getPendingPayments() {
        List<PaymentDto> payments = paymentService.getPendingPayments();
        return ResponseEntity.ok(payments);
    }

    private UserDetailsImpl getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }
}
