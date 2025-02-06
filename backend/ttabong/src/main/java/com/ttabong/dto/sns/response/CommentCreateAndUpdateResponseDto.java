package com.ttabong.dto.sns.response;

import lombok.*;

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
    private Long commentId;
    private Long reviewId;

    // 작성자 정보
    private Long writerId;
    private String writerName;
    private String writerProfileImage;

    private String content;
    private LocalDateTime updatedAt;
}
