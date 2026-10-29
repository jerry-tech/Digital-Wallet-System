package com.jerry.wallet.controller;

import com.jerry.wallet.dto.CreateWalletDto;
import com.jerry.wallet.dto.FundWalletDto;
import com.jerry.wallet.dto.WalletTransferDto;
import com.jerry.wallet.pojo.WalletPojo;
import com.jerry.wallet.pojo.TransferResultPojo;
import com.jerry.wallet.pojo.WalletTransactionPojo;
import com.jerry.wallet.service.WalletService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/wallets")
    public ResponseEntity<?> createWalletForUser(@RequestBody @Valid CreateWalletDto createWalletDto) {
        WalletPojo walletPojo = walletService.createWalletForUser(createWalletDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(walletPojo);
    }

    @PostMapping("/wallets/{walletId}/fund")
    public ResponseEntity<?> fundWallet(@PathVariable String walletId, @RequestBody @Valid FundWalletDto fundWalletDto) {
        WalletTransactionPojo walletTransactionPojo = walletService.fundWallet(walletId, fundWalletDto);
        return ResponseEntity.status(HttpStatus.OK).body(walletTransactionPojo);
    }

    @PostMapping("/wallets/{walletId}/transfer")
    public ResponseEntity<?> transferFunds(@PathVariable String walletId, @RequestBody @Valid WalletTransferDto walletTransferDto) {
        TransferResultPojo transferResultPojo = walletService.transferFunds(walletId, walletTransferDto);
        return ResponseEntity.status(HttpStatus.OK).body(transferResultPojo);
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String walletId) {
        return ResponseEntity.ok(walletService.getBalance(walletId));
    }

    @GetMapping("/users/{userId}/wallets")
    public ResponseEntity<List<WalletPojo>> getUserWallets(@PathVariable String userId,
                                                           @RequestParam(defaultValue = "10") @Min(1) @Max(100) Long limit,
                                                           @RequestParam(defaultValue = "0") @Min(0) Long pageNumber,
                                                           @RequestParam(defaultValue = "DESC") Sort.Direction sortOrder) {
        return ResponseEntity.ok(walletService.getUserWallets(userId, limit, pageNumber, sortOrder));
    }
}
