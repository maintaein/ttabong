package com.ttabong.jwt;

import com.ttabong.config.JwtProperties;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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

    public String createToken(Long userId, String userType) {
        // 토큰에 담을 정보(Claims)를 구성
        Claims claims = Jwts.claims();
        // subject: 주로 토큰의 대표값(여기서는 userId라고 보면 됨)
        claims.setSubject(userId.toString()); // ✅ sub에 userId 저장
        claims.put("userType", userType); // 토큰에 유저 타입도 같이 넣어둠

        Date now = new Date();
        Date expiration = new Date(now.getTime() + this.expiration);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        System.out.println("success created JWT access token~~  ");
        return token;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 토큰입니다.");
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("유효하지 않은 토큰입니다.");
        }
        return false;
    }


    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }


    public AuthDto toAuthDto(String token) {
        Claims claims = getClaims(token);
        return new AuthDto(claims.get("userId", Integer.class), claims.get("userType", String.class));
    }
}
