package com.ttabong.controller.recruit;

import com.ttabong.dto.recruit.requestDto.vol.ApplyRecruitRequestDto;
import com.ttabong.dto.recruit.requestDto.vol.DeleteLikesRequestDto;
import com.ttabong.dto.recruit.requestDto.vol.LikeOnRecruitRequestDto;
import com.ttabong.dto.recruit.responseDto.vol.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("vol")
public class VolRecruitController {

    @GetMapping("/templates")
    public ResponseEntity<ReadRecruitsResponseDto> listRecruits(@RequestParam Integer cursor, @RequestParam Integer limit) {
        ReadRecruitsResponseDto responseDto = ReadRecruitsResponseDto.builder()
                .templates(List.of(
                        ReadRecruitsResponseDto.TemplateDetail.builder()
                                .template(ReadRecruitsResponseDto.Template.builder()
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
                                        .build()
                                )
                                .group(ReadRecruitsResponseDto.Group.builder()
                                        .groupId(10)
                                        .groupName("청소년 봉사단")
                                        .build()
                                )
                                .organization(ReadRecruitsResponseDto.Organization.builder()
                                        .orgId(5)
                                        .orgName("서울 봉사 센터")
                                        .build()
                                )
                                .build()
                ))
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/templates/{templateId}")
    public ResponseEntity<ReadRecruitDetailResponseDto> recruitsDetail(@PathVariable String templateId) {
        ReadRecruitDetailResponseDto responseDto = new ReadRecruitDetailResponseDto();
        return ResponseEntity.ok().body(responseDto);
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplyRecruitResponseDto> applyRecruit(@RequestBody ApplyRecruitRequestDto applyRecruitDto) {
        ApplyRecruitResponseDto.Application application = ApplyRecruitResponseDto.Application.builder()
                .applicationId(1)
                .status("PENDING")
                .build();

        ApplyRecruitResponseDto responseDto = ApplyRecruitResponseDto.builder()
                .message("신청 완료")
                .application(application)
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    @PatchMapping("/applications/{applicationsId}")
    public ResponseEntity<CancelRecruitResponseDto> cancelRecruit(@PathVariable String applicationsId) {
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

    @GetMapping("/applications/recruits")
    public ResponseEntity<MyApplicationsResponseDto> myApplications(@RequestParam Integer cursor, @RequestParam Integer limit) {
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


    @GetMapping("/recruits/{recruitId}")
    public ResponseEntity<MyApplicationDetailResponseDto> myApplicationsDetail(@PathVariable Integer recruitId) {
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


    @GetMapping("/volunteer-reactions/likes")
    public ResponseEntity<MyLikesRecruitsResponseDto> myLikesOnRecruits(@RequestParam Integer cursor, @RequestParam Integer limit) {
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


    @PostMapping("/volunteer_reactions")
    public ResponseEntity<LikeOnRecruitResponseDto> likeOnRecruit(@RequestBody LikeOnRecruitRequestDto likeOnRecruitRequest) {
        LikeOnRecruitResponseDto responseDto = LikeOnRecruitResponseDto.builder()
                .relationId(1001) // 임시 ID, 실제 데이터베이스 값이 필요함
                .isLike(likeOnRecruitRequest.getIsLike())
                .build();

        return ResponseEntity.ok().body(responseDto);
    }


    @PatchMapping("/volunteer_reactions/cancel")
    public ResponseEntity<?> deleteRecruitFromLike(@RequestBody DeleteLikesRequestDto deleteLikeRequest) {
        return ResponseEntity.ok().body(
                Map.of("message", "좋아요 삭제 완료", "deletedReactionIds", deleteLikeRequest.getReactionIds())
        );
    }

}