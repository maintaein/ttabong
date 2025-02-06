package com.ttabong.controller.user;

import com.ttabong.dto.user.EmailCheckResponse;
import com.ttabong.dto.user.LoginRequest;
import com.ttabong.dto.user.OrganizationRegisterRequest;
import com.ttabong.dto.user.VolunteerRegisterRequest;
import com.ttabong.entity.user.User;
import com.ttabong.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("") //자동 기본값이 /api
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
    @PostMapping("/org/register")
    public ResponseEntity<?> signUpOrganization(@RequestBody OrganizationRegisterRequest request) {
        try {
            User user = userService.registerOrganization(request);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email, @RequestParam String type) {
        boolean exists = userService.checkEmail(email, type);
        String message;
        EmailCheckResponse response;

        //계정찾기 시, 이렇게 이메일 체크 후 (+인증 후) 비번 변경 가능.
        if("find".equalsIgnoreCase(type)) {
            message = exists ? "해당 이메일이 존재합니다." : "해당 이메일을 찾으 수 없습니다.";
            response = new EmailCheckResponse(exists, message);

            if(exists){
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        //이메일 중복확인 시.
        else if("register".equalsIgnoreCase(type)) {
            message = exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.";
            response = new EmailCheckResponse(exists, message);
            return ResponseEntity.ok(response);
        }
        response = new EmailCheckResponse(false, "잘못된 타입입니다.");
        return ResponseEntity.badRequest().body(response);
    }
}
