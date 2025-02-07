package com.ttabong.dto.sns.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

/*
 * 4. 후기 _ 전체 조회 (봉사자+기관) (미리보기)_피드부분
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllReviewPreviewResponseDto {
    // 후기 정보
    private Integer reviewId;
    private Integer recruitId;
    private String title;
    private String content;
    private Boolean isDeleted;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    // 작성자 정보
    private Integer writerId;
    private String writerName;

    // 그룹 정보
    private Integer groupId;
    private String groupName;

    // 기관 정보
    private Integer orgId;
    private String orgName;

    // 이미지 리스트
    private List<String> images;
}
