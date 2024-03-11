package local.socialnetwork.authserver.config.jwt;

import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;

import jakarta.annotation.PostConstruct;

import jakarta.servlet.http.HttpServletRequest;

import local.socialnetwork.authserver.exception.InvalidJwtAuthenticationException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;

import java.security.Key;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Base64;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    String secretKey;

    @Value("${security.jwt.token.expire-length}")
    long validityInMilliseconds;

    Key key;

    final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        key = new SecretKeySpec(Base64.getDecoder().decode(secretKey),
                SignatureAlgorithm.HS256.getJcaName());
    }

    public String createToken(UUID id, String username, List<String> roles) {
        String uuid = String.valueOf(id);
        Claims claims = Jwts.claims().setSubject(uuid);
        claims.put("authUserId", id);
        claims.put("username", username);
        claims.put("isAdmin", isAdmin(roles));
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(validity)
                   .signWith(key)
                   .compact();
    }

    private boolean isAdmin(List<String> roles) {
        return roles.stream().anyMatch(role -> role.equalsIgnoreCase("ADMIN"));
    }

    Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getUsername(String token) {
        for (Map.Entry<String, Object> entry : Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .entrySet()) {
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

    boolean validateToken(String token) throws InvalidJwtAuthenticationException {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            if (claimsJws.getBody().getExpiration().before(new Date())) {
                return false;
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }
        return true;
    }
}