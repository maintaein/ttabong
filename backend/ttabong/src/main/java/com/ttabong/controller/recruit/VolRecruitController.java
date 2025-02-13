package com.ttabong.controller.recruit;

import com.ttabong.dto.recruit.requestDto.vol.ApplyRecruitRequestDto;
import com.ttabong.dto.recruit.requestDto.vol.DeleteLikesRequestDto;
import com.ttabong.dto.recruit.requestDto.vol.LikeOnRecruitRequestDto;
import com.ttabong.dto.recruit.responseDto.vol.*;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.entity.recruit.Application;
import com.ttabong.service.recruit.VolRecruitService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("vol")
public class VolRecruitController {

    private final VolRecruitService volRecruitService;

    @Autowired
    public VolRecruitController(VolRecruitService volRecruitService) {
        this.volRecruitService = volRecruitService;
    }

    // 1. 모집 공고 리스트 조회
    @GetMapping("/templates")
    public ResponseEntity<ReadVolRecruitsListResponseDto> listRecruits(
            @RequestParam(required = false) Integer cursor,
            @RequestParam Integer limit) {
        ReadVolRecruitsListResponseDto responseDto = volRecruitService.getTemplates(cursor, limit);
        return ResponseEntity.ok().body(responseDto);
    }

    // 2. 특정 모집 공고 상세 조회
    @GetMapping("/templates/{templateId}")
    public ResponseEntity<ReadRecruitDetailResponseDto> recruitsDetail(@PathVariable Integer templateId) {
        return volRecruitService.getTemplateById(templateId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. 모집 공고 신청
    @PostMapping("/applications")
    public ResponseEntity<ApplyRecruitResponseDto> applyRecruit(
            HttpServletRequest request,
            @RequestBody ApplyRecruitRequestDto applyRecruitRequest) {

        AuthDto authDto = (AuthDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = authDto.getUserId();

        Application application = volRecruitService.applyRecruit(userId, applyRecruitRequest.getRecruitId());

        ApplyRecruitResponseDto responseDto = ApplyRecruitResponseDto.builder()
                .message("신청 완료")
                .application(new ApplyRecruitResponseDto.Application(application.getId(), application.getStatus()))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 4. 공고 신청 취소
    @PatchMapping("/applications/{applicationId}")
    public ResponseEntity<CancelRecruitResponseDto> cancelRecruit(@PathVariable Integer applicationId) {
        Application application = volRecruitService.cancelRecruitApplication(applicationId);

        CancelRecruitResponseDto responseDto = CancelRecruitResponseDto.builder()
                .message("신청 취소 완료")
                .application(CancelRecruitResponseDto.ApplicationDto.fromEntity(application))
                .build();

        return ResponseEntity.ok().body(responseDto);
    }



    // 5. 신청한 공고 목록 조회
    @GetMapping("/applications/recruits")
    public ResponseEntity<List<MyApplicationsResponseDto>> myApplications(
            @RequestParam Integer cursor, @RequestParam Integer limit, HttpServletRequest request) {

        AuthDto authDto = (AuthDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = authDto.getUserId();

        List<MyApplicationsResponseDto> responseDto = volRecruitService.getMyApplications(userId, cursor, limit);
        return ResponseEntity.ok().body(responseDto);
    }

    // 6. 특정 공고 상세 조회
    @GetMapping("/recruits/{recruitId}")
    public ResponseEntity<MyApplicationDetailResponseDto> myApplicationsDetail(@PathVariable Integer recruitId) {
        return volRecruitService.getRecruitDetail(recruitId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 7. "좋아요"한 템플릿 목록 조회
    @GetMapping("/volunteer-reactions/likes")
    public ResponseEntity<List<MyLikesRecruitsResponseDto>> myLikesOnRecruits(
            @RequestParam Integer cursor, @RequestParam Integer limit) {
        AuthDto authDto = (AuthDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = authDto.getUserId();

        List<MyLikesRecruitsResponseDto> responseDto = volRecruitService.getLikedTemplates(userId, cursor, limit);
        return ResponseEntity.ok().body(responseDto);
    }

    // 8. 특정 템플릿 "좋아요" 혹은 "싫어요"하기
    @PostMapping("/volunteer-reactions")
    public ResponseEntity<LikeOnRecruitResponseDto> likeOnRecruit(@RequestBody LikeOnRecruitRequestDto request) {
        AuthDto authDto = (AuthDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = authDto.getUserId();

        Integer reactionId = volRecruitService.saveReaction(userId, request.getTemplateId(), request.getIsLike());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LikeOnRecruitResponseDto(reactionId, request.getIsLike()));
    }

    // 9. "좋아요" 취소
    @PatchMapping("/volunteer-reactions/cancel")
    public ResponseEntity<?> deleteRecruitFromLike(@RequestBody DeleteLikesRequestDto request) {
        volRecruitService.deleteReactions(request.getReactionIds());
        return ResponseEntity.noContent().build();
    }
}
