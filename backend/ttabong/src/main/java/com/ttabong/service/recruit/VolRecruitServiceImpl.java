package com.ttabong.service.recruit;

import com.ttabong.dto.recruit.responseDto.vol.GroupDto;
import com.ttabong.dto.recruit.responseDto.vol.ReadRecruitDetailResponseDto;
import com.ttabong.dto.recruit.responseDto.vol.ReadVolRecruitsListResponseDto;
import com.ttabong.dto.recruit.responseDto.vol.ReadVolRecruitsResponseDto;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.dto.user.OrganizationDto;
import com.ttabong.entity.recruit.Application;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.user.Volunteer;
import com.ttabong.repository.recruit.ApplicationRepository;
import com.ttabong.repository.recruit.RecruitRepository;
import com.ttabong.repository.recruit.TemplateRepository;
import com.ttabong.repository.user.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ReadVolRecruitsListResponseDto getTemplates(Integer cursor, Integer limit) {
        AuthDto authDto = (AuthDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int userId = authDto.getUserId();
        String userType = authDto.getUserType();

//        System.out.println("요청한 사용자 ID: " + userId);
//        System.out.println("사용자 타입: " + userType);

        List<Template> templates = (cursor == null) ?
                templateRepository.findTopNTemplates(limit) :
                templateRepository.findTemplatesAfterCursor(cursor, limit);

        List<ReadVolRecruitsListResponseDto.TemplateWrapper> templateDetails = templates.stream()
                .map(template -> new ReadVolRecruitsListResponseDto.TemplateWrapper(
                        ReadVolRecruitsResponseDto.from(template),
                        GroupDto.from(template.getGroup()),
                        template.getOrg() != null ? OrganizationDto.from(template.getOrg()) : null
                ))
                .collect(Collectors.toList()); // ✅ Collectors.toList() 사용

        return new ReadVolRecruitsListResponseDto(templateDetails);
    }

    @Override
    public Optional<ReadRecruitDetailResponseDto> getTemplateById(Integer templateId) {
        return templateRepository.findById(templateId)
                .map(ReadRecruitDetailResponseDto::from);
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
