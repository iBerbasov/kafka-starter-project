package ru.practicum.yandex.kafka.example.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.practicum.yandex.kafka.starter.avro.VeryImportantBusinessData;

import java.time.Duration;
import java.util.Collections;

@SpringBootApplication
@Slf4j
public class ConsumerApp implements CommandLineRunner {

    private final Consumer<String, VeryImportantBusinessData> consumer;

    @Autowired
    public ConsumerApp(Consumer<String, VeryImportantBusinessData> consumer) {
        this.consumer = consumer;
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApp.class, args);
    }

    @Override
    public void run(String... args) {
        consumer.subscribe(Collections.singletonList("example-important-business-data-topic"));
        while (true) {
            ConsumerRecords<String, VeryImportantBusinessData> records = consumer.poll(Duration.ofSeconds(5));
            records.forEach(record -> {
                VeryImportantBusinessData data = record.value();
                log.info(
                        "Received data: id={}, author={}, important content={}, not imp content={}",
                        data.getId(),
                        data.getAuthor(),
                        data.getImportantContent(),
                        data.getNotWorthMyTimeContent()
                );
            });
        }
    }
}
