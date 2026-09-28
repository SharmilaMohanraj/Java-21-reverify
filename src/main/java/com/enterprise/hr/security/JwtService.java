package com.enterprise.hr.security;

import com.enterprise.hr.domain.Employee;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
 private final Key key; private final long expiration;
 public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms:3600000}") long expiration) { if(secret.length()<32) throw new IllegalArgumentException("JWT secret must be at least 32 characters"); key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.expiration=expiration; }
 public String issue(Employee e) { Instant now=Instant.now(); return Jwts.builder().subject(e.getEmail()).claim("role", "ROLE_"+e.getRole().name()).issuedAt(Date.from(now)).expiration(Date.from(now.plusMillis(expiration))).signWith(key).compact(); }
 public Claims claims(String token) { return Jwts.parser().verifyWith((javax.crypto.SecretKey)key).build().parseSignedClaims(token).getPayload(); }
 public String subject(String token) { return claims(token).getSubject(); }
 public String role(String token) { return claims(token).get("role", String.class); }
}
