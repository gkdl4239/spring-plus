package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoFilterResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class TodoRepositoryQueryImpl implements TodoRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {

        Todo result = queryFactory
            .selectFrom(todo)
            .join(todo.user, user).fetchJoin()
            .where(
                todo.id.eq(todoId)
            )
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoFilterResponse> findAllByFilter(
        String title,
        String managerName,
        LocalDateTime start,
        LocalDateTime end,
        Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (title != null && !title.isEmpty()) {
            builder.and(todo.title.contains(title));
        }
        if (managerName != null && !managerName.isEmpty()) {
            builder.and(manager.user.nickname.contains(managerName));
        }
        if (start != null) {
            builder.and(todo.createdAt.goe(start));
        }
        if (end != null) {
            builder.and(todo.createdAt.loe(end));
        }

        List<TodoFilterResponse> result = queryFactory
            .select(Projections.fields(
                TodoFilterResponse.class,
                todo.id.as("id"),
                todo.title.as("title"),
                manager.id.countDistinct().longValue().as("managerCount"),
                comment.id.countDistinct().longValue().as("commentCount"),
                todo.createdAt.as("createdAt")
            ))
            .from(todo)
            .leftJoin(manager).on(manager.todo.id.eq(todo.id))
            .leftJoin(comment).on(comment.todo.id.eq(todo.id))
            .where(builder)
            .groupBy(todo.id)
            .orderBy(todo.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(todo.id.count())
            .from(todo)
            .leftJoin(manager).on(manager.todo.id.eq(todo.id))
            .leftJoin(comment).on(comment.todo.id.eq(todo.id))
            .where(builder)
            .fetchOne();

        return new PageImpl<>(result, pageable, total);
    }

}
