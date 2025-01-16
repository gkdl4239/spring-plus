package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.service.LogService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class ManagerRegistrationLoggingAspect {

    private final HttpServletRequest request;
    private final LogService logService;

    @Before("execution(* org.example.expert.domain.manager.controller.ManagerController.saveManager(..))")
    public void logBeforeRequest(JoinPoint joinPoint) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuthUser authUser = (AuthUser) principal;
        Long userId = authUser.getId();
        String methodName = joinPoint.getSignature().toShortString();
        String message = "매니저 등록 요청";
        LocalDateTime requestTime = LocalDateTime.now();

        logService.saveLog(userId, methodName, message, requestTime);
    }
}
