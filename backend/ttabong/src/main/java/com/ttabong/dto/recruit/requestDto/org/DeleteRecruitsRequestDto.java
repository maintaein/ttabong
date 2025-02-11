package com.ttabong.dto.recruit.requestDto.org;

import com.ttabong.dto.GlobalBaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteRecruitsRequestDto implements GlobalBaseDto {
    private ArrayList<Integer> deletedRecruits;
}
