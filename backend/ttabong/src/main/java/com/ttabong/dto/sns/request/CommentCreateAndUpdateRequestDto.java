package com.ttabong.dto.sns.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 9. 댓글 _ 작성
 * 10. 댓글 _ 수정
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateAndUpdateRequestDto {
    @NotBlank
    private String content;  // 댓글 내용 (필수)
}
