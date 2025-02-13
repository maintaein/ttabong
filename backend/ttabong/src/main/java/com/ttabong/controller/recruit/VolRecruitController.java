package com.ttabong.controller.recruit;

import com.ttabong.config.LoggerConfig;
import com.ttabong.dto.recruit.requestDto.vol.ApplyRecruitRequestDto;
import com.ttabong.dto.recruit.requestDto.vol.DeleteLikesRequestDto;
import com.ttabong.dto.recruit.requestDto.vol.LikeOnRecruitRequestDto;
import com.ttabong.dto.recruit.responseDto.vol.*;
import com.ttabong.entity.recruit.Application;
import com.ttabong.jwt.JwtProvider;
import com.ttabong.service.recruit.VolRecruitService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("vol")
public class VolRecruitController implements LoggerConfig {


    private final VolRecruitService volRecruitService;
    private final JwtProvider jwtProvider;

    @Autowired
    public VolRecruitController(VolRecruitService volRecruitService, JwtProvider jwtProvider) {
        this.volRecruitService = volRecruitService;
        this.jwtProvider = jwtProvider;
    }

    // 모집 공고 리스트 조회
    @GetMapping("/templates")
    public ResponseEntity<ReadVolRecruitsListResponseDto> listRecruits(
            @RequestParam(required = false) Integer cursor,
            @RequestParam Integer limit) {
        logger().info("1. 모집 공고 리스트 조회\"/templates\"");

        ReadVolRecruitsListResponseDto responseDto = volRecruitService.getTemplates(cursor, limit);
        return ResponseEntity.ok().body(responseDto);
    }


    // 특정 모집 공고 상세 조회
    @GetMapping("/templates/{templateId}")
    public ResponseEntity<ReadRecruitDetailResponseDto> recruitsDetail(@PathVariable Integer templateId) {
        logger().info("2. 특정 모집 공고 상세 조회\"//templates/{templateId}\"");
        return volRecruitService.getTemplateById(templateId)
                .map(ResponseEntity::ok) // ✅ 성공 시 DTO를 감싼 ResponseEntity 반환
                .orElse(ResponseEntity.notFound().build()); // ✅ 존재하지 않으면 404 반환
    }


    // 모집 공고 신청
    @PostMapping("/applications")
    public ResponseEntity<ApplyRecruitResponseDto> applyRecruit(
            HttpServletRequest request,
            @RequestBody ApplyRecruitRequestDto applyRecruitRequest) {
        logger().info("3. 모집 공고 신청\"/applications\"");
        int userId = extractUserIdFromRequest(request);

        Application application = volRecruitService.applyRecruit(userId, applyRecruitRequest.getRecruitId());

        ApplyRecruitResponseDto responseDto = ApplyRecruitResponseDto.builder()
                .message("신청 완료")
                .application(ApplyRecruitResponseDto.Application.builder()
                        .applicationId(application.getId())
                        .status(application.getStatus())
                        .build())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // JWT에서 userId 추출하는 메서드
    private int extractUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Claims claims = jwtProvider.getClaims(token);
        return Integer.parseInt(claims.getSubject());
    }


    //공고 신청 취소
    @PatchMapping("/applications/{applicationsId}")
    public ResponseEntity<CancelRecruitResponseDto> cancelRecruit(@PathVariable String applicationsId) {
        logger().info("4. 공고 신청 취소\"//applications/{applicationsId}\"");

        CancelRecruitResponseDto.Application application = CancelRecruitResponseDto.Application.builder()
                .applicationId(Integer.parseInt(applicationsId))
                .isDeleted(true)
                .build();

        CancelRecruitResponseDto responseDto = CancelRecruitResponseDto.builder()
                .message("신청 취소 완료")
                .application(application)
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //신청한 공고 목록 조회
    @GetMapping("/applications/recruits")
    public ResponseEntity<MyApplicationsResponseDto> myApplications(@RequestParam Integer cursor, @RequestParam Integer limit) {
        logger().info("5. 신청한 공고 목록 조회\"/applications/recruits\"");

        MyApplicationsResponseDto responseDto = MyApplicationsResponseDto.builder()
                .applicationId(55)
                .status("PENDING")
                .evaluationDone(false)
                .createdAt(LocalDateTime.now())
                .template(MyApplicationsResponseDto.Template.builder()
                        .templateId(1)
                        .title("환경 보호 봉사")
                        .activityLocation("서울특별시 강남구 테헤란로 123")
                        .status("ALL")
                        .imageId("https://example.com/image.jpg")
                        .contactName("김봉사")
                        .contactPhone("010-1234-5678")
                        .description("환경 보호를 위한 봉사활동입니다.")
                        .createdAt(LocalDateTime.now())
                        .build())
                .group(MyApplicationsResponseDto.Group.builder()
                        .groupId(10)
                        .groupName("환경 봉사팀")
                        .build())
                .recruit(MyApplicationsResponseDto.Recruit.builder()
                        .recruitId(101)
                        .deadline(LocalDateTime.now().plusDays(7))
                        .activityDate(LocalDate.now().plusDays(10))
                        .activityStart(10.00)
                        .activityEnd(14.00)
                        .maxVolunteer(20)
                        .participateVolCount(15)
                        .status("RECRUITING")
                        .createdAt(LocalDateTime.now())
                        .build())
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    // 특정공고 상세 조회
    @GetMapping("/recruits/{recruitId}")
    public ResponseEntity<MyApplicationDetailResponseDto> myApplicationsDetail(@PathVariable Integer recruitId) {
        logger().info("6. 특정공고 상세 조회\"/recruits/{recruitId}\"");
        MyApplicationDetailResponseDto responseDto = MyApplicationDetailResponseDto.builder()
                .group(MyApplicationDetailResponseDto.Group.builder()
                        .groupId(10)
                        .groupName("청소년 봉사단")
                        .build())
                .template(MyApplicationDetailResponseDto.Template.builder()
                        .templateId(1)
                        .categoryId(3)
                        .title("환경 정화 봉사")
                        .activityLocation("서울특별시 종로구")
                        .status("ACTIVE")
                        .imageId("https://example.com/image.jpg")
                        .contactName("김봉사")
                        .contactPhone("010-1234-5678")
                        .description("서울 시내 공원에서 환경 정화 활동")
                        .createdAt(LocalDateTime.now())
                        .build())
                .recruit(MyApplicationDetailResponseDto.Recruit.builder()
                        .recruitId(101)
                        .deadline(LocalDateTime.now().plusDays(7))
                        .activityDate(LocalDate.now().plusDays(10))
                        .activityStart(10)
                        .activityEnd(14)
                        .maxVolunteer(20)
                        .participateVolCount(15)
                        .status("RECRUITING")
                        .updatedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build())
                .organization(MyApplicationDetailResponseDto.Organization.builder()
                        .orgId(5)
                        .name("서울 봉사 센터")
                        .build())
                .application(MyApplicationDetailResponseDto.Application.builder()
                        .applicationId(55)
                        .name("봉사 신청")
                        .status("PENDING")
                        .build())
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //"좋아요"한 템플릿 목록 조회
    @GetMapping("/volunteer-reactions/likes")
    public ResponseEntity<MyLikesRecruitsResponseDto> myLikesOnRecruits(@RequestParam Integer cursor, @RequestParam Integer limit) {
        logger().info("7. \"좋아요\"한 템플릿 목록 조회\"/volunteer-reactions/likes\"");

        MyLikesRecruitsResponseDto responseDto = MyLikesRecruitsResponseDto.builder()
                .likedTemplates(List.of(
                        MyLikesRecruitsResponseDto.LikedTemplate.builder()
                                .templateId(1)
                                .thumbnailImg("https://example.com/template_image.jpg")
                                .activityLocation("서울특별시 종로구")
                                .title("환경 보호 봉사")
                                .recruit(MyLikesRecruitsResponseDto.Recruit.builder()
                                        .deadline(LocalDateTime.now().plusDays(5))
                                        .build())
                                .group(MyLikesRecruitsResponseDto.Group.builder()
                                        .groupId(10)
                                        .groupName("환경 봉사팀")
                                        .build())
                                .build()
                ))
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    // 특정 템플릿 "좋아요" 혹은 "싫어요"하기
    @PostMapping("/volunteer_reactions")
    public ResponseEntity<LikeOnRecruitResponseDto> likeOnRecruit(@RequestBody LikeOnRecruitRequestDto likeOnRecruitRequest) {
        logger().info("8. 특정 템플릿 \"좋아요\" 혹은 \"싫어요\"하기\"volunteer_reactions\"");
        LikeOnRecruitResponseDto responseDto = LikeOnRecruitResponseDto.builder()
                .relationId(1001) // 임시 ID, 실제 데이터베이스 값이 필요함
                .isLike(likeOnRecruitRequest.getIsLike())
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    // "좋아요"목록에서 특정 템플릿 "좋아요"취소 ("좋아요"삭제)
    @PatchMapping("/volunteer_reactions/cancel")
    public ResponseEntity<?> deleteRecruitFromLike(@RequestBody DeleteLikesRequestDto deleteLikeRequest) {
        logger().info("9. 특 \"좋아요\"목록에서 특정 템플릿 \"좋아요\"취소 \"/volunteer_reactions/cancel\"");
        return ResponseEntity.ok().body(
                Map.of("message", "좋아요 삭제 완료", "deletedReactionIds", deleteLikeRequest.getReactionIds())
        );
    }

}