package com.ttabong.dto.recruit.responseDto.org;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteRecruitsResponseDto {
    private String message;
    private List<Integer> deletedRecruits;
}
