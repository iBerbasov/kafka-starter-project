package ru.practicum.yandex.kafka.starter.integration.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.practicum.yandex.kafka.starter.KafkaAutoConfiguration;
import ru.practicum.yandex.kafka.starter.avro.VeryImportantBusinessData;


@SpringBootTest(classes = {KafkaAutoConfiguration.class})
@Testcontainers
public class KafkaIntegrationBaseConfiguration {

    protected static final String TEST_TOPIC = "test-topic";
    private static final Network NETWORK = Network.newNetwork();
    private static final int SCHEMA_REGISTRY_PORT = 8081;

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))
            .withNetwork(NETWORK)
            .withNetworkAliases("kafka-broker")
            .withReuse(false)
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true");

    @Container
    static final GenericContainer<?> SCHEMA_REGISTRY = new GenericContainer<>(
            DockerImageName.parse("confluentinc/cp-schema-registry:7.5.0"))
            .withNetwork(NETWORK)
            .withExposedPorts(SCHEMA_REGISTRY_PORT)
            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
            .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS",
                    "PLAINTEXT://kafka-broker:9092")
            .withReuse(false)
            .dependsOn(KAFKA);

    @Autowired
    protected Producer<String, VeryImportantBusinessData> producer;

    @Autowired
    protected Consumer<String, VeryImportantBusinessData> consumer;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        final String schemaRegistryUrl = String.format(
                "http://%s:%d",
                SCHEMA_REGISTRY.getHost(),
                SCHEMA_REGISTRY.getMappedPort(SCHEMA_REGISTRY_PORT)
        );

        registry.add("app.kafka.producer.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("app.kafka.consumer.bootstrap-servers", KAFKA::getBootstrapServers);

        registry.add("app.kafka.producer.key-serializer",
                () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("app.kafka.producer.value-serializer",
                () -> "io.confluent.kafka.serializers.KafkaAvroSerializer");
        registry.add("app.kafka.producer.schema-registry-url",
                () -> schemaRegistryUrl);

        registry.add("app.kafka.consumer.group-id",
                () -> "test-group-" + System.currentTimeMillis());
        registry.add("app.kafka.consumer.key-deserializer",
                () -> "org.apache.kafka.common.serialization.StringDeserializer");
        registry.add("app.kafka.consumer.value-deserializer",
                () -> "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        registry.add("app.kafka.consumer.schema-registry-url", () -> schemaRegistryUrl);
        registry.add("app.kafka.consumer.specific.avro.reader", () -> "true"); // Важно!
    }
}
