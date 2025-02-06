package com.ttabong.controller.recruit;

import com.ttabong.dto.recruit.requestDto.vol.likeOnRecruitRequestDto;
import com.ttabong.dto.recruit.requestDto.vol.ApplyRecruitRequestDto;
import com.ttabong.dto.recruit.requestDto.vol.deleteLikesRequestDto;
import com.ttabong.dto.recruit.responseDto.vol.likeOnRecruitResponseDto;
import com.ttabong.dto.recruit.responseDto.vol.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("vol")
public class VolRecruitController {

    @GetMapping("templates")
    public ResponseEntity<ReadRecruitsResponseDto> listRecruits(@RequestParam Integer cursor, @RequestParam Integer limit) {
        return ResponseEntity.ok().body(new ReadRecruitsResponseDto());
    }

    @GetMapping("templates/{templateId}")
    public ResponseEntity<ReadRecruitDetailResponseDto> RecruitsDetail(@PathVariable String templateId) {
        return ResponseEntity.ok().body(new ReadRecruitDetailResponseDto());
    }

    @PostMapping("applications")
    public ResponseEntity<ApplyRecruitResponseDto> applyRecruit(@RequestBody ApplyRecruitRequestDto applyRecruitDto) {
        return ResponseEntity.ok().body(new ApplyRecruitResponseDto());
    }

    @PatchMapping("applications/{applicationsId}")
    public ResponseEntity<CancelRecruitResponseDto> cancelRecruit(@PathVariable String applicationsId) {
        return ResponseEntity.ok().body(new CancelRecruitResponseDto());
    }

    @GetMapping("applications/recruits")
    public ResponseEntity<MyApplicationsResponseDto> myApplications(@RequestParam Integer cursor, @RequestParam Integer limit) {
        return ResponseEntity.ok().body(new MyApplicationsResponseDto());
    }

    @GetMapping("recruits/{recruitId}")
    public ResponseEntity<MyApplicationDetailResponseDto> myApplicationsDetail(@PathVariable String recruitId) {
        return ResponseEntity.ok().body(new MyApplicationDetailResponseDto());

    }

    @GetMapping("volunteer-reactions/like")
    public ResponseEntity<MyLikesRecruitsResponseDto> myLikesOnRecruits(@RequestParam Integer cursor, @RequestParam Integer limit) {
        return ResponseEntity.ok().body(new MyLikesRecruitsResponseDto());
    }

    @PostMapping("volunteer_reactions")
    public ResponseEntity<likeOnRecruitResponseDto> likeOnRecruit(@RequestBody likeOnRecruitRequestDto likeOnRecruitRequest) {
        return ResponseEntity.ok().body(new likeOnRecruitResponseDto());
    }

    @PatchMapping("volunteer_reactions/cancel")
    public ResponseEntity<?> deleteRecruitFromLike(@RequestBody deleteLikesRequestDto deleteLikeRequest) {
        return ResponseEntity.noContent().build();
    }
}