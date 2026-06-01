package com.jerry.wallet.controller;

import com.jerry.wallet.dto.CreateWalletDto;
import com.jerry.wallet.dto.FundWalletDto;
import com.jerry.wallet.dto.WalletTransferDto;
import com.jerry.wallet.pojo.WalletPojo;
import com.jerry.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<?> createWalletForUser(@RequestBody CreateWalletDto createWalletDto) {
        walletService.createWalletForUser(createWalletDto);
        return ResponseEntity.ok("Wallet created successfully");
    }

    @PostMapping("/{walletId}/fund")
    public ResponseEntity<?> fundWallet(@PathVariable String walletId, @RequestBody FundWalletDto fundWalletDto) {
        walletService.fundWallet(walletId, fundWalletDto);
        return ResponseEntity.ok("Wallet funded successfully");
    }

    @PostMapping("/{walletId}/transfer")
    public ResponseEntity<?> transferFunds(@PathVariable String walletId, @RequestBody WalletTransferDto walletTransferDto) {
        walletService.transferFunds(walletId, walletTransferDto);
        return ResponseEntity.ok("Funds transferred successfully");
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String walletId) {
        return ResponseEntity.ok(walletService.getBalance(walletId));
    }

    @GetMapping("/users/{userId}/wallets")
    public ResponseEntity<List<WalletPojo>> getUserWallets(@PathVariable String userId,
                                                           @RequestParam(defaultValue = "10", required = false) Long limit,
                                                           @RequestParam(defaultValue = "0", required = false) Long offset) {
        return ResponseEntity.ok(walletService.getUserWallets(userId, limit, offset));
    }
}
