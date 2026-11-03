package com.jerry.history.controller;


import com.jerry.history.pojo.TransactionHistoryPojo;
import com.jerry.history.service.TransactionEventService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping()
public class TransactionEventController {

    private final TransactionEventService transactionEventService;

    @GetMapping("/wallets/{walletId}/history")
    public ResponseEntity<List<TransactionHistoryPojo>> getTransactionHistoryByWalletId(@PathVariable String walletId,
                                                                                        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Long limit,
                                                                                        @RequestParam(defaultValue = "0") @Min(0) Long pageNumber,
                                                                                        @RequestParam(defaultValue = "DESC") Sort.Direction sortOrder) {
        List<TransactionHistoryPojo> history = transactionEventService.findTransactionEventHistoryByWalletId(walletId, limit, pageNumber, sortOrder);
        return ResponseEntity.ok(history);
    }


    @GetMapping("/users/{userId}/activity")
    public ResponseEntity<List<TransactionHistoryPojo>> getTransactionHistoryByUserId(@PathVariable String userId,
                                                                                      @RequestParam(defaultValue = "10") @Min(1) @Max(100) Long limit,
                                                                                      @RequestParam(defaultValue = "0") @Min(0) Long pageNumber,
                                                                                      @RequestParam(defaultValue = "DESC") Sort.Direction sortOrder) {
        List<TransactionHistoryPojo> history = transactionEventService.findTransactionEventHistoryByUserId(userId, limit, pageNumber, sortOrder);
        return ResponseEntity.ok(history);
    }
}
