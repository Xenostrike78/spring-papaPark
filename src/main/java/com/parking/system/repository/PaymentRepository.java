package com.parking.system.repository;

import com.parking.system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByParkingSessionId(Long sessionId);
}
