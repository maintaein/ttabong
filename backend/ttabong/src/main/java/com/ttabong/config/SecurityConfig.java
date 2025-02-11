package com.ttabong.config;

import com.ttabong.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1) csrf 설정 부분을 람다로 바꿈
                .csrf(csrf -> csrf.disable())
                // 2) authorizeHttpRequests 또한 람다로
                // ✅ JWT 인증이 적용되도록 요청별 인증 정책 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/check-email", "/user/login",
                                "/org/register", "/volunteer/register").permitAll()  // 로그인 & 회원가입만 허용
//                        .anyRequest().authenticated()  // 나머지는 인증 필요
                                .anyRequest().permitAll()
                )
                // ✅ HTTP Basic 인증 제거 (JWT 기반이므로 필요 없음)
                .httpBasic(httpBasic -> httpBasic.disable())
                // ✅ JWT 필터 추가 (UsernamePasswordAuthenticationFilter 전에 실행)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        ;// 3) httpBasic 설정(필요하다면)


        return http.build();
    }
}
