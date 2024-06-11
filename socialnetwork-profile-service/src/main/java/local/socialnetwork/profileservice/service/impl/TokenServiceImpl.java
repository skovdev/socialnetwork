package local.socialnetwork.profileservice.service.impl;

import local.socialnetwork.profileservice.service.TokenService;

import org.springframework.stereotype.Component;

@Component
public class TokenServiceImpl implements TokenService {

    private String token;

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }
}