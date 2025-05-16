package ru.practicum.yandex.kafka.starter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.producer")
@Getter
@Setter
public class KafkaProducerProperties {
    private String bootstrapServers;
    private String keySerializer;
    private String valueSerializer;
    private String schemaRegistryUrl;
}

