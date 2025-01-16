package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.expert.domain.todo.dto.response.TodoFilterResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoRepositoryQuery {

    Optional<Todo> findByIdWithUser(Long todoId);

    Page<TodoFilterResponse> findAllByFilter(String title, String managerName, LocalDateTime start,
        LocalDateTime end, Pageable pageable);
}
