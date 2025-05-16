package ru.practicum.yandex.kafka.starter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.consumer")
@Getter
@Setter
public class KafkaConsumerProperties {
    private String bootstrapServers;
    private String groupId;
    private String keyDeserializer;
    private String valueDeserializer;
    private boolean autoCommit = true;
    private String schemaRegistryUrl;
}
