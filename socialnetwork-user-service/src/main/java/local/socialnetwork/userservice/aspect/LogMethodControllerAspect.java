package local.socialnetwork.userservice.aspect;

import local.socialnetwork.userservice.aspect.annotation.LogMethodController;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class LogMethodControllerAspect {

    @Around("@annotation(local.socialnetwork.userservice.aspect.annotation.LogMethodController)")
    public Object loggingController(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method method = methodSignature.getMethod();

        if (method.isAnnotationPresent(LogMethodController.class)) {

        }

        return joinPoint.proceed();

    }
}
