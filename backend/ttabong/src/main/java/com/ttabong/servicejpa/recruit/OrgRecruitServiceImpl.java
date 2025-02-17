package com.ttabong.servicejpa.recruit;

import com.ttabong.dto.recruit.requestDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.*;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.entity.recruit.*;
import com.ttabong.entity.sns.ReviewImage;
import com.ttabong.entity.user.Organization;
import com.ttabong.exception.*;
import com.ttabong.repositoryjpa.recruit.*;
import com.ttabong.repositoryjpa.sns.ReviewImageRepositoryJpa;
import com.ttabong.repositoryjpa.user.OrganizationRepositoryJpa;
import com.ttabong.repositoryjpa.user.VolunteerRepositoryJpa;
import com.ttabong.util.CacheUtil;
import com.ttabong.util.ImageUtil;
import com.ttabong.util.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrgRecruitServiceImpl implements OrgRecruitServiceJpa {
    private final RecruitRepositoryJpa recruitRepository;
    private final TemplateRepositoryJpa templateRepository;
    private final TemplateGroupRepositoryJpa templateGroupRepository;
    private final OrganizationRepositoryJpa organizationRepository;
    private final CategoryRepositoryJpa categoryRepository;
    private final ApplicationRepositoryJpa applicationRepository;
    private final VolunteerRepositoryJpa volunteerRepository;
    private final ImageService imageService;
    private final ImageUtil imageUtil;
    private final CacheUtil cacheUtil;
    private final ReviewImageRepositoryJpa reviewImageRepository;

    public void checkOrgToken(AuthDto authDto) {
        if (authDto == null || authDto.getUserId() == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        } else if (!"organization".equalsIgnoreCase(authDto.getUserType())) {
            throw new ForbiddenException("기관 계정으로 로그인을 해야 합니다.");
        }
    }

    @Transactional(readOnly = true)
    public ReadAvailableRecruitsResponseDto readAvailableRecruits(Integer cursor, Integer limit, AuthDto authDto) {
        try {
            checkOrgToken(authDto);
            if (cursor == null || cursor == 0) {
                cursor = Integer.MAX_VALUE;
            }
            if (limit == null || limit == 0) {
                limit = 10;
            }
            List<Recruit> recruits = recruitRepository.findRecruitByTemplateOrgIdAndIsDeletedFalse(authDto.getUserId(), PageRequest.of(cursor, limit));

            List<ReadAvailableRecruitsResponseDto.TemplateDetail> templateDetails = recruits.stream().map((recruit) ->{
                try {
                    return ReadAvailableRecruitsResponseDto.TemplateDetail.builder()
                    .template(ReadAvailableRecruitsResponseDto.Template.builder()
                            .templateId(recruit.getTemplate().getId())
                            .categoryId(recruit.getTemplate().getCategory().getId())
                            .title(recruit.getTemplate().getTitle())
                            .activityLocation(recruit.getTemplate().getActivityLocation())
                            .status(recruit.getTemplate().getStatus())
                            .imageUrl(imageUtil.getPresignedDownloadUrl(String.valueOf(recruit.getTemplate().getThumbnailImage())))
                            .contactName(recruit.getTemplate().getContactName())
                            .contactPhone(recruit.getTemplate().getContactPhone())
                            .description(recruit.getTemplate().getDescription())
                            .createdAt(LocalDateTime.from(recruit.getTemplate().getCreatedAt()))
                            .build())
                    .group(ReadAvailableRecruitsResponseDto.Group.builder()
                            .groupId(recruit.getTemplate().getGroup().getId())
                            .groupName(recruit.getTemplate().getGroup().getGroupName())
                            .build())
                    .recruits(recruits.stream().map(Recruit ->{
                        return ReadAvailableRecruitsResponseDto.Recruit.builder()
                            .recruitId(recruit.getId())
                            .deadline(LocalDateTime.from(recruit.getDeadline()))
                            .activityDate(recruit.getActivityDate())
                            .activityStart(recruit.getActivityStart())
                            .activityEnd(recruit.getActivityEnd())
                            .maxVolunteer(recruit.getMaxVolunteer())
                            .participateVolCount(recruit.getParticipateVolCount())
                            .status(recruit.getStatus())
                            .updatedAt(LocalDateTime.from(recruit.getUpdatedAt()))
                            .createdAt(LocalDateTime.from(recruit.getCreatedAt()))
                            .build();
                    }).toList())
                    .build();
                } catch (Exception e) {
                    log.error("이미지 URL생성 관련 오류 발생, {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }).toList();
            return ReadAvailableRecruitsResponseDto.builder().templates(templateDetails).build();
            } catch (NotFoundException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("모집 정보를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReadMyRecruitsResponseDto readMyRecruits(Integer cursor, Integer limit, AuthDto authDto) {
        return null;
    }/*
        checkOrgToken(authDto);
        if (cursor == null) {
            cursor = Integer.MAX_VALUE;
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));

        List<Recruit> recruits = recruitRepository.(cursor, authDto.getUserId(), pageable);

        if (recruits.isEmpty()) {
            throw new NotFoundException("활성화된 모집 공고가 없습니다.");
        }

        List<ReadMyRecruitsResponseDto.RecruitDetail> recruitDetails = recruits.stream().map(recruit -> {
            Template template = recruit.getTemplate();
            TemplateGroup templateGroup = template.getGroup();

            ReadMyRecruitsResponseDto.Group group = templateGroup != null ?
                    ReadMyRecruitsResponseDto.Group.builder()
                            .groupId(templateGroup.getId())
                            .groupName(templateGroup.getGroupName())
                            .build()
                    : null;

            ReadMyRecruitsResponseDto.Template dtoTemplate = ReadMyRecruitsResponseDto.Template.builder()
                    .templateId(template.getId())
                    .title(template.getTitle())
                    .build();

            ReadMyRecruitsResponseDto.Recruit dtoRecruit = ReadMyRecruitsResponseDto.Recruit.builder()
                    .recruitId(recruit.getId())
                    .status(recruit.getStatus())
                    .maxVolunteer(recruit.getMaxVolunteer())
                    .participateVolCount(recruit.getParticipateVolCount())
                    .activityDate(recruit.getActivityDate() != null ? recruit.getActivityDate() : new Date())
                    .activityStart(recruit.getActivityStart() != null ? recruit.getActivityStart() : BigDecimal.valueOf(10.00))
                    .activityEnd(recruit.getActivityEnd() != null ? recruit.getActivityEnd() : BigDecimal.valueOf(12.00))
                    .deadline(recruit.getDeadline() != null ?
                            recruit.getDeadline().atZone(ZoneId.systemDefault()).toLocalDateTime()
                            : LocalDateTime.now())
                    .createdAt(recruit.getCreatedAt() != null ?
                            recruit.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                            : LocalDateTime.now())
                    .build();

            return ReadMyRecruitsResponseDto.RecruitDetail.builder()
                    .group(group)
                    .template(dtoTemplate)
                    .recruit(dtoRecruit)
                    .build();
        }).collect(Collectors.toList());

        return ReadMyRecruitsResponseDto.builder()
                .recruits(recruitDetails)
                .build();
    }
    */
    @Override
    public DeleteRecruitsResponseDto deleteRecruits(DeleteRecruitsRequestDto deleteRecruitDto, AuthDto authDto) {

        checkOrgToken(authDto);

        List<Integer> recruitIds = deleteRecruitDto.getDeletedRecruits();

        List<Recruit> recruitsToDelete = recruitRepository.findRecruitByIdInAndIsDeletedFalse(recruitIds);

        if (recruitsToDelete.isEmpty()) {
            throw new NotFoundException("삭제할 수 있는 공고가 없습니다. 이미 삭제되었거나 권한이 없습니다.");
        }else {
            recruitsToDelete.forEach(recruit -> {
                if(recruit.getTemplate().getOrg().getId().equals(authDto.getUserId())) {
                    recruit.updateDelete();
                }
            });
        }
        return DeleteRecruitsResponseDto.builder()
                .message("공고 삭제 완료")
                .deletedRecruits(recruitIds)
                .build();
    }

    @Override
    public UpdateRecruitsResponseDto updateRecruit(Integer recruitId, UpdateRecruitsRequestDto requestDto, AuthDto authDto) {

        checkOrgToken(authDto);

        Instant deadlineInstant = requestDto.getDeadline() != null
                ? requestDto.getDeadline().atZone(ZoneId.systemDefault()).toInstant()
                : Instant.now();

        Date activityDate = requestDto.getActivityDate() != null
                ? new java.sql.Date(requestDto.getActivityDate().getTime())
                : new java.sql.Date(System.currentTimeMillis());

        Optional<Recruit> recruit = Optional.ofNullable(recruitRepository.findRecruitByIdAndIsDeletedFalse(recruitId));
        if(recruit.isEmpty()) {
            log.info("{}번 공고를 찾을 수 없습니다", recruitId);
            //공고 못찾음 에러
        }else if(!recruit.get().getTemplate().getOrg().getId().equals(authDto.getUserId())) {
            log.info("{}번 공고에 대한 권한이 {}에게 없습니다", recruitId, authDto.getUserId());
            //내 공고 아니여서 권한 없음
        }else{
            recruit.get().patchUpdate(requestDto);
            cacheUtil.addCompleteEventScheduler(recruitId, getMinutesToCompleteEvent(recruit.get()));
            cacheUtil.addDeadlineEventScheduler(recruitId, getMinutesToDeadlineEvent(recruit.get()));
        }
        //리턴 안으로 넣기
        return UpdateRecruitsResponseDto.builder()
                .message("공고 수정 완료")
                .recruitId(recruitId)
                .build();

    }

    @Override
    public CloseRecruitResponseDto closeRecruit(CloseRecruitRequestDto closeRecruitDto, AuthDto authDto) {
        checkOrgToken(authDto);
        Integer recruitId = closeRecruitDto.getRecruitId();
        Optional<Recruit> recruit = Optional.ofNullable(recruitRepository.findRecruitByIdAndIsDeletedFalse(recruitId));

        if (recruit.isEmpty()) {
            log.info("{}번 공고를 찾을 수 없습니다", recruitId);
            throw new NotFoundException("해당 공고를 찾을 수 없습니다.");
        }else if(!recruit.get().getTemplate().getOrg().getId().equals(authDto.getUserId())) {
            log.info("{}번 공고에 대한 권한이 {}에게 없습니다", recruitId, authDto.getUserId());
            //내 공고 아니여서 권한이 없음
        }else{
            log.info("{}번 공고의 상태를 'CLOSED' 로 변경했습니다", recruitId);
            recruit.get().statusClose();
            cacheUtil.removeDeadlineSchedule(recruitId);
        }

        return CloseRecruitResponseDto.builder()
                .message("공고 마감 완료")
                .recruitId(recruitId)
                .build();
    }

    //12
    @Override
    public UpdateGroupResponseDto updateGroup(UpdateGroupRequestDto updateGroupDto, AuthDto authDto) {

        checkOrgToken(authDto);
        Optional<TemplateGroup> group = templateGroupRepository.findByIdAndIsDeletedFalse(updateGroupDto.getGroupId());
        if(group.isEmpty()) {
            log.info("{}번 템플릿 그룹를 찾을 수 없습니다", updateGroupDto.getGroupId());
        }else if(!group.get().getOrg().getId().equals(authDto.getUserId())) {
            log.info("{}번 템플릿 그룹에 대한 권한이 {}에게 없습니다", updateGroupDto.getGroupId(), authDto.getUserId());
        }else {
            group.get().updateName(updateGroupDto.getGroupName());
        }

        return UpdateGroupResponseDto.builder()
                .message("수정 성공")
                .groupId(updateGroupDto.getGroupId())
                .orgId(authDto.getUserId())
                .build();
    }

    @Override
    public UpdateTemplateResponse createTemplate(UpdateTemplateRequestDto updateTemplateDto, AuthDto authDto) {

        checkOrgToken(authDto);
        Optional<Template> template = templateRepository.findTemplateById(updateTemplateDto.getTemplateId());
        if (!template.get().getOrg().getId().equals(authDto.getUserId())) {
            //권한 없는 유저(내 템플릿 아님)
        } else {
            Template newTemplate =
                    Template.updateTemplate(template.get(), updateTemplateDto
                        , templateGroupRepository.getReferenceById(updateTemplateDto.getTemplateId())
                        , organizationRepository.getReferenceById(updateTemplateDto.getOrgId())
                        , categoryRepository.getReferenceById(updateTemplateDto.getCategoryId())
                    );
            Template finalNewTemplate = templateRepository.save(newTemplate);
            List<ReviewImage> reviewImages = updateTemplateDto.getImages().stream().map((image) ->{
                return reviewImageRepository.save(ReviewImage.builder()
                    .template(finalNewTemplate)
                    .imageUrl(cacheUtil.findObjectPath(image))
                    .isDeleted(false)
                    .createdAt(Instant.now())
                    .build());
            }).toList();
        }

        return UpdateTemplateResponse.builder()
                .message("템플릿 수정 성공")
                .templateId(updateTemplateDto.getTemplateId())
                .orgId(authDto.getUserId())
                .build();
    }

    @Override
    public DeleteTemplatesResponseDto deleteTemplates(DeleteTemplatesRequestDto deleteTemplatesDto, AuthDto authDto) {

        checkOrgToken(authDto);

        List<Template> templatesToDelete = templateRepository.findByIdInAndIsDeletedFalse(deleteTemplatesDto.getDeletedTemplates());

        if (templatesToDelete.isEmpty()) {
            //throw new NotFoundException("삭제할 수 있는 템플릿이 없습니다.")
            //나의 공고가 아님
        } else {
            templatesToDelete.forEach(template -> {
                if (!template.getOrg().getId().equals(authDto.getUserId())) {
                    template.deleteTemplate();
                }
            });
        }
        return DeleteTemplatesResponseDto.builder()
                .message("템플릿 삭제 성공")
                .deletedTemplates(templatesToDelete.stream().map(Template::getId).toList())
                .build();
    }
    @Override
    public DeleteGroupResponseDto deleteGroup(DeleteGroupDto deleteGroupDto, AuthDto authDto) {

        checkOrgToken(authDto);

        Organization userOrg = organizationRepository.findByUserId(authDto.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 기관이 없습니다."));

        Optional<TemplateGroup> groupToDelete = templateGroupRepository.findByIdAndIsDeletedFalse(deleteGroupDto.getGroupId());

        if(groupToDelete.isEmpty()) {
            //그룹이 없습니다
            return null;
        } else if (!groupToDelete.get().getOrg().getId().equals(authDto.getUserId())) {
            //내 그룹이 아니여서 그룹을 삭제할 수 없습니다.
            return null;
        } else {
            groupToDelete.get().markDeleted();
            return DeleteGroupResponseDto.builder()
                    .message("삭제 성공")
                    .groupId(groupToDelete.get().getId())
                    .orgId(userOrg.getId())
                    .build();
        }
    }

//7 미완성
    @Override
    @Transactional(readOnly = true)
    public ReadTemplatesResponseDto readTemplates(Integer cursor, Integer limit, AuthDto authDto) {

        Pageable pageable = PageRequest.of(cursor, limit);
        List<Template> templates = templateRepository.findByGroupIdAndIsDeletedFalse(cursor, pageable);

        //ReadTemplatesResponseDto.builder().


        return ReadTemplatesResponseDto.builder()
                //.groups(groupDtos)
                .build();
    }
//6
    // TODO: 봉사 그룹의 디폴트 이름을 봉사 그룹 개수를 세어서 만들어보자(중복되지 않도록)
    @Override
    public CreateGroupResponseDto createGroup(CreateGroupRequestDto createGroupDto, AuthDto authDto) {

        checkOrgToken(authDto);
        if(createGroupDto.getGroupName().isBlank()) {
            //빈 문자열
            return null;
        }
        Optional<Organization> org = organizationRepository.findById(authDto.getUserId());
        if(org.isEmpty()) {
            //유저가 잘못됨
            return null;
        }
        Optional<TemplateGroup> group = templateGroupRepository
                .findByOrgIdAndByGroupNameAndIsDeletedFalse(authDto.getUserId(), createGroupDto.getGroupName());
        if(group.isPresent()){
            return null;
        }
        TemplateGroup templateGroup = templateGroupRepository.save(
            TemplateGroup.builder().org(org.get())
            .groupName(createGroupDto.getGroupName())
            .isDeleted(false)
            .build());
        return CreateGroupResponseDto.builder()
            .message("그룹 생성 성공")
            .groupId(templateGroup.getId())
            .build();
    }
//5
    @Override
    public CreateRecruitResponseDto createRecruit(CreateRecruitRequestDto createRecruitDto, AuthDto authDto) {

        checkOrgToken(authDto);
        /*
        Organization org = organizationRepository.findByUserId(authDto.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 유저의 기관 정보 없음"));
        Template template = templateRepository.findById(createRecruitDto.getTemplateId())
                .orElseThrow(() -> new NotFoundException("해당 템플릿이 존재하지 않습니다."));
        if (!template.getOrg().getId().equals(org.getId())) {
            throw new ForbiddenException("해당 템플릿에 대한 권한이 없습니다.");
        }
        */
        Optional<Template> baseTemplate =  templateRepository.findByIdAndIsDeletedFalse(createRecruitDto.getTemplateId());
        if(baseTemplate.isEmpty()) {
            //템플릿이 없음
            return null;
        }
        if(baseTemplate.get().getOrg().getId().equals(authDto.getUserId())) {
            //템플릿 권한이 없음
        }
        Recruit recruit = recruitRepository.save(
                Recruit.builder()
                        .template(baseTemplate.get())
                        .deadline(localDateTimeToInstant(createRecruitDto.getDeadline()))
                        .activityDate(createRecruitDto.getActivityDate())
                        .activityStart(createRecruitDto.getActivityStart())
                        .activityEnd(createRecruitDto.getActivityEnd())
                        .maxVolunteer(createRecruitDto.getMaxVolunteer())
                        .status("RECRUITING")
                        .isDeleted(false)
                        .createdAt(localDateTimeToInstant(LocalDateTime.now()))
                        .build()
        );
        cacheUtil.addDeadlineEventScheduler(recruit.getId(), getMinutesToDeadlineEvent(recruit));
        cacheUtil.addCompleteEventScheduler(recruit.getId(), getMinutesToCompleteEvent(recruit));

        return CreateRecruitResponseDto.builder()
                .message("공고 생성 완료")
                .recruitId(recruit.getId())
                .build();
    }
//4
    @Override
    @Transactional(readOnly = true)
    public ReadRecruitResponseDto readRecruit(Integer recruitId, AuthDto authDto) {

        Recruit recruit = recruitRepository.findByRecruitId(recruitId)
                .orElseThrow(() -> new NotFoundException("해당 공고가 없거나 삭제되었습니다."));

        LocalDateTime deadlineLocalDateTime = recruit.getDeadline() != null
                ? LocalDateTime.ofInstant(recruit.getDeadline(), ZoneId.systemDefault())
                : null;

        LocalDateTime updatedAtLocalDateTime = recruit.getUpdatedAt() != null
                ? LocalDateTime.ofInstant(recruit.getUpdatedAt(), ZoneId.systemDefault())
                : LocalDateTime.now();

        LocalDateTime createdAtLocalDateTime = recruit.getCreatedAt() != null
                ? LocalDateTime.ofInstant(recruit.getCreatedAt(), ZoneId.systemDefault())
                : LocalDateTime.now();

        Date activityDate = recruit.getActivityDate() != null ? recruit.getActivityDate() : new Date();

        ReadRecruitResponseDto.Recruit recruitDto = ReadRecruitResponseDto.Recruit.builder()
                .recruitId(recruit.getId())
                .deadline(deadlineLocalDateTime)
                .activityDate(activityDate)
                .activityStart(recruit.getActivityStart() != null ? recruit.getActivityStart() : BigDecimal.valueOf(10.00))
                .activityEnd(recruit.getActivityEnd() != null ? recruit.getActivityEnd() : BigDecimal.valueOf(12.00))
                .maxVolunteer(recruit.getMaxVolunteer())
                .participateVolCount(recruit.getParticipateVolCount())
                .status(recruit.getStatus())
                .updatedAt(updatedAtLocalDateTime)
                .createdAt(createdAtLocalDateTime)
                .build();

        ReadRecruitResponseDto.Group groupDto = new ReadRecruitResponseDto.Group(
                recruit.getTemplate().getGroup().getId(),
                recruit.getTemplate().getGroup().getGroupName()
        );

        LocalDateTime templateCreatedAt = recruit.getTemplate().getCreatedAt() != null
                ? LocalDateTime.ofInstant(recruit.getTemplate().getCreatedAt(), ZoneId.systemDefault())
                : LocalDateTime.now();

        List<String> imageUrls = imageService.getImageUrls(recruit.getTemplate().getId(), true);

        ReadRecruitResponseDto.Template templateDto = ReadRecruitResponseDto.Template.builder()
                .templateId(recruit.getTemplate().getId())
                .categoryId(recruit.getTemplate().getCategory() != null ? recruit.getTemplate().getCategory().getId() : null)
                .title(recruit.getTemplate().getTitle())
                .activityLocation(recruit.getTemplate().getActivityLocation())
                .status(recruit.getTemplate().getStatus())
                .images(imageUrls)
                .contactName(recruit.getTemplate().getContactName())
                .contactPhone(recruit.getTemplate().getContactPhone())
                .description(recruit.getTemplate().getDescription())
                .createdAt(templateCreatedAt)
                .build();

        ReadRecruitResponseDto.Organization orgDto = new ReadRecruitResponseDto.Organization(
                recruit.getTemplate().getOrg().getId(),
                recruit.getTemplate().getOrg().getOrgName()
        );

        return ReadRecruitResponseDto.builder()
                .group(groupDto)
                .template(templateDto)
                .recruit(recruitDto)
                .organization(orgDto)
                .build();
    }
//3
    @Override
    @Transactional(readOnly = true)
    public ReadApplicationsResponseDto readApplications(Integer recruitId, AuthDto authDto) {

        checkOrgToken(authDto);

        Optional<Recruit> recruit = recruitRepository.findByRecruitId(recruitId);
        if(recruit.isEmpty()) {
            //해당공고 없음
        }
        if(!recruit.get().getTemplate().getOrg().getId().equals(authDto.getUserId())) {
            //공고권한 없음
            return null;
        }

        List<Application> applications = applicationRepository.findByRecruitId(recruitId);

        List<ReadApplicationsResponseDto.ApplicationDetail> applicationDetails = applications.stream()
            .map(application -> {
                String profileImagePath = application.getVolunteer().getUser().getProfileImage();

                String profileImageUrl;
                try {
                    profileImageUrl = (profileImagePath != null) ? imageUtil.getPresignedDownloadUrl(profileImagePath) : imageUtil.getPresignedDownloadUrl("0");
                } catch (Exception e) {
                    throw new ImageProcessException("프로필 이미지 URL 생성 중 오류 발생", e);
                }

                return ReadApplicationsResponseDto.ApplicationDetail.builder()
                    .user(ReadApplicationsResponseDto.User.builder()
                        .userId(application.getVolunteer().getUser().getId())
                        .email(application.getVolunteer().getUser().getEmail())
                        .name(application.getVolunteer().getUser().getName())
                        .profileImage(profileImageUrl)
                        .build())
                    .volunteer(ReadApplicationsResponseDto.Volunteer.builder()
                        .volunteerId(application.getVolunteer().getId())
                        .recommendedCount(application.getVolunteer().getRecommendedCount())
                        .totalVolunteerHours(
                        application.getVolunteer().getUser().getTotalVolunteerHours() != null
                                ? application.getVolunteer().getUser().getTotalVolunteerHours().intValue()
                                : 0
                        )
                        .build())
                    .application(ReadApplicationsResponseDto.Application.builder()
                        .applicationId(application.getId())
                        .recruitId(application.getRecruit().getId())
                        .status(application.getStatus())
                        .createdAt(application.getCreatedAt() != null
                                ? LocalDateTime.ofInstant(application.getCreatedAt(), ZoneId.systemDefault())
                                : LocalDateTime.now())
                        .build())
                    .build();
                })
                .collect(Collectors.toList());

        return ReadApplicationsResponseDto.builder()
                .recruitId(recruitId)
                .applications(applicationDetails)
                .build();
    }
//2
    @Override
    public UpdateApplicationsResponseDto updateStatuses(UpdateApplicationsRequestDto updateApplicationDto, AuthDto authDto) {

        checkOrgToken(authDto);

        Integer applicationId = updateApplicationDto.getApplicationId();
        Integer recruitId = updateApplicationDto.getRecruitId();
        Integer volunteerId = updateApplicationDto.getVolunteerId();
        Boolean accept = updateApplicationDto.getAccept();
        String status = accept ? "APPROVED" : "REJECTED";
        
        Optional<Recruit> recruit = recruitRepository.findByRecruitId(recruitId);
        
        if(recruit.isEmpty()) {
            //없는공고
            log.info("해당 공고 {} 를 찾을 수 없습니다", recruitId);
        }
        if(!recruit.get().getTemplate().getOrg().getId().equals(authDto.getUserId())){
            //권한없음
            log.info("해당 공고에 대한 권한이 없습니다");
        }
        Optional<Application> application = applicationRepository.findById(updateApplicationDto.getApplicationId());

        if(application.isEmpty()) {
            log.info("신청기록이 존재하지 않습니다");
        }
        if(!(application.get().getId()==updateApplicationDto.getApplicationId())){
            log.info("신청번호와과 동일하지 않습니다");
        }
        if(!(application.get().getVolunteer().getId()==updateApplicationDto.getVolunteerId())){
            log.info("신청자와 신청내역의 봉사자가 일치하지 않습니다");
        }
        application.get().updateStatus(status);

        return UpdateApplicationsResponseDto.builder()
                .message("신청 상태 변경 완료")
                .application(UpdateApplicationsResponseDto.Application.builder()
                        .applicationId(applicationId)
                        .recruitId(recruitId)
                        .status(status)
                        .createdAt(LocalDateTime.now())
                        .build())
                .build();
    }
//1
    @Override
    public List<EvaluateApplicationsResponseDto> evaluateApplicants(
            Integer recruitId,
            List<EvaluateApplicationsRequestDto> evaluateApplicationDtoList,
            AuthDto authDto) {

        checkOrgToken(authDto);
        /*
        Organization org = organizationRepository.findByUserId(authDto.getUserId())
                .orElseThrow(() -> new ForbiddenException("해당 기관을 찾을 수 없습니다."));

        Integer recruitOrgId = recruitRepository.findOrgIdByRecruitId(recruitId)
                .orElseThrow(() -> new NotFoundException("해당 모집 공고를 찾을 수 없습니다."));

        if (!org.getId().equals(recruitOrgId)) {
            throw new ForbiddenException("이 모집 공고의 신청자를 평가할 권한이 없습니다.");
        }
        */
        Map<Integer, Application> applicationMap = applicationRepository.findByRecruitIdMap(recruitId);

        return evaluateApplicationDtoList.stream().filter(evluateApplicationDto ->{
                return !applicationMap.get(evluateApplicationDto.getVolunteerId()).getEvaluationDone();
            }).map(evaluateApplicationDto ->{
            Application app = applicationMap.get(evaluateApplicationDto.getVolunteerId());
            app.setStatus(evaluateApplicationDto.getStatus());
            if(evaluateApplicationDto.getRecommendationStatus().equals("RECOMMEND")){
                app.getVolunteer().incrementRecommendedCount();
            }else if(evaluateApplicationDto.getRecommendationStatus().equals("NOTRECOMMEND")){
                app.getVolunteer().incrementNotRecommendedCount();
            }
            return EvaluateApplicationsResponseDto.builder()
                    .volunteerId(evaluateApplicationDto.getVolunteerId())
                    .recommendationStatus(evaluateApplicationDto.getRecommendationStatus())
                    .build();
        }).toList();

    }
    public int getMinutesToDeadlineEvent(Recruit recruit) {
        LocalDateTime recruitDeadline = recruit.getDeadline()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
                .plusDays(1)
                .truncatedTo(ChronoUnit.DAYS);
        LocalDateTime now = LocalDateTime.now();
        return (int) ChronoUnit.MINUTES.between(now, recruitDeadline);
    }
    public int getMinutesToCompleteEvent(Recruit recruit) {
        Date activityDate = recruit.getActivityDate();
        BigDecimal activityEnd = recruit.getActivityEnd();

        LocalDateTime activityDateTime = activityDate.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        LocalDateTime activityEndTime = activityDateTime
                .withHour(activityEnd.intValue())
                .withMinute(activityEnd.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).intValue())
                .withSecond(0);

        LocalDateTime now = LocalDateTime.now();

        return (int) ChronoUnit.MINUTES.between(now, activityEndTime);
    }
    private Instant localDateTimeToInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant();
    }
}
