package com.ttabong.controller.user;

import com.ttabong.dto.user.LoginRequest;
import com.ttabong.dto.user.OrganizationRegisterRequest;
import com.ttabong.dto.user.VolunteerRegisterRequest;
import com.ttabong.entity.user.User;
import com.ttabong.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
            // 토큰 발급하자
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
