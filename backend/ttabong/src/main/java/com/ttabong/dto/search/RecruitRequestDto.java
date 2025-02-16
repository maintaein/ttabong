package com.ttabong.dto.search;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RecruitRequestDto {
    private String recruitTitle;
    private String status;
    private String region;
    private LocalDate startDate;
    private LocalDate endDate;
}
