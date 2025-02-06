package com.ttabong.dto.sns;

import lombok.*;

/*
 * 11. 댓글 _ 삭제
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDeleteResDto {
    private String message;
    private Long commentId;
}
