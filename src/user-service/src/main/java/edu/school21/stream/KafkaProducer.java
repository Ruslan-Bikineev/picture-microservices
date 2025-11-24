package edu.school21.stream;

import edu.school21.config.properties.KafkaProducerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaProducer {

    private final StreamBridge streamBridge;
    private final KafkaProducerProperties kafkaProducerProperties;

    public void produce(String topic, Object logMessage) {
        try {
            Message<?> message = MessageBuilder
                    .withPayload(logMessage)
                    .build();
            push(kafkaProducerProperties.topics().get(topic), message);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private void push(final String topic, final Message<?> message) {
      log.info("KafkaProducer.push() topic: {} message {}", topic, message);
      if (!streamBridge.send(topic, message)) {
          log.info("KafkaProducer.push() message to topic: {} sent failed", topic);
      }
    }
}
