package org.example.expert.domain.todo.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoFilterResponse;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
            todoSaveRequest.getTitle(),
            todoSaveRequest.getContents(),
            weather,
            user
        );

        Manager manager = new Manager(user, newTodo);
        newTodo.addManager(manager);

        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
            savedTodo.getId(),
            savedTodo.getTitle(),
            savedTodo.getContents(),
            weather,
            new UserResponse(user.getId(), user.getEmail(), user.getNickname())
        );
    }

    public Page<TodoResponse> getTodos(
        int page,
        int size,
        String weather,
        String startDate,
        String endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);

        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;

        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(
            pageable,
            weather,
            start,
            end);

        return todos.map(todo -> new TodoResponse(
            todo.getId(),
            todo.getTitle(),
            todo.getContents(),
            todo.getWeather(),
            new UserResponse(todo.getUser().getId(), todo.getUser().getEmail(),
                todo.getUser().getNickname()),
            todo.getCreatedAt(),
            todo.getModifiedAt()
        ));
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
            .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
            todo.getId(),
            todo.getTitle(),
            todo.getContents(),
            todo.getWeather(),
            new UserResponse(user.getId(), user.getEmail(), user.getNickname()),
            todo.getCreatedAt(),
            todo.getModifiedAt()
        );
    }

    public Page<TodoFilterResponse> getTodosByFilter(
        int page,
        int size,
        String title,
        String managerName,
        String startDate,
        String endDate) {

        Pageable pageable = PageRequest.of(page - 1, size);

        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;

        return todoRepository.findAllByFilter(
            title,
            managerName,
            start,
            end,
            pageable);
    }
}
