package local.socialnetwork.profileservice.interceptor;

import feign.RequestTemplate;
import feign.RequestInterceptor;

import local.socialnetwork.profileservice.service.TokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenFeignRequestInterceptor implements RequestInterceptor {

    final TokenService tokenService;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = tokenService.getToken();
        if (token != null && !token.isEmpty()) {
            requestTemplate.header("Authorization", "Bearer " + token);
        } else {
            throw new IllegalStateException("Token cannot be null or empty");
        }
    }
}
