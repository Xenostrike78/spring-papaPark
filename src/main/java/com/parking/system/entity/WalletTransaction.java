package com.parking.system.entity;

import com.parking.system.enums.TransactionType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime transactionDate;

    private String remarks;

    // Constructors
    public WalletTransaction() {}

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user;
        private BigDecimal amount;
        private TransactionType type;
        private String remarks;

        public Builder user(User user) { this.user = user; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder type(TransactionType type) { this.type = type; return this; }
        public Builder remarks(String remarks) { this.remarks = remarks; return this; }

        public WalletTransaction build() {
            WalletTransaction wt = new WalletTransaction();
            wt.user = this.user;
            wt.amount = this.amount;
            wt.type = this.type;
            wt.remarks = this.remarks;
            return wt;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
