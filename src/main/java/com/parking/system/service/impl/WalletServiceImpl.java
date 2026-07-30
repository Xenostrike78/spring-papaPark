package com.parking.system.service.impl;

import com.parking.system.dto.WalletTransactionDto;
import com.parking.system.entity.User;
import com.parking.system.entity.WalletTransaction;
import com.parking.system.enums.TransactionType;
import com.parking.system.exception.EntityNotFoundException;
import com.parking.system.exception.PaymentException;
import com.parking.system.repository.UserRepository;
import com.parking.system.repository.WalletTransactionRepository;
import com.parking.system.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletTransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public WalletTransactionDto addMoney(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException("Amount must be greater than zero");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        user.setWalletBalance(user.getWalletBalance().add(amount));
        userRepository.save(user);

        WalletTransaction tx = WalletTransaction.builder()
                .user(user)
                .amount(amount)
                .type(TransactionType.CREDIT)
                .remarks("Added money to wallet")
                .build();

        tx = transactionRepository.save(tx);
        return mapToDto(tx);
    }

    @Override
    @Transactional
    public WalletTransactionDto deductMoney(Long userId, BigDecimal amount, String remarks) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException("Amount must be greater than zero");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (user.getWalletBalance().compareTo(amount) < 0) {
            throw new PaymentException("Insufficient wallet balance!");
        }

        user.setWalletBalance(user.getWalletBalance().subtract(amount));
        userRepository.save(user);

        WalletTransaction tx = WalletTransaction.builder()
                .user(user)
                .amount(amount)
                .type(TransactionType.DEBIT)
                .remarks(remarks)
                .build();

        tx = transactionRepository.save(tx);
        return mapToDto(tx);
    }

    @Override
    public BigDecimal getWalletBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return user.getWalletBalance();
    }

    @Override
    public List<WalletTransactionDto> getWalletHistory(Long userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private WalletTransactionDto mapToDto(WalletTransaction tx) {
        WalletTransactionDto dto = new WalletTransactionDto();
        dto.setId(tx.getId());
        dto.setUserId(tx.getUser().getId());
        dto.setAmount(tx.getAmount());
        dto.setType(tx.getType());
        dto.setRemarks(tx.getRemarks());
        dto.setTransactionDate(tx.getTransactionDate());
        return dto;
    }
}
