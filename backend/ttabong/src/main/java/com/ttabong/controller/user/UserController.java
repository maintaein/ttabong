package com.ttabong.controller.user;

import com.ttabong.dto.user.*;
import com.ttabong.jwt.JwtProvider;
import com.ttabong.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("") //자동 기본값이 /api
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    // 로그인 엔드포인트
    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String message;
        LoginResponse loginResponse;
        System.out.println(loginRequest.getEmail());
        System.out.println(loginRequest.getPassword());

        try {
            Long userId = userService.login(loginRequest);

            //액세스토큰을 발급받자.
            //토큰에 담을 내용 : userId, loginRequest.getUserType()
            String accessToken = jwtProvider.createToken(userId, loginRequest.getUserType());

            message = "로그인 성공";

            loginResponse = new LoginResponse(message, accessToken); //여기에 토큰도 담아서 주면 됨

            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            System.out.println("problem" + e.getMessage());
            // 로그인 실패 시 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    // 봉사자 회원가입 엔드포인트
    @PostMapping("/volunteer/register")
    public ResponseEntity<?> registerVolunteer(@RequestBody VolunteerRegisterRequest request) {
        try {
            userService.registerVolunteer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("봉사자 회원가입이 완료되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 기관 회원가입 엔드포인트
    @PostMapping("/org/register")
    public ResponseEntity<?> registerOrganization(@RequestBody OrganizationRegisterRequest request) {
        try {
            userService.registerOrganization(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("기관 회원가입이 완료되었습니다.");
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
        if ("find".equalsIgnoreCase(type)) {
            message = exists ? "해당 이메일이 존재합니다." : "해당 이메일을 찾으 수 없습니다.";
            response = new EmailCheckResponse(exists, message);

            if (exists) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        //이메일 중복확인 시.
        else if ("register".equalsIgnoreCase(type)) {
            message = exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.";
            response = new EmailCheckResponse(exists, message);
            return ResponseEntity.ok(response);
        }
        response = new EmailCheckResponse(false, "잘못된 타입입니다.");
        return ResponseEntity.badRequest().body(response);
    }
}
