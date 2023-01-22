package local.socialnetwork.apigateway.annotation.processing;

import local.socialnetwork.apigateway.annotation.LoggingFilter;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.annotation.Before;

import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class LoggingFilterAspectProcessing {

    @Before("@annotation(local.socialnetwork.apigateway.annotation.LoggingFilter)")
    public void loggingFilterBefore(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String methodName = method.getName();
        LocalDateTime logDateTime = LocalDateTime.now();

        if (method.isAnnotationPresent(LoggingFilter.class)) {
            log.info(String.format("Start logging of filter: %s - Date and time: %s", methodName, logDateTime));
        }
    }

    @After("@annotation(local.socialnetwork.apigateway.annotation.LoggingFilter)")
    public void loggingFilterAfter(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String methodName = method.getName();
        LocalDateTime logDateTime = LocalDateTime.now();
        if (method.isAnnotationPresent(LoggingFilter.class)) {
            log.info(String.format("End logging of filter: %s - Date and time: %s", methodName, logDateTime));
        }
    }
}
