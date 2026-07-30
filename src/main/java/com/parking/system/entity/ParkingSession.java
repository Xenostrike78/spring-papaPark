package com.parking.system.entity;

import com.parking.system.enums.ParkingStatus;
import com.parking.system.enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_sessions")
public class ParkingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private ParkingSlot parkingSlot;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;
    private Integer totalHours;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingStatus status = ParkingStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @OneToOne(mappedBy = "parkingSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    // Constructors
    public ParkingSession() {}

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user;
        private Vehicle vehicle;
        private ParkingSlot parkingSlot;
        private LocalDateTime entryTime;
        private ParkingStatus status = ParkingStatus.ACTIVE;
        private PaymentStatus paymentStatus = PaymentStatus.PENDING;

        public Builder user(User user) { this.user = user; return this; }
        public Builder vehicle(Vehicle vehicle) { this.vehicle = vehicle; return this; }
        public Builder parkingSlot(ParkingSlot parkingSlot) { this.parkingSlot = parkingSlot; return this; }
        public Builder entryTime(LocalDateTime entryTime) { this.entryTime = entryTime; return this; }
        public Builder status(ParkingStatus status) { this.status = status; return this; }
        public Builder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }

        public ParkingSession build() {
            ParkingSession s = new ParkingSession();
            s.user = this.user;
            s.vehicle = this.vehicle;
            s.parkingSlot = this.parkingSlot;
            s.entryTime = this.entryTime;
            s.status = this.status;
            s.paymentStatus = this.paymentStatus;
            return s;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public ParkingSlot getParkingSlot() { return parkingSlot; }
    public void setParkingSlot(ParkingSlot parkingSlot) { this.parkingSlot = parkingSlot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public Integer getTotalHours() { return totalHours; }
    public void setTotalHours(Integer totalHours) { this.totalHours = totalHours; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public ParkingStatus getStatus() { return status; }
    public void setStatus(ParkingStatus status) { this.status = status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
}
