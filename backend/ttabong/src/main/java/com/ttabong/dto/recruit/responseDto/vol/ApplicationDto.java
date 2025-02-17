package com.ttabong.dto.recruit.responseDto.vol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDto {
    private Integer applicationId;
    private String name;
    private String status;
}
