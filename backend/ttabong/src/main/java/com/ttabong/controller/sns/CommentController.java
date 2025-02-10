package com.ttabong.controller.sns;

import com.ttabong.dto.sns.request.CommentCreateAndUpdateRequestDto;
import com.ttabong.dto.sns.response.CommentCreateAndUpdateResponseDto;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.service.sns.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 9. 댓글 작성
    @PostMapping("/{reviewId}")
    public ResponseEntity<CommentCreateAndUpdateResponseDto> createComment(
            @AuthenticationPrincipal AuthDto authDto,
            @PathVariable(name = "reviewId") Integer reviewId,
            @RequestBody @Valid CommentCreateAndUpdateRequestDto requestDto) {

        CommentCreateAndUpdateResponseDto response = commentService.createComment(authDto, reviewId, requestDto);
        return ResponseEntity.ok(response);
    }

    // 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentCreateAndUpdateResponseDto> updateComment(
            @AuthenticationPrincipal AuthDto authDto,
            @PathVariable Integer commentId,
            @RequestBody @Valid CommentCreateAndUpdateRequestDto requestDto) {

        CommentCreateAndUpdateResponseDto response = commentService.updateComment(authDto, commentId, requestDto);
        return ResponseEntity.ok(response);
    }



}


/*
//http://localhost:8080/api/reviews/2/comments
//http://localhost:8080/api/reviews/comments/1
//http://localhost:8080/api/reviews/comments/1/delete


댓글 _ 작성	POST	/api/reviews/{reviewId}/comments
댓글 _ 수정	PATCH	/api/reviews/comments/{commentId}
댓글 _ 삭제	PATCH	/api/reviews/comments/{commentId}/delete
후기 _ 이미지 업로드하기	PUT	{presignedUrl}


 */