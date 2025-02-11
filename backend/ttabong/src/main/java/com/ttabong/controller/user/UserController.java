package com.ttabong.controller.user;

import com.ttabong.dto.user.EmailCheckResponse;
import com.ttabong.dto.user.LoginRequest;
import com.ttabong.dto.user.LoginResponse;
import com.ttabong.dto.user.RegisterResponse;
import com.ttabong.dto.user.OrganizationRegisterRequest;
import com.ttabong.dto.user.VolunteerRegisterRequest;
import com.ttabong.entity.user.User;
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
    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        try {
            if (loginRequest.getUserType() == null || loginRequest.getUserType().isEmpty()) {
                return ResponseEntity.badRequest().body("userType이 필요합니다.");
            }

            Long userId = userService.login(loginRequest);

            String accessToken = jwtProvider.createToken(userId, loginRequest.getUserType());

            return ResponseEntity.ok(new LoginResponse(200, "로그인 성공", accessToken));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(401, "이메일 또는 비밀번호가 일치하지 않습니다.", null));
        }
    }

    @PostMapping("/volunteer/register")
    public ResponseEntity<?> registerVolunteer(@RequestBody VolunteerRegisterRequest request) {
        try {
            userService.registerVolunteer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(201,"봉사자 회원가입이 완료되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new RegisterResponse(400,e.getMessage()));
        }
    }

    @PostMapping("/org/register")
    public ResponseEntity<?> registerOrganization(@RequestBody OrganizationRegisterRequest request) {
        try {
            userService.registerOrganization(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(201,"기관 회원가입이 완료되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new RegisterResponse(400,e.getMessage()));
        }
    }

    @GetMapping("/user/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(@RequestParam String email, @RequestParam String type) {
        boolean exists = userService.checkEmail(email, type);
        String message;

        if ("find".equalsIgnoreCase(type)) {
            message = exists ? "해당 이메일이 존재합니다." : "해당 이메일을 찾을 수 없습니다.";
        } else if ("register".equalsIgnoreCase(type)) {
            message = exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.";
        } else {
            return ResponseEntity.badRequest().body(new EmailCheckResponse(400, false, "잘못된 타입입니다."));
        }

        if ("find".equalsIgnoreCase(type) && !exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EmailCheckResponse(404, exists, message));
        }

        return ResponseEntity.ok(new EmailCheckResponse(200, exists, message));
    }

}
