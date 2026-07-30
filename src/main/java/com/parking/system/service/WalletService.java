package com.parking.system.service;

import com.parking.system.dto.WalletTransactionDto;
import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    WalletTransactionDto addMoney(Long userId, BigDecimal amount);
    WalletTransactionDto deductMoney(Long userId, BigDecimal amount, String remarks);
    BigDecimal getWalletBalance(Long userId);
    List<WalletTransactionDto> getWalletHistory(Long userId);
}
