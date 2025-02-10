package com.ttabong.dto.sns.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * 9. 댓글 _ 작성
 * 10. 댓글 _ 수정
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateAndUpdateResponseDto {
    private Integer commentId;
    private Integer reviewId;

    // 작성자 정보
    private Integer writerId;
    private String writerName;
    private String writerProfileImage;

    private String content;
    private LocalDateTime updatedAt;
}
