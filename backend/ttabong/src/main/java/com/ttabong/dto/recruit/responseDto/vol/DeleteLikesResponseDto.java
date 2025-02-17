package com.ttabong.dto.recruit.responseDto.vol;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteLikesResponseDto {
    private String message;
    private List<Integer> deletedReactionIds;
}
