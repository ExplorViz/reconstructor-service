package net.explorviz.reconstructor.stream;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.util.Map;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.streams.StreamsConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class KafkaHelper {

  // Broker host
  private final String bootstrapServer;

  // Application ID
  private final String applicationId;

  // Topic to read traces from
  private final String topicTraces;

  // Topic to write/read records from/to
  private final String topicRecords;



  private final SchemaRegistryClient registry;
  private final String schemaRegistryUrl;

  @Inject
  public KafkaHelper(
      @ConfigProperty(name = "quarkus.kafka-streams.bootstrap-servers") String bootstrapServer,
      @ConfigProperty(name = "quarkus.kafka-streams.application-id") String applicationId,
      @ConfigProperty(name = "explorviz.kafka-streams.topics.traces") String topicTraces,
      @ConfigProperty(name = "explorviz.kafka-streams.topics.records") String topicRecords,
      @ConfigProperty(name = "explorviz.schema-registry.url") String schemaRegistryUrl,
      SchemaRegistryClient schemaRegistryClient) {
    this.bootstrapServer = bootstrapServer;
    this.applicationId = applicationId;
    this.topicTraces = topicTraces;
    this.topicRecords = topicRecords;
    this.schemaRegistryUrl = schemaRegistryUrl;
    this.registry = schemaRegistryClient;

  }

  public String getBootstrapServer() {
    return bootstrapServer;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public String getTopicTraces() {
    return topicTraces;
  }

  public String getTopicRecords() {
    return topicRecords;
  }

  /**
   * Returns a new instance of the default configuration for all streams to be used.
   */
  public Properties newDefaultStreamProperties() {
    final Properties streamsConfig = new Properties();
    streamsConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServer());
    streamsConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, getApplicationId());
    return streamsConfig;
  }

  /**
   * Returns a SerDe for specific avro records to be used with a schema registry.
   *
   * @param <T> the type of the avro records
   * @return the SerDe
   */
  public <T extends SpecificRecord> SpecificAvroSerde<T> getAvroValueSerde() {
    final SpecificAvroSerde<T> valueSerde = new SpecificAvroSerde<>(registry);
    valueSerde.configure(
        Map.of(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl),
        false);
    return valueSerde;
  }

}