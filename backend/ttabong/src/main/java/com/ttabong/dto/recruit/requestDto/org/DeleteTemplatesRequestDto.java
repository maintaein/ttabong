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
public class DeleteTemplatesRequestDto {
    private List<Integer> deletedTemplates;  // 삭제할 템플릿 ID 리스트
}
