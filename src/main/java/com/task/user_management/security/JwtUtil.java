package com.task.user_management.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class JwtUtil {
    private final Key key;
    private final long expiration;
    public JwtUtil(@Value("${application.jwt.secret}") String secret,
                   @Value("${application.jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }
    public String generateToken(String email, Set<String> roles) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String getEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
    public Set<String> getRoles(String token) {
        Object rolesObj = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("roles");

        Set<String> roles = new HashSet<>();
        if (rolesObj instanceof List<?> list) {
            for (Object r : list) {
                roles.add(r.toString());
            }
        }
        return roles;
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
