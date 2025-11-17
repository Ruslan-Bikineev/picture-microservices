package edu.school21.service;

import edu.school21.entity.UserLogMessage;
import edu.school21.repository.UserLogMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserLogMessageService {

    private final UserLogMessageRepository userLogMessageRepository;

    public void save(UserLogMessage userLogMessage) {
        userLogMessageRepository.save(userLogMessage);
    }
}
