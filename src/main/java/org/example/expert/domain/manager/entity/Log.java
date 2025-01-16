package org.example.expert.domain.manager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String methodName;

    @Lob
    private String message;

    private LocalDateTime timestamp;

    public Log(Long userId, String methodName, String message, LocalDateTime timestamp) {
        this.userId = userId;
        this.methodName = methodName;
        this.message = message;
        this.timestamp = timestamp;
    }
}
