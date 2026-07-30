package com.parking.system.dto;

import com.parking.system.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletTransactionDto {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private TransactionType type;
    private String remarks;
    private LocalDateTime transactionDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}
