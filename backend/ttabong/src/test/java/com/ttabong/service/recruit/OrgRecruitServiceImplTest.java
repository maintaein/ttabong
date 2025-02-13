package com.ttabong.service.recruit;

import com.ttabong.dto.recruit.responseDto.org.ReadAvailableRecruitsResponseDto;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.entity.recruit.Category;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.recruit.TemplateGroup;
import com.ttabong.repository.recruit.RecruitRepository;
import com.ttabong.repository.recruit.TemplateRepository;
import com.ttabong.util.service.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgRecruitServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(OrgRecruitServiceImplTest.class);

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private RecruitRepository recruitRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private OrgRecruitServiceImpl orgRecruitService;

    private final AuthDto mockAuthDto = new AuthDto(1, "organization");

    @Test
    void readAvailableRecruits_success() {

        // given
        Category parentCategory = Category.builder().id(1).name("환경").build();
        Category childCategory = Category.builder().id(2).name("플로깅").parent(parentCategory).build();
        TemplateGroup templateGroup = TemplateGroup.builder().id(1).groupName("기본 그룹").build();

        Template mockTemplate = Template.builder()
                .id(1)
                .category(childCategory)
                .title("하천 정화 봉사")
                .activityLocation("서울 한강")
                .status("ACTIVE")
                .contactName("홍길동")
                .contactPhone("010-1234-5678")
                .description("하천 주변 정화 활동")
                .createdAt(Instant.now())
                .group(templateGroup)
                .build();

        Recruit mockRecruit = Recruit.builder()
                .id(1)
                .deadline(Instant.now())
                .activityDate(new Date())
                .activityStart(BigDecimal.ZERO)
                .activityEnd(BigDecimal.ONE)
                .maxVolunteer(10)
                .participateVolCount(5)
                .status("OPEN")
                .updatedAt(Instant.now())
                .createdAt(Instant.now())
                .template(mockTemplate)
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        when(templateRepository.findAvailableTemplates(nullable(Integer.class), eq(mockAuthDto.getUserId()), any(Pageable.class)))
                .thenReturn(List.of(mockTemplate));

        when(recruitRepository.findByTemplateId(eq(mockTemplate.getId())))
                .thenReturn(List.of(mockRecruit));

        when(imageService.getImageUrls(eq(mockTemplate.getId()), eq(true)))
                .thenReturn(List.of("https://example.com/image.jpg"));

        // info 로그 추가
        log.info("***************** Template repository returned: {}", templateRepository.findAvailableTemplates(null, mockAuthDto.getUserId(), pageable));
        log.info("***************** Recruit repository returned: {}", recruitRepository.findByTemplateId(mockTemplate.getId()));

        // when
        ReadAvailableRecruitsResponseDto response = orgRecruitService.readAvailableRecruits(null, 10, mockAuthDto);
        ReadAvailableRecruitsResponseDto.Template expectedTemplateDto = convertToTemplateDto(mockTemplate, "https://example.com/image.jpg");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTemplates()).isNotNull().hasSize(1);

        assertThat(response.getTemplates().get(0).getTemplate())
                .usingRecursiveComparison()
                .isEqualTo(expectedTemplateDto);

        verify(templateRepository, times(2)).findAvailableTemplates(nullable(Integer.class), eq(mockAuthDto.getUserId()), any(Pageable.class));
        verify(recruitRepository, times(2)).findByTemplateId(eq(mockTemplate.getId()));
        verify(imageService, times(1)).getImageUrls(eq(mockTemplate.getId()), eq(true));
    }

    private ReadAvailableRecruitsResponseDto.Template convertToTemplateDto(Template template, String imageUrl) {
        return ReadAvailableRecruitsResponseDto.Template.builder()
                .templateId(template.getId())
                .categoryId(template.getCategory() != null ? template.getCategory().getId() : null)
                .title(template.getTitle())
                .activityLocation(template.getActivityLocation())
                .status(template.getStatus())
                .imageUrl(imageUrl)
                .contactName(template.getContactName())
                .contactPhone(template.getContactPhone())
                .description(template.getDescription())
                .createdAt(LocalDateTime.ofInstant(template.getCreatedAt(), ZoneId.systemDefault()))
                .build();
    }
}
