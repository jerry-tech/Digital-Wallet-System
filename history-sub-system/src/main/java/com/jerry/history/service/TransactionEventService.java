package com.jerry.history.service;

import com.jerry.history.pojo.TransactionHistoryPojo;
import com.jerry.history.pojo.WalletEventPojo;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface TransactionEventService {

    void logWalletTransactionEventHistory(WalletEventPojo walletEventPojo, String payload);

    List<TransactionHistoryPojo> findTransactionEventHistoryByWalletId(String walletId, Long limit, Long pageNumber, Sort.Direction sortOrderConstant);

    List<TransactionHistoryPojo> findTransactionEventHistoryByUserId(String userId, Long limit, Long pageNumber, Sort.Direction sortOrderConstant);
}
