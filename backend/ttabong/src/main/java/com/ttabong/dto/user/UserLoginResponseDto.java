package com.ttabong.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginResponseDto {
    private Integer userId;
    private String name;
    private String email;
}
