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


    // JWT를 만들 때 사용하는 '비밀키'
    private final String secretKey = "mysupersecretkeyasdflkgsalkdjfhlsdkflhashldf"; // 실제로는 안전하게 보관해야 함

    // JWT의 유효 시간 (예: 30분)
    private final long validityInMillis = 30 * 60 * 1000L * Integer.MAX_VALUE;

    /**
     * JWT 생성 메서드
     *
     * @param userId   JWT에 포함할 유저 ID
     * @param userType JWT에 포함할 유저 타입(예: "VOLUNTEER" or "ORGANIZATION")
     * @return 생성된 토큰 문자열
     */
    public String createToken(Long userId, String userType) {
        // 토큰에 담을 정보(Claims)를 구성
        Claims claims = Jwts.claims();
        // subject: 주로 토큰의 대표값(여기서는 userId라고 보면 됨)
        claims.setSubject(userId.toString()); // ✅ sub에 userId 저장
        claims.put("userType", userType); // 토큰에 유저 타입도 같이 넣어둠

        // 현재 시간, 만료 시간 설정
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityInMillis);
        System.out.println(claims.toString());
        // 토큰을 생성하고 서명함
        return Jwts.builder()
                .setClaims(claims)           // 위에서 정의한 내용(유저 ID, 타입 등) 넣기
                .setIssuedAt(now)            // 토큰 발행 시각
                .setExpiration(expiration)   // 토큰 만료 시각
                .signWith(SignatureAlgorithm.HS256, secretKey) // 어떤 알고리즘으로 서명할지 + 비밀키
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true; // 유효한 토큰
        } catch (ExpiredJwtException e) {
            System.out.println("토큰이 만료됨");
        } catch (JwtException e) {
            System.out.println("유효하지 않은 토큰");
        }
        return false; // 유효하지 않은 토큰
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }


    public AuthDto toAuthDto(String token) {
        Claims claims = getClaims(token);
        return new AuthDto(claims.get("userId", Integer.class), claims.get("userType", String.class));
    }
}
