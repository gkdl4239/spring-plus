package org.example.expert.domain.manager.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.entity.Log;
import org.example.expert.domain.manager.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional
    public void saveLog(Long userId, String methodName, String message, LocalDateTime timestamp) {
        Log log = new Log(userId, methodName, message, timestamp);
        logRepository.save(log);
    }
}
