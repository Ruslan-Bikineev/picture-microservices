package edu.school21.utils;

import edu.school21.exception.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        return parseToken(token)
                .getSubject();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractToken(String header) {
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith("Bearer "))
                .map(h -> h.substring(7))
                .orElse(null);
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            throw new InvalidJwtTokenException("Недопустимая подпись JWT: " + e.getMessage());
        } catch (MalformedJwtException e) {
            throw new InvalidJwtTokenException("Недопустимый токен JWT: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new InvalidJwtTokenException("Срок действия токена JWT истек: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new InvalidJwtTokenException("Токен JWT не поддерживается: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new InvalidJwtTokenException("Строка утверждений JWT пуста: " + e.getMessage());
        }
    }
}
