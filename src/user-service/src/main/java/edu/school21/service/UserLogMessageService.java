package edu.school21.service;

import edu.school21.entity.UserLogMessage;
import edu.school21.repository.UserLogMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserLogMessageService {

    private final UserLogMessageRepository userLogMessageRepository;

    public void saveAll(List<UserLogMessage> userLogMessages) {
        userLogMessageRepository.saveAll(userLogMessages);
    }
}
