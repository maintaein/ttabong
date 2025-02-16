package com.ttabong.dto.search;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RecruitRequestDto {
    private String recruitTitle;  // 검색어 (제목, 기관명 검색)
    private String status;         // 모집 상태 (예: RECRUITING, CLOSED)
    private String region;         // 지역 필터링
    private LocalDate startDate;   // 활동 시작일
    private LocalDate endDate;     // 활동 종료일
}
