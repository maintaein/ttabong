package com.ttabong.service.recruit;

import com.ttabong.dto.recruit.requestDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.*;

import java.util.List;


public interface OrgRecruitService {

    ReadAvailableRecruitsResponseDto readAvailableRecruits(Integer cursor, Integer limit);
    ReadMyRecruitsResponseDto readMyRecruits(Integer cursor, Integer limit);
    DeleteRecruitsResponseDto deleteRecruits(DeleteRecruitsRequestDto deleteRecruitDto);
    UpdateRecruitsResponseDto updateRecruit(Integer recruitId, UpdateRecruitsRequestDto requestDto);
    CloseRecruitResponseDto closeRecruit(CloseRecruitRequestDto closeRecruitDto);
    UpdateGroupResponseDto updateGroup(UpdateGroupRequestDto updateGroupDto);
    UpdateTemplateResponse updateTemplate(UpdateTemplateRequestDto updateTemplateDto);
    DeleteTemplatesResponseDto deleteTemplates(DeleteTemplatesRequestDto deleteTemplatesDto);
    DeleteGroupResponseDto deleteGroup(DeleteGroupDto deleteGroupDto);
    ReadTemplatesResponseDto readTemplates(Integer cursor, Integer limit);
    CreateTemplateResponseDto createTemplate(CreateTemplateRequestDto createTemplateDto);
    CreateGroupResponseDto createGroup(CreateGroupRequestDto createGroupDto);
    CreateRecruitResponseDto createRecruit(CreateRecruitRequestDto createRecruitDto);
    ReadRecruitResponseDto readRecruit(Integer recruitId);
    ReadApplicationsResponseDto readApplications(Integer recruitId);
    UpdateApplicationsResponseDto updateStatuses(UpdateApplicationsRequestDto updateApplicationDto);
    List<EvaluateApplicationsResponseDto> evaluateApplicants(Integer recruitId, List<EvaluateApplicationsRequestDto> evaluateApplicationDtoList);
}
