package local.socialnetwork.authserver.service;

public interface TokenService {
    void setToken(String token);
    String getToken();
}