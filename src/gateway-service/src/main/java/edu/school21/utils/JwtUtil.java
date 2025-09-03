package edu.school21.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JwtUtil {

    public String extractUserId(String bearerToken) {
        String token = extractToken(bearerToken);
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    }

    public String extractToken(String header) {
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith("Bearer "))
                .map(h -> h.substring(7))
                .orElse(null);
    }
}
