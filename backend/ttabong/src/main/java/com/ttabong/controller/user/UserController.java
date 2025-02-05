package com.ttabong.controller.user;

import com.ttabong.dto.user.LoginRequest;
import com.ttabong.dto.user.OrganizationRegisterRequest;
import com.ttabong.dto.user.VolunteerRegisterRequest;
import com.ttabong.entity.user.User;
import com.ttabong.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 로그인 엔드포인트
    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.login(loginRequest);
            System.out.println("user : " + user);
            // 로그인 성공 시 200 OK와 함께 사용자 정보를 반환 (토큰 발급 로직 추가 가능)
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            // 로그인 실패 시 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
    }

    // 봉사자 회원가입 엔드포인트
    @PostMapping("/volunteer/register")
    public ResponseEntity<?> signUpVolunteer(@RequestBody VolunteerRegisterRequest request) {
        try {
            User user = userService.registerVolunteer(request);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 기관 회원가입 엔드포인트
    @PostMapping("/api/org/register")
    public ResponseEntity<?> signUpOrganization(@RequestBody OrganizationRegisterRequest request) {
        try {
            User user = userService.registerOrganization(request);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
