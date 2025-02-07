package com.ttabong.jwt;

import com.ttabong.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("userType", userType);

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


    /**
     * JWT를 복호화(파싱)하여 Claims(토큰에 담긴 정보)를 꺼내는 메서드
     * @param token 클라이언트가 보낸 토큰 문자열
     * @return 토큰 안에 들어있는 Claims
     */
    public Claims getClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


            System.out.println("seccess decoded Token Claims ");
            return claims;
        } catch (Exception e) {

            System.out.println("JWT 파싱 오류: " + e.getMessage());
            throw e;
        }
    }
}
