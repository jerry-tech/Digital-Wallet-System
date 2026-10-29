package com.jerry.wallet.serviceImpl;

import com.jerry.wallet.dto.WalletTransactionDto;
import com.jerry.wallet.model.WalletTransactions;
import com.jerry.wallet.repository.WalletTransactionsRepository;
import com.jerry.wallet.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionsRepository walletTransactionsRepository;


    @Override
    public WalletTransactions createWalletTransaction(WalletTransactionDto walletTransactionDto) {
        WalletTransactions walletTransactions = new WalletTransactions();
        walletTransactions.setWallet(walletTransactionDto.wallet());
        walletTransactions.setAmount(walletTransactionDto.amount());
        walletTransactions.setType(walletTransactionDto.transactionType());
        walletTransactions.setStatus(walletTransactionDto.transactionStatus());
        walletTransactions.setTransferId(walletTransactionDto.transferId());
        walletTransactions.setBalanceAfter(walletTransactionDto.balanceAfter());

        return walletTransactionsRepository.save(walletTransactions);
    }
}
