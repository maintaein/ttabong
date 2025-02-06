package com.ttabong.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long userId;
    private String email;
    private String name;
    private String password;
    private String phone;
    private String profileImage;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
}
