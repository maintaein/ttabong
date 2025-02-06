package com.ttabong.dto.sns.response;

import lombok.*;

/*
 * 11. 댓글 _ 삭제
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDeleteResponseDto {
    private String message;
    private Long commentId;
}
