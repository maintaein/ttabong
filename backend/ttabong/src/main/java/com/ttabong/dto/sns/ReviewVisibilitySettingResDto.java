package com.ttabong.dto.sns;

import lombok.*;

import java.time.LocalDateTime;

/*
 * 5. 후기 _ 공개 여부 설정
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewVisibilitySettingResDto {
    private String message;
    private Long reviewId;
    private Boolean isPublic;
    private LocalDateTime updatedAt;
}

