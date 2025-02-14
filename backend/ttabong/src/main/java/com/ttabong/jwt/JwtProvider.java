package com.ttabong.jwt;

import com.ttabong.config.JwtProperties;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {


    private final SecretKey secretKey;
    private final long expiration;

    public JwtProvider(JwtProperties jwtProperties) {
        if (jwtProperties.getSecret() == null || jwtProperties.getSecret().isEmpty()) {
            throw new IllegalArgumentException("not defined a secret key");
        }

        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = jwtProperties.getExpiration();
    }

    public String createToken(String userId, String userType) {
        Claims claims = Jwts.claims();
        claims.setSubject(userId);
        claims.put("userType", userType); // 유저 타입 추가

        Date now = new Date();
        Date expiration = new Date(now.getTime() + this.expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("만료된 토큰입니다.", HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException | SecurityException e) {
            throw new JwtAuthenticationException("위조된 토큰입니다.", HttpStatus.FORBIDDEN);
        } catch (UnsupportedJwtException e) {
            throw new JwtAuthenticationException("지원되지 않는 토큰입니다.", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            throw new JwtAuthenticationException("잘못된 토큰입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    public AuthDto toAuthDto(String token) {
        Claims claims = getClaims(token);
        return new AuthDto(claims.get("userId", Integer.class), claims.get("userType", String.class));
    }

    public boolean isVolunteer(String token) {
        Claims claims = getClaims(token);
        String userType = claims.get("userType", String.class);
        return "volunteer".equals(userType);
    }
}
