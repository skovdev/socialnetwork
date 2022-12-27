package local.socialnetwork.authservice.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;
import local.socialnetwork.authservice.exception.InvalidJwtAuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import javax.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProvider {

//    @Value("${security.jwt.token.secret-key}")
//    private String secretKey;

    private Key key;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

    private UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
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