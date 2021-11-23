package local.socialnetwork.userservice.aspect;

import local.socialnetwork.userservice.aspect.annotation.IdentifyNewUser;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Slf4j
@Aspect
@Component
public class IdentifyNewUserAspect {

    @Around("@annotation(local.socialnetwork.userservice.aspect.annotation.IdentifyNewUser)")
    public Object identify(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method method = methodSignature.getMethod();

        Parameter[] parameters = method.getParameters();

        Parameter parameter = parameters[0];

        if (parameter.isAnnotationPresent(IdentifyNewUser.class)) {
            log.info(String.format("Starting identify user: {} - Time: {}", System.currentTimeMillis()));

        }

        return joinPoint.proceed();

    }
}
