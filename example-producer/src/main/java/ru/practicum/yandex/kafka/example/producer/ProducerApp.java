package ru.practicum.yandex.kafka.example.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.practicum.yandex.kafka.starter.avro.VeryImportantBusinessData;

import java.util.concurrent.TimeUnit;


@SpringBootApplication
@Slf4j
public class ProducerApp implements CommandLineRunner {

    private final Producer<String, VeryImportantBusinessData> producer;

    @Autowired
    public ProducerApp(Producer<String, VeryImportantBusinessData> producer) {
        this.producer = producer;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProducerApp.class, args);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        int idGenerator = 0;
        while (true) {
            VeryImportantBusinessData data = VeryImportantBusinessData.newBuilder()
                    .setId(idGenerator++)
                    .setAuthor("mock-author")
                    .setImportantContent("content #%d here".formatted(idGenerator))
                    .setNotWorthMyTimeContent("meh")
                    .build();
            producer.send(new ProducerRecord<>("example-important-business-data-topic", data));
            producer.flush();
            log.info("Sent avro message #{}", idGenerator);
            TimeUnit.SECONDS.sleep(3);
        }
    }
}
