package com.ttabong.dto.recruit.requestDto.org;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecruitsRequestDto {
    private Integer recruitId;
    private LocalDateTime deadline;
    private Date activityDate;
    private double activityStart;
    private double activityEnd;
    private Integer maxVolunteer;
    private List<String> images;
    private Integer imageCount;
}
