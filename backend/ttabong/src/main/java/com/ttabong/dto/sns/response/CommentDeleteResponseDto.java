package com.ttabong.dto.sns.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
