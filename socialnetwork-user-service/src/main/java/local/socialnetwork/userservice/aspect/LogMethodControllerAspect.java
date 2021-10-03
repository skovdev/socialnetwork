package local.socialnetwork.userservice.aspect;

import local.socialnetwork.userservice.aspect.annotation.LogMethodController;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class LogMethodControllerAspect {

    @Around("@annotation(local.socialnetwork.userservice.aspect.annotation.LogMethodController)")
    public Object loggingController(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method method = methodSignature.getMethod();

        String methodName = joinPoint.getSignature().getName();
        LocalDateTime logDateTime = LocalDateTime.now();

        if (method.isAnnotationPresent(LogMethodController.class)) {
            log.info(String.format("Start logging method of controller: %s - Date and time: %s", methodName, logDateTime));
        }

        return joinPoint.proceed();

    }
}
