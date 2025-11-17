package edu.school21.service;

import edu.school21.dto.kafka.UserLogMessageDto;
import edu.school21.dto.response.UserRsDto;
import edu.school21.stream.KafkaProducer;
import edu.school21.utils.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserKafkaLogger {

    private final MapperUtil mapperUtil;
    private final KafkaProducer kafkaProducer;
    private static final String USER_SERVICE_LOG_TOPIC = "user-service-log";
    private static final String USER_SUCCESS_REGISTERED_MESSAGE = "Пользователь успешно зарегистрирован";
    private static final String USER_SUCCESS_AUTHORIZATION_MESSAGE = "Пользователь успешно авторизован";

    public void userRegisterLog(UserRsDto userRsDto) {
        UserLogMessageDto userLogMessageDto = mapperUtil.mapToUserLogMessageDto(userRsDto, USER_SUCCESS_REGISTERED_MESSAGE);
        kafkaProducer.produce(USER_SERVICE_LOG_TOPIC, userLogMessageDto);
    }

    public void userAuthorizationLog(Long userId, String username) {
        UserLogMessageDto userLogMessageDto = mapperUtil.mapToUserLogMessageDto(userId, username, USER_SUCCESS_AUTHORIZATION_MESSAGE);
        kafkaProducer.produce(USER_SERVICE_LOG_TOPIC, userLogMessageDto);
    }
}
