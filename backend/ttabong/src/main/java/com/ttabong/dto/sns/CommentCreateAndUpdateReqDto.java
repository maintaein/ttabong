package com.ttabong.dto.sns;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

/*
 * 9. 댓글 _ 작성
 * 10. 댓글 _ 수정
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateAndUpdateReqDto {
    @NotBlank
    private String content;  // 댓글 내용 (필수)
}
