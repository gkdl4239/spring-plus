package org.example.expert.domain.manager.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.entity.Log;
import org.example.expert.domain.manager.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(Long userId) {
        LocalDateTime timestamp = LocalDateTime.now();
        String message = "매니저 등록 요청";
        Log log = new Log(userId, message, timestamp);
        logRepository.save(log);
    }
}
