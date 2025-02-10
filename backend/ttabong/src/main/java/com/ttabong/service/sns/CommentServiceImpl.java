package com.ttabong.service.sns;

import com.ttabong.dto.sns.request.CommentCreateAndUpdateRequestDto;
import com.ttabong.dto.sns.response.CommentCreateAndUpdateResponseDto;
import com.ttabong.dto.sns.response.CommentDeleteResponseDto;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.entity.sns.Review;
import com.ttabong.entity.sns.ReviewComment;
import com.ttabong.entity.user.User;
import com.ttabong.repository.sns.CommentRepository;
import com.ttabong.repository.sns.ReviewCommentRepository;
import com.ttabong.repository.sns.ReviewRepository;
import com.ttabong.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final UserRepository userRepository;

    @Override
    public CommentCreateAndUpdateResponseDto createComment(AuthDto authDto,
                                                           Integer reviewId,
                                                           CommentCreateAndUpdateRequestDto requestDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("해당 후기를 찾을 수 없습니다. id: " + reviewId));

        User writer = userRepository.findById(authDto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. id: " + authDto.getUserId()));

        ReviewComment comment = ReviewComment.builder()
                .review(review)
                .writer(writer)
                .content(requestDto.getContent())
                .updatedAt(Instant.now())
                .createdAt(Instant.now())
                .build();

        commentRepository.save(comment);

        return CommentCreateAndUpdateResponseDto.builder()
                .commentId(comment.getId())
                .reviewId(review.getId())
                .writer(CommentCreateAndUpdateResponseDto.WriterDto.builder()
                        .writerId(writer.getId())
                        .writerName(writer.getName())
                        .writerProfileImage(writer.getProfileImage())
                        .build())
                .content(comment.getContent())
                .updatedAt(comment.getUpdatedAt().atZone(java.time.ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .build();
    }

    @Override
    public CommentCreateAndUpdateResponseDto updateComment(AuthDto authDto, Integer commentId, CommentCreateAndUpdateRequestDto requestDto) {

        ReviewComment existingComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!existingComment.getWriter().getId().equals(authDto.getUserId())) {
            throw new RuntimeException("댓글 수정 권한 없음");
        }

        existingComment.updateContent(requestDto.getContent());

        return CommentCreateAndUpdateResponseDto.builder()
                .commentId(existingComment.getId())
                .reviewId(existingComment.getReview().getId())
                .writer(CommentCreateAndUpdateResponseDto.WriterDto.builder()
                        .writerId(existingComment.getWriter().getId())
                        .writerName(existingComment.getWriter().getName())
                        .writerProfileImage(existingComment.getWriter().getProfileImage())
                        .build())
                .content(existingComment.getContent())
                .updatedAt(existingComment.getUpdatedAt().atZone(java.time.ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .build();
    }

    @Override
    public CommentDeleteResponseDto deleteComment(AuthDto authDto, Integer commentId) {

        ReviewComment existingComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));
        
        if (!existingComment.getWriter().getId().equals(authDto.getUserId())) {
            throw new RuntimeException("댓글 삭제 권한 없습니다");
        }
        
        existingComment.markDeleted();

        return CommentDeleteResponseDto.builder()
                .message("댓글 삭제 성공")
                .commentId(existingComment.getId())
                .build();
    }

}
