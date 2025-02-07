package com.ttabong.dto.recruit.requestDto.org;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTemplateRequestDto {
    private Integer groupId; // 그룹 ID
    private Integer orgId; // 조직 ID
    private Integer categoryId; // 카테고리 ID
    private String title; // 봉사 제목
    private String activityLocation; // 활동 장소
    private String status; // 상태 (ALL, ACTIVE 등)
    private List<String> images; // 이미지 리스트
    private Integer imageCount; // 이미지 개수
    private String contactName; // 담당자 이름
    private String contactPhone; // 담당자 전화번호
    private String description; // 봉사 설명
}