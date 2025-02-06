package com.ttabong.dto.recruit.requestDto.org;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRecruitRequestDto {

    private int templateId; // 템플릿 ID
    private LocalDateTime deadline; // 지원 마감일
    private LocalDate activityDate; // 활동 날짜
    private double activityStart; // 활동 시작 시간 (14.00 → 14:00)
    private double activityEnd; // 활동 종료 시간 (20.00 → 20:00)
    private int maxVolunteer; // 최대 모집 인원

}
