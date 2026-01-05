package com.emmanuel.creditrisk.backend.security;

import com.emmanuel.creditrisk.backend.entity.User;
import com.emmanuel.creditrisk.backend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long EXPIRATION_MS = 86400000; // 24 hours

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Inject UserRepository to fetch user details
    private final UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(String email) {
        // Fetch user to get their registered name
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        return Jwts.builder()
                .setSubject(email)                    // Principal (used by Spring Security)
                .claim("name", user.getName())        // ← Custom claim: user's full name
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public String validateAndExtractEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Optional helper: extract name from token (useful elsewhere if needed)
    public String extractName(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("name", String.class);
    }
}
