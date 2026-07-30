package com.parking.system.controller;

import com.parking.system.dto.WalletTransactionDto;
import com.parking.system.security.UserDetailsImpl;
import com.parking.system.service.WalletService;
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
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    /**
     * Add money to the currently authenticated user's wallet.
     * Accepts JSON body: { "amount": 100.00 }
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WalletTransactionDto> addMoneyCurrentUser(@RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserDetails().getId();
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        WalletTransactionDto transaction = walletService.addMoney(userId, amount);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Add money - admin or explicit userId version.
     */
    @PostMapping("/add-money")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WalletTransactionDto> addMoney(@RequestParam Long userId,
                                                          @RequestParam BigDecimal amount) {
        WalletTransactionDto transaction = walletService.addMoney(userId, amount);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/deduct-money")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WalletTransactionDto> deductMoney(@RequestParam Long userId,
                                                             @RequestParam BigDecimal amount,
                                                             @RequestParam String remarks) {
        WalletTransactionDto transaction = walletService.deductMoney(userId, amount, remarks);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Get wallet balance for the current user.
     * Returns { "balance": 500.00 }
     */
    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getMyWalletBalance() {
        Long userId = getCurrentUserDetails().getId();
        BigDecimal balance = walletService.getWalletBalance(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("balance", balance);
        return ResponseEntity.ok(result);
    }

    /**
     * Get wallet balance for a specific user (by path variable).
     */
    @GetMapping("/balance/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BigDecimal> getWalletBalance(@PathVariable Long userId) {
        BigDecimal balance = walletService.getWalletBalance(userId);
        return ResponseEntity.ok(balance);
    }

    /**
     * Get wallet transaction history for the current user.
     */
    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<WalletTransactionDto>> getMyTransactions() {
        Long userId = getCurrentUserDetails().getId();
        List<WalletTransactionDto> history = walletService.getWalletHistory(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get wallet transaction history for a specific user.
     */
    @GetMapping("/history/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<WalletTransactionDto>> getWalletHistory(@PathVariable Long userId) {
        List<WalletTransactionDto> history = walletService.getWalletHistory(userId);
        return ResponseEntity.ok(history);
    }

    private UserDetailsImpl getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }
}
