package com.ll.hereispaw.domain.payment.point.kafka.config;
import java.util.HashMap;
import java.util.Map;

import com.ll.hereispaw.domain.payment.point.kafka.dto.PointDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka // Kafka 리스너 기능 활성화
@Configuration
public class PointKafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers; // 카프카 브로커 주소

    @Bean(name = "pointConsumerFactory")
    public ConsumerFactory<String, PointDto> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // JsonDeserializer 설정
        JsonDeserializer<PointDto> jsonDeserializer = new JsonDeserializer<>(
                PointDto.class);
        jsonDeserializer.addTrustedPackages("com.ll.hereispaw.domain.point.kafka");

        // TypeMapping 이슈를 피하기 위한 설정
        jsonDeserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean(name = "pointKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PointDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PointDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
