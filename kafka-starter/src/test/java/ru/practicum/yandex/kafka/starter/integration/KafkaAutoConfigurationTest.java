package ru.practicum.yandex.kafka.starter.integration;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import ru.practicum.yandex.kafka.starter.avro.VeryImportantBusinessData;
import ru.practicum.yandex.kafka.starter.integration.config.KafkaIntegrationBaseConfiguration;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
class KafkaAutoConfigurationTest extends KafkaIntegrationBaseConfiguration {

    @Test
    void testAvroMessageFlow() {
        try {
            //arrange
            consumer.subscribe(Collections.singletonList(TEST_TOPIC));
            await().atMost(15, TimeUnit.SECONDS)
                    .pollInterval(500, TimeUnit.MILLISECONDS)
                    .until(() -> {
                        consumer.poll(Duration.ofMillis(100)); // Активируем присоединение к группе
                        return !consumer.assignment().isEmpty();
                    });
            var expected = VeryImportantBusinessData.newBuilder()
                    .setId(1)
                    .setAuthor("test-user")
                    .setImportantContent("test-content")
                    .setNotWorthMyTimeContent("")
                    .build();

            //act
            producer.send(new ProducerRecord<>(TEST_TOPIC, expected));
            producer.flush();
            var records = consumer.poll(Duration.ofSeconds(5));
            var actual = records.iterator().next().value();

            //assert
            assertNotNull(records);
            assertFalse(records.isEmpty());
            assertNotNull(actual);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getAuthor(), actual.getAuthor());
            assertEquals(expected.getImportantContent(), actual.getImportantContent());
            assertEquals(expected.getNotWorthMyTimeContent(), actual.getNotWorthMyTimeContent());
        } finally {
            consumer.close();
            producer.close();
        }
    }
}