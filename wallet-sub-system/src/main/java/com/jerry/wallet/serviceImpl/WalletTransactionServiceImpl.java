package com.jerry.wallet.serviceImpl;

import com.google.gson.Gson;
import com.jerry.wallet.config.KafkaTopicProperties;
import com.jerry.wallet.dto.WalletTransactionDto;
import com.jerry.wallet.dto.WalletTransferEventDto;
import com.jerry.wallet.model.WalletTransactions;
import com.jerry.wallet.repository.WalletTransactionsRepository;
import com.jerry.wallet.service.WalletTransactionService;
import com.jerry.wallet.service.kafka.KafkaPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;



@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionsRepository walletTransactionsRepository;
    private final KafkaPublisherService kafkaPublisherService;
    private final KafkaTopicProperties kafkaTopicProperties;
    private final Gson gson;


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
