package com.jerry.history.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jerry.history.model.TransactionEvents;
import com.jerry.history.pojo.TransactionHistoryPojo;
import com.jerry.history.pojo.WalletEventPojo;
import com.jerry.history.repository.TransactionEventsRepository;
import com.jerry.history.service.GsonUtilService;
import com.jerry.history.service.TransactionEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventServiceImpl implements TransactionEventService {
    private final TransactionEventsRepository transactionEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void logWalletTransactionEventHistory(WalletEventPojo walletEventPojo, String payload) {

        // 1. fast path: we already stored this event (Kafka redelivery, outbox resend, consumer restart)
        if (transactionEventRepository.findTransactionEventsByTransactionId(walletEventPojo.eventId().trim()).isPresent()) {
            log.info("Duplicate event ignored eventId={}", walletEventPojo.eventId());
            return;
        }

        TransactionEvents transactionEvent = new TransactionEvents();
        transactionEvent.setId(walletEventPojo.eventId());
        transactionEvent.setTransactionId(walletEventPojo.eventId());
        transactionEvent.setEventType(walletEventPojo.eventType().name());
        transactionEvent.setEventData(payload);
        transactionEvent.setAmount(new BigDecimal(walletEventPojo.amount()));
        transactionEvent.setWalletId(walletEventPojo.walletId());
        transactionEvent.setUserId(walletEventPojo.userId());
        if (walletEventPojo.timestamp() != null) {
            transactionEvent.setCreatedAt(LocalDateTime.ofInstant(walletEventPojo.timestamp(), ZoneOffset.UTC));
        }
        try {
            transactionEventRepository.save(transactionEvent);
        } catch (DataIntegrityViolationException duplicateKey) {
            log.info("Duplicate event ignored (concurrent insert) eventId={}", walletEventPojo.eventId());
        }

    }

    @Override
    public List<TransactionHistoryPojo> findTransactionEventHistoryByWalletId(String walletId, Long limit, Long pageNumber, Sort.Direction sortOrderConstant) {
       return transactionEventRepository.findTransactionEventsByWalletId(walletId,
                PageRequest.of(pageNumber.intValue(), limit.intValue(), Sort.by(sortOrderConstant, "createdAt")))
                .stream().map(this::toPojo)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionHistoryPojo> findTransactionEventHistoryByUserId(String userId, Long limit, Long pageNumber, Sort.Direction sortOrderConstant) {
        return transactionEventRepository.findTransactionEventsByUserId(userId,
                        PageRequest.of(pageNumber.intValue(), limit.intValue(), Sort.by(sortOrderConstant, "createdAt")))
                .stream().map(this::toPojo)
                .collect(Collectors.toList());
    }

    private TransactionHistoryPojo toPojo(TransactionEvents row) {
        try {
            return TransactionHistoryPojo.of(row, objectMapper.readTree(row.getEventData()));
        } catch (JsonProcessingException e) {
            return TransactionHistoryPojo.of(row, null);
        }
    }
}
