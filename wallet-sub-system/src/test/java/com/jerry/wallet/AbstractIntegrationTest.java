package com.jerry.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jerry.wallet.dto.CreateWalletDto;
import com.jerry.wallet.service.WalletService;
import com.jerry.wallet.service.WalletTransactionService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {


    @Autowired protected MockMvc mockMvc;
    @Autowired protected JdbcTemplate jdbc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected KafkaTemplate<String, String> kafkaTemplate;
    @Autowired protected WalletTransactionService transactionEventService;
    @Autowired protected Gson gson;

    @Value("${kafka.topics.wallet-creation-topic}") protected String creationTopic;
    @Value("${kafka.topics.wallet-funding-topic}") protected String fundingTopic;
    @Value("${kafka.topics.wallet-transfer-topic}") protected String transferTopic;


    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @ServiceConnection
    static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.8.0");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        kafka.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }


    protected String eventJson(String eventId, String type, String walletId, String userId,
                               String amount, String transactionId, Instant timestamp) {
        return """
               {"eventId":"%s","eventType":"%s","walletId":"%s","userId":"%s","counterpartyWalletId":null,
                "amount":"%s","transactionId":%s,"transferId":null,"transactionType":null,
                "timestamp":"%s","schemaVersion":1}
               """.formatted(eventId, type, walletId, userId, amount,
                transactionId == null ? "null" : "\"" + transactionId + "\"", timestamp);
    }

    protected void publish(String topic, String walletId, String json) throws Exception {
        kafkaTemplate.send(topic, walletId, json).get(10, TimeUnit.SECONDS);
    }

    protected int count(String sql, Object... args) {
        return jdbc.queryForObject(sql, Integer.class, args);
    }

    protected void awaitRow(String eventId) {
        Awaitility.await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(count("select count(*) from transaction_events where id = ?", eventId)).isEqualTo(1));
    }

    protected <T> List<Future<T>> runConcurrently(int n, Callable<T> task) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(n);
        CountDownLatch ready = new CountDownLatch(n);
        CountDownLatch go = new CountDownLatch(1);
        List<Future<T>> futures = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                go.await();
                return task.call();
            }));
        }
        ready.await();
        go.countDown();
        pool.shutdown();
        assertThat(pool.awaitTermination(60, TimeUnit.SECONDS)).as("no deadlock / hang").isTrue();
        return futures;
    }
}