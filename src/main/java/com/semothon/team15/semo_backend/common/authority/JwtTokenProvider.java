package com.semothon.team15.semo_backend.common.authority;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String originKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private Key secretKey;

    @PostConstruct
    public void init() {
        if (originKey == null || originKey.isEmpty()) {
            throw new IllegalStateException("JWT Secret Key is not set");
        }
        this.secretKey = Keys.hmacShaKeyFor(originKey.getBytes());
    }

    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiration)
                .signWith((SecretKey) secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = validateToken(token);
        //List<String> roles = claims.get("roles", List.class);
        List<?> rawRoles = claims.get("roles", List.class);
        List<String> roles = rawRoles.stream()
                .filter(obj -> obj instanceof String)
                .map(obj -> (String) obj)
                .toList();

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
    }
}
