package com.parking.system.entity;

import com.parking.system.enums.VehicleType;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    private String model;
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Vehicle() {}

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String vehicleNumber;
        private VehicleType vehicleType;
        private String model;
        private String color;
        private User user;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder vehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; return this; }
        public Builder vehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; return this; }
        public Builder model(String model) { this.model = model; return this; }
        public Builder color(String color) { this.color = color; return this; }
        public Builder user(User user) { this.user = user; return this; }

        public Vehicle build() {
            Vehicle v = new Vehicle();
            v.id = this.id;
            v.vehicleNumber = this.vehicleNumber;
            v.vehicleType = this.vehicleType;
            v.model = this.model;
            v.color = this.color;
            v.user = this.user;
            return v;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
