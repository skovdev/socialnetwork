package local.socialnetwork.userservice.aspect;

import local.socialnetwork.userservice.aspect.annotation.IdentifyNewUser;

import local.socialnetwork.userservice.exception.UserAlreadyExistsException;

import local.socialnetwork.userservice.model.dto.RegistrationDto;

import local.socialnetwork.userservice.service.UserService;
import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;

import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdentifyNewUserAspect {

    UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Before("@annotation(local.socialnetwork.userservice.aspect.annotation.IdentifyNewUser)")
    public void identify(JoinPoint joinPoint) {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        RegistrationDto registrationDto = (RegistrationDto) joinPoint.getArgs()[0];

        if (methodSignature.getMethod().isAnnotationPresent(IdentifyNewUser.class)) {

            log.info(String.format("Starting identify user - Time: %d", System.currentTimeMillis()));

            if (registrationDto != null && registrationDto.getUsername() != null) {

                userService.findByUsername(registrationDto.getUsername())
                        .orElseThrow(() -> new UserAlreadyExistsException("User with such of username already exist in database"));
            }
        }
    }
}
