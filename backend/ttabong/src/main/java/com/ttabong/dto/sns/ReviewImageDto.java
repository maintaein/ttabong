package com.ttabong.dto.sns;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImageDto {
    private Long imageId;
    private Long reviewId;
    private String imageUrl;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private Long nextImageId; // 다음 이미지 ID
}
