package com.ttabong.dto.recruit.requestDto.org;

import com.ttabong.dto.GlobalBaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecruitsRequestDto implements GlobalBaseDto {
    private Integer recruitId;
    private LocalDateTime deadline;
    private Date activityDate;
    private BigDecimal activityStart;
    private BigDecimal activityEnd;
    private Integer maxVolunteer;
    private List<String> images;
    private Integer imageCount;
}
