package com.ttabong.service.recruit;

import com.ttabong.dto.recruit.requestDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.*;


public interface OrgRecruitService {
    //1. 메인페이지 /api/org/templates/available?cursor={templateId}&limit={limit}
    ReadAvailableRecruitsResponseDto readAvailableRecruits(Integer cursor, Integer limit);

    ReadMyRecruitsResponseDto readMyRecruits(Integer cursor, Integer limit);

    DeleteRecruitsResponseDto deleteRecruits(DeleteRecruitsRequestDto deleteRecruitDto);

    UpdateRecruitsResponseDto updateRecruit(Integer recruitId, UpdateRecruitsRequestDto requestDto);

    CloseRecruitResponseDto closeRecruit(CloseRecruitRequestDto closeRecruitDto);

    UpdateGroupResponseDto updateGroup(UpdateGroupRequestDto updateGroupDto);

    UpdateTemplateResponse updateTemplate(UpdateTemplateRequestDto updateTemplateDto);

    DeleteTemplatesResponseDto deleteTemplates(DeleteTemplatesRequestDto deleteTemplatesDto);

    DeleteGroupResponseDto deleteGroup(DeleteGroupDto deleteGroupDto);

    ReadTemplatesResponseDto readTemplates(int cursor, int limit);

    CreateTemplateResponseDto createTemplate(CreateTemplateRequestDto createTemplateDto);

    CreateGroupResponseDto createGroup(CreateGroupRequestDto createGroupDto);

    CreateRecruitResponseDto createRecruit(CreateRecruitRequestDto createRecruitDto);

    ReadRecruitResponseDto readRecruit(int recruitId);
}
