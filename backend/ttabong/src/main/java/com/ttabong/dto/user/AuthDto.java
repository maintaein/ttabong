package com.ttabong.dto.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AuthDto {
    private Integer userId;
    private String role;

    public AuthDto(Integer userId, String role) {
        this.userId = userId;
        this.role = role;
    }

}