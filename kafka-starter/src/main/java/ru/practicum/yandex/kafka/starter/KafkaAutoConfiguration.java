package ru.practicum.yandex.kafka.starter;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.yandex.kafka.starter.avro.VeryImportantBusinessData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Configuration
@EnableConfigurationProperties({KafkaProducerProperties.class, KafkaConsumerProperties.class})
public class KafkaAutoConfiguration {

    private static String SCHEMA_REGISTRY_URL_CONFIG = "schema.registry.url";

    private Producer<?, ?> producer;
    private Consumer<?, ?> consumer;

    @Bean
    @ConditionalOnMissingBean
    public Producer<String, VeryImportantBusinessData> kafkaProducer(KafkaProducerProperties props) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, props.getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, props.getValueSerializer());
        config.put(SCHEMA_REGISTRY_URL_CONFIG, props.getSchemaRegistryUrl());

        this.producer = new KafkaProducer<>(config);
        return (Producer<String, VeryImportantBusinessData>) producer;
    }

    @Bean
    @ConditionalOnMissingBean
    public Consumer<String, VeryImportantBusinessData> kafkaConsumer(KafkaConsumerProperties props) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, props.getGroupId());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, props.getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, props.getValueDeserializer());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, props.isAutoCommit());
        config.put(SCHEMA_REGISTRY_URL_CONFIG, props.getSchemaRegistryUrl());
        config.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        this.consumer = new KafkaConsumer<>(config);
        return (Consumer<String, VeryImportantBusinessData>) consumer;
    }

    @PreDestroy
    public void destroy() {
        Optional.ofNullable(this.producer).ifPresent(Producer::close);
        Optional.ofNullable(this.consumer).ifPresent(Consumer::close);
    }
}
