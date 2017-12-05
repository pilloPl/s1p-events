package com.s1p.eventsourcing;

import com.s1p.eventsourcing.events.DomainEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaConfiguration {

    public static final String S1P_SNAPSHOTS_FOR_CARDS = "s1p-snapshots-for-cards";

    @Bean
    KafkaTemplate<String, DomainEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    ProducerFactory<String,DomainEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(config());
    }

    private Map<String, Object> config() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return config;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    StreamsConfig streamsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "s1p-streams");
        return new StreamsConfig(config);
    }

    @Bean
    KTable<String, CreditCard> kTable(KStreamBuilder builder) {
        Serde<DomainEvent> domainEventSerde = new JsonSerde<>(DomainEvent.class);
        Serde<CreditCard> creditCardSerde = new JsonSerde<>(CreditCard.class);

        return
                builder
                .stream(Serdes.String(), domainEventSerde, Repository.S1P_CREDIT_CARDS_EVENTS)
                .groupBy((s, domainEvent) -> domainEvent.aggregateUUID().toString(),
                        Serdes.String(), domainEventSerde)
                .aggregate(CreditCard::new,
                        (s, domainEvent, creditCard) -> creditCard.handle(domainEvent),
                        creditCardSerde,
                        S1P_SNAPSHOTS_FOR_CARDS);

    }

}
