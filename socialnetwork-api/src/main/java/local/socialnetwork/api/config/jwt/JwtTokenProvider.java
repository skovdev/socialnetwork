package local.socialnetwork.api.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

import local.socialnetwork.api.exception.InvalidJwtAuthenticationException;

import local.socialnetwork.model.constants.UserConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

    private UserDetailsService userDetailsService;

    @Qualifier("userDetailsService")
    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(UUID id, String username, List<String> roles) {

        String uuid = String.valueOf(id);

        Claims claims = Jwts.claims().setSubject(uuid);

        claims.put("username", username);
        claims.put("isAdmin", isAdmin(roles));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(validity)
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();

    }

    private boolean isAdmin(List<String> roles) {
        return roles.stream().anyMatch(role -> role.equalsIgnoreCase(UserConstants.ROLE_ADMIN));
    }

    Authentication getAuthentication(String token) {

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

    }

    private String getUsername(String token) {

        for (Map.Entry<String, Object> entry : Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().entrySet()) {

            if (entry.getKey().equals("username")) {
                return String.valueOf(entry.getValue());
            }
        }

        return null;

    }

    String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;

    }

    boolean validateToken(String token) {

        try {

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            if (claimsJws.getBody().getExpiration().before(new Date())) {
                return false;
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }

        return true;

    }
}