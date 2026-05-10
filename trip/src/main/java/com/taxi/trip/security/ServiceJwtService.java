package com.taxi.trip.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class ServiceJwtService {
    @Value("${jwt.secret}")
    private String serviceSecret;

    @Value("${jwt.expiration:600000}")
    private Long expiration;

    private String currentToken = "";
    private long tokenTimeStamp;

    private void generateServiceToken() {
        tokenTimeStamp = System.currentTimeMillis();
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(serviceSecret));

        currentToken = Jwts.builder()
                .subject("trip-service")
                .claim("role", "SERVICE")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

    }
    public String getCurrentToken() {
        if (currentToken.isEmpty() || tokenTimeStamp + expiration*0.75 <= System.currentTimeMillis()) {
            generateServiceToken();
        }
        return currentToken;
    }
}
