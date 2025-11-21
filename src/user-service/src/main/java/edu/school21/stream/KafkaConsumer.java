package edu.school21.stream;

import edu.school21.dto.kafka.UserLogMessageDto;
import edu.school21.entity.UserLogMessage;
import edu.school21.service.UserLogMessageService;
import edu.school21.utils.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final MapperUtil mapperUtil;
    private final UserLogMessageService userLogMessageService;

    @Bean
    public Consumer<List<UserLogMessageDto>> userServiceLog() {
        return messages -> {
            log.info("KafkaConsumer.userServiceLog() messages size: {}", messages.size());
            List<UserLogMessage> userLogMessages = messages.stream()
                    .map(mapperUtil::mapToUserLogMessage)
                    .toList();
            userLogMessageService.saveAll(userLogMessages);
        };
    }
}
