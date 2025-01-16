package org.example.expert.domain.todo.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoFilterResponse {

    private Long id;
    private String title;
    private Long managerCount;
    private Long commentCount;
    private LocalDateTime createdAt;

    public TodoFilterResponse(Long id, String title, Long managerCount, Long commentCount,
        LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.managerCount = managerCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
    }
}
