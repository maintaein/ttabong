package com.ttabong.dto.sns.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/*
 * 11. 댓글 _ 삭제
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDeleteResponseDto {
    private String message;
    private Integer commentId;
}
