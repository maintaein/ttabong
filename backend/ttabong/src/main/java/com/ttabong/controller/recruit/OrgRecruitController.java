package com.ttabong.controller.recruit;

import com.ttabong.config.LoggerConfig;
import com.ttabong.dto.recruit.requestDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.*;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.service.recruit.OrgRecruitService;
import com.ttabong.util.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/org")
@RequiredArgsConstructor
@Slf4j
public class OrgRecruitController extends LoggerConfig {
    private final OrgRecruitService orgRecruitService;
    private final CacheService cacheService;

    //1. 메인페이지
    @GetMapping("/templates/available")
    public ResponseEntity<ReadAvailableRecruitsResponseDto> readAvailableRecruits(
            @RequestParam(required = false, name = "templateId") Integer cursor,
            @RequestParam(defaultValue = "10", name = "limit") Integer limit,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("1. 메인페이지, <GET> /templates/available");
        ReadAvailableRecruitsResponseDto response = orgRecruitService.readAvailableRecruits(cursor, limit, authDto);

        return ResponseEntity.ok().body(response);
    }

    //2. 공고 _ 공고 전체 조회(그룹+템플릿+공고)
    @GetMapping("/recruits")
    public ResponseEntity<ReadMyRecruitsResponseDto> readRecruits(
            @RequestParam(required = false, name = "recruitId") Integer cursor,
            @RequestParam(defaultValue = "10", name = "limit") Integer limit,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("2. 공고_공고 전체 조회,<GET> /recruits");
        ReadMyRecruitsResponseDto response = orgRecruitService.readMyRecruits(cursor, limit, authDto);

        return ResponseEntity.ok(response);
    }

    //3. 공고 _ 공고 삭제 (여러개 선택 후 한 번에 삭제)
    @PatchMapping("/recruits/delete")
    public ResponseEntity<DeleteRecruitsResponseDto> deleteRecruits(
            @RequestBody DeleteRecruitsRequestDto deleteRecruitDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("3. 공고_공고 삭제,<PATCH> /recruits/delete");
        DeleteRecruitsResponseDto response = orgRecruitService.deleteRecruits(deleteRecruitDto, authDto);

        return ResponseEntity.ok(response);
    }

    //4. 공고 _ 공고 수정
    @PatchMapping("/recruits/{recruitId}")
    public ResponseEntity<UpdateRecruitsResponseDto> updateRecruit(
            @PathVariable(name = "recruitId") Integer recruitId,
            @RequestBody UpdateRecruitsRequestDto requestDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("4. 공고_공고 수정,<PATCH> /recruits/{recruitId}");
        UpdateRecruitsResponseDto response = orgRecruitService.updateRecruit(recruitId, requestDto, authDto);

        return ResponseEntity.ok(response);
    }

    //5 공고 _ 공고 마감
    @PatchMapping("/recruits/close")
    public ResponseEntity<CloseRecruitResponseDto> closeRecruit(
            @RequestBody CloseRecruitRequestDto closeRecruitDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("5. 공고_공고 마감,<PATCH> /recruits/close");
        CloseRecruitResponseDto response = orgRecruitService.closeRecruit(closeRecruitDto, authDto);

        return ResponseEntity.ok().body(response);
    }

    // 6.공고 _ 그룹명 수정
    @PatchMapping("/groups")
    public ResponseEntity<UpdateGroupResponseDto> updateGroup(
            @RequestBody UpdateGroupRequestDto updateGroupDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("6. 공고_그룹명 수정,<PATCH> /groups");
        UpdateGroupResponseDto response = orgRecruitService.updateGroup(updateGroupDto, authDto);

        return ResponseEntity.ok().body(response);
    }

    //7 공고 _ 템플릿 수정
    @PatchMapping("/templates")
    public ResponseEntity<UpdateTemplateResponse> updateTemplate(
            @RequestBody UpdateTemplateRequestDto updateTemplateDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("7. 공고_템플릿 수정,<PATCH> /templates");
        UpdateTemplateResponse response = orgRecruitService.updateTemplate(updateTemplateDto, authDto);

        return ResponseEntity.ok().body(response);
    }

    //8. 공고 _ 템플릿 삭제 (여러개 선택 후 한 번에 삭제)
    @PatchMapping("/templates/delete")
    public ResponseEntity<DeleteTemplatesResponseDto> deleteTemplates(
            @RequestBody DeleteTemplatesRequestDto deleteTemplatesDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("8. 공고_템플릿 삭제,<PATCH> /templates/delete");
        DeleteTemplatesResponseDto response = orgRecruitService.deleteTemplates(deleteTemplatesDto, authDto);

        return ResponseEntity.ok().body(response);
    }

    //9. 공고 _ 그룹 삭제
    @PatchMapping("/groups/delete")
    public ResponseEntity<DeleteGroupResponseDto> deleteGroup(
            @RequestBody DeleteGroupDto deleteGroupDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("9. 공고_그룹 삭제,<PATCH> /groups/delete");
        DeleteGroupResponseDto response = orgRecruitService.deleteGroup(deleteGroupDto, authDto);

        return ResponseEntity.ok().body(response);
    }


    //10 공고 _ 그룹+템플릿 조회
    @GetMapping("templates")
    public ResponseEntity<ReadTemplatesResponseDto> readTemplates(
            @RequestParam(defaultValue = "0") int cursor,
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("10 공고 _ 그룹+템플릿 조회,<GET> /templates");
        ReadTemplatesResponseDto responseDto = orgRecruitService.readTemplates(cursor, limit, authDto);

        return ResponseEntity.ok().body(responseDto);
    }

    //11 공고 _ 템플릿 생성
    @PostMapping(value = "/templates", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateTemplateResponseDto> createTemplate(
            @RequestBody CreateTemplateRequestDto createTemplateDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("11 공고 _ 템플릿 생성,<POST> /templates");
        CreateTemplateResponseDto response = orgRecruitService.createTemplate(createTemplateDto, authDto);

        return ResponseEntity.ok().body(response);
    }

    //11-1 minio Presigned URL 발급 API
    @GetMapping("/templates/presigned")
    public ResponseEntity<CreateTemplateResponseDto> generatePresignedUrls(@AuthenticationPrincipal AuthDto authDto) throws Exception {
        logger.info("11 미니오 프리사인드 URL발급,<GET> /templates/presigned");

        List<String> presignedUrls = cacheService.generatePresignedUrlsForTemplate(authDto);

        CreateTemplateResponseDto response = CreateTemplateResponseDto.builder()
                .message("Presigned URL 생성 완료")
                .images(presignedUrls)
                .build();

        return ResponseEntity.ok().body(response);
    }

    //12. 공고 _ 그룹 생성
    @PostMapping("/groups")
    public ResponseEntity<CreateGroupResponseDto> createGroup(
            @RequestBody CreateGroupRequestDto createGroupDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("12 공고_그룹 생성,<POST> /groups");
        CreateGroupResponseDto response = orgRecruitService.createGroup(createGroupDto, authDto);

        return ResponseEntity.ok().body(response);
    }

    //13. 공고 _ 공고 생성
    @PostMapping("/recruits")
    public ResponseEntity<CreateRecruitResponseDto> createRecruit(
            @RequestBody CreateRecruitRequestDto createRecruitDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("13 공고_공고 생성,<POST> /recruits");
        CreateRecruitResponseDto response = orgRecruitService.createRecruit(createRecruitDto, authDto);

        return ResponseEntity.ok().body(response);
    }

    //14. 공고_상세조회
    @GetMapping("/recruits/{recruitId}")
    public ResponseEntity<ReadRecruitResponseDto> readRecruit(
            @PathVariable(name = "recruitId") Integer recruitId,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("14 공고_상세조회,<GET> /recruits/{recruitId}");
        ReadRecruitResponseDto response = orgRecruitService.readRecruit(recruitId, authDto);

        return ResponseEntity.ok().body(response);
    }

    //15. 공고, 봉사자 관리 _ 개별공고랑 관련된 지원자 조회
    @GetMapping("/recruits/{recruitId}/applications")
    public ResponseEntity<ReadApplicationsResponseDto> readApplications(
            @PathVariable(name = "recruitId") Integer recruitId,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("15 공고, 봉사자 관리,<GET> /recruits/{recruitId}/applications");
        ReadApplicationsResponseDto response = orgRecruitService.readApplications(recruitId, authDto);

        return ResponseEntity.ok().body(response);
    }

    //16. 봉사자 관리 _ 봉사자 수락/거절
    @PatchMapping("/applications/status")
    public ResponseEntity<UpdateApplicationsResponseDto> updateStatuses(
            @RequestBody UpdateApplicationsRequestDto updateApplicationDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("16 봉사자 관리, 봉사자 수락/거절, <PATCH> /applications/status");
        UpdateApplicationsResponseDto response = orgRecruitService.updateStatuses(updateApplicationDto, authDto);

        return ResponseEntity.ok().body(response);
    }

    //17. 봉사자 관리 _ 봉사자 평가
    @PatchMapping("/recruits/{recruitId}/applications/evaluate")
    public ResponseEntity<List<EvaluateApplicationsResponseDto>> evaluateApplicants(
            @PathVariable(name = "recruitId") Integer recruitId,
            @RequestBody List<EvaluateApplicationsRequestDto> evaluateApplicationDtoList,
            @AuthenticationPrincipal AuthDto authDto) {
        logger.info("17. 봉사자 관리 _ 봉사자 평가, <PATCH> /recruits/{recruitId}/applications/evaluate");
        List<EvaluateApplicationsResponseDto> response = orgRecruitService.evaluateApplicants(recruitId, evaluateApplicationDtoList, authDto);

        return ResponseEntity.ok().body(response);
    }
}
