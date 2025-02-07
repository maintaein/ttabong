package com.ttabong.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    // JWT를 만들 때 사용하는 '비밀키'
    private final String secretKey = "my-super-secret-key"; // 실제로는 안전하게 보관해야 함

    // JWT의 유효 시간 (예: 30분)
    private final long validityInMillis = 30 * 60 * 1000L;

    /**
     * JWT 생성 메서드
     * @param userId JWT에 포함할 유저 ID
     * @param userType JWT에 포함할 유저 타입(예: "VOLUNTEER" or "ORGANIZATION")
     * @return 생성된 토큰 문자열
     */
    public String createToken(Long userId, String userType) {
        // 토큰에 담을 정보(Claims)를 구성
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        // subject: 주로 토큰의 대표값(여기서는 userId라고 보면 됨)
        claims.put("userType", userType); // 토큰에 유저 타입도 같이 넣어둠

        // 현재 시간, 만료 시간 설정
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityInMillis);

        // 토큰을 생성하고 서명함
        return Jwts.builder()
                .setClaims(claims)           // 위에서 정의한 내용(유저 ID, 타입 등) 넣기
                .setIssuedAt(now)            // 토큰 발행 시각
                .setExpiration(expiration)   // 토큰 만료 시각
                .signWith(SignatureAlgorithm.HS256, secretKey) // 어떤 알고리즘으로 서명할지 + 비밀키
                .compact();
    }

    /**
     * JWT를 복호화(파싱)하여 Claims(토큰에 담긴 정보)를 꺼내는 메서드
     * @param token 클라이언트가 보낸 토큰 문자열
     * @return 토큰 안에 들어있는 Claims
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)          // 서명 검증에 쓴 비밀키
                .parseClaimsJws(token)             // 토큰 파싱
                .getBody();
    }
}
