package edu.school21.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "kafka-producer")
public record KafkaProducerProperties(Map<String, String> topics) {
}
