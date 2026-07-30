package com.parking.system.entity;

import com.parking.system.enums.PaymentStatus;
import com.parking.system.enums.PaymentType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private ParkingSession parkingSession;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime paymentDate;

    @Column(unique = true)
    private String transactionId;

    // Constructors
    public Payment() {}

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user;
        private ParkingSession parkingSession;
        private BigDecimal amount;
        private PaymentType paymentType;
        private PaymentStatus paymentStatus;
        private LocalDateTime paymentDate;
        private String transactionId;

        public Builder user(User user) { this.user = user; return this; }
        public Builder parkingSession(ParkingSession parkingSession) { this.parkingSession = parkingSession; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder paymentType(PaymentType paymentType) { this.paymentType = paymentType; return this; }
        public Builder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public Builder paymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; return this; }
        public Builder transactionId(String transactionId) { this.transactionId = transactionId; return this; }

        public Payment build() {
            Payment p = new Payment();
            p.user = this.user;
            p.parkingSession = this.parkingSession;
            p.amount = this.amount;
            p.paymentType = this.paymentType;
            p.paymentStatus = this.paymentStatus;
            p.paymentDate = this.paymentDate;
            p.transactionId = this.transactionId;
            return p;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public ParkingSession getParkingSession() { return parkingSession; }
    public void setParkingSession(ParkingSession parkingSession) { this.parkingSession = parkingSession; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
