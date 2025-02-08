package com.ttabong.dto.sns.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/*
 * 5. 후기 _ 공개 여부 설정
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewVisibilitySettingRequestDto {
    private Boolean isPublic; // 현재 상태 전달 (백엔드에서 반대로 변경)
}
