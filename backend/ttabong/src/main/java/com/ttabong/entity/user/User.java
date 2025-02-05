package com.ttabong.entity.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, unique = true, length = 80)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    // 저장 전 BCrypt 암호화 할 것
    @Column(nullable = false, length = 256)
    private String password;

    @Column(nullable = false, length = 20)
    private String phone;

    // 얘는 널값 허용
    @Column(name = "profile_image", length = 200)
    private String profileImage;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
