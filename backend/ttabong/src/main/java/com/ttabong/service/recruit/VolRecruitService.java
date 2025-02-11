package com.ttabong.service.recruit;

import com.ttabong.dto.recruit.responseDto.vol.ReadRecruitsResponseDto;
import com.ttabong.dto.recruit.responseDto.vol.ReadVolRecruitsResponseDto;
import com.ttabong.entity.recruit.Application;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.user.Volunteer;

import java.util.List;
import java.util.Optional;

public interface VolRecruitService {

    List<ReadVolRecruitsResponseDto> getTemplates(Integer cursor, Integer limit);

    Optional<Template> getTemplateById(Integer templateId);

    Application applyRecruit(int userId, int recruitId);
}
