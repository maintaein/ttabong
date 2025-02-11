package com.ttabong.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private int status;
    private String message;
    private String access_token;
}
