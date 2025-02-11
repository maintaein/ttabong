package com.ttabong.service.recruit;

import com.ttabong.dto.recruit.responseDto.vol.ReadVolRecruitsResponseDto;
import com.ttabong.entity.recruit.Application;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.user.Volunteer;
import com.ttabong.repository.recruit.ApplicationRepository;
import com.ttabong.repository.recruit.RecruitRepository;
import com.ttabong.repository.recruit.TemplateRepository;
import com.ttabong.repository.user.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VolRecruitServiceImpl implements VolRecruitService {
    private final RecruitRepository recruitRepository;
    private final TemplateRepository templateRepository;
    private final VolunteerRepository volunteerRepository;
    private final ApplicationRepository applicationRepository;

    @Autowired
    public VolRecruitServiceImpl(
            RecruitRepository recruitRepository,
            TemplateRepository templateRepository,
            VolunteerRepository volunteerRepository,
            ApplicationRepository applicationRepository) {
        this.recruitRepository = recruitRepository;
        this.templateRepository = templateRepository;
        this.volunteerRepository = volunteerRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public List<ReadVolRecruitsResponseDto> getTemplates(Integer cursor, Integer limit) {
        List<Template> templates = (cursor == null)
                ? templateRepository.findTopNTemplates(limit)
                : templateRepository.findTemplatesAfterCursor(cursor, limit);

        return templates.stream()
                .map(ReadVolRecruitsResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Template> getTemplateById(Integer templateId) {
        return templateRepository.findById(templateId);
    }

    @Override
    public Application applyRecruit(int userId, int recruitId) {
        Volunteer volunteer = volunteerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("봉사자를 찾을 수 없습니다."));

        Recruit recruit = recruitRepository.findByRecruitId(recruitId)
                .orElseThrow(() -> new IllegalArgumentException("봉사 공고를 찾을 수 없습니다."));

        Application application = Application.builder()
                .volunteer(volunteer)
                .recruit(recruit)
                .status("PENDING")
                .createdAt(Instant.now())
                .build();

        return applicationRepository.save(application);
    }
}
