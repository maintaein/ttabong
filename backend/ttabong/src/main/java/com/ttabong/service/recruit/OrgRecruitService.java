package com.ttabong.service.recruit;

import com.ttabong.dto.recruit.responseDto.org.ReadAvailableRecruitsResponseDto;
import com.ttabong.dto.recruit.responseDto.org.ReadMyRecruitsResponseDto;


public interface OrgRecruitService {
    //1. 메인페이지 /api/org/templates/available?cursor={templateId}&limit={limit}
    ReadAvailableRecruitsResponseDto readAvailableRecruits(Integer cursor, Integer limit);

    ReadMyRecruitsResponseDto readMyRecruits(Integer cursor, Integer limit);
}
