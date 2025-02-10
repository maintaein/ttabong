package com.ttabong.service.sns;

import com.ttabong.dto.sns.request.CommentCreateAndUpdateRequestDto;
import com.ttabong.dto.sns.response.CommentCreateAndUpdateResponseDto;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.entity.sns.Review;
import com.ttabong.entity.sns.ReviewComment;
import com.ttabong.entity.user.User;
import com.ttabong.repository.sns.CommentRepository;
import com.ttabong.repository.sns.ReviewRepository;
import com.ttabong.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public CommentCreateAndUpdateResponseDto createComment(AuthDto authDto,
                                                           Integer reviewId,
                                                           CommentCreateAndUpdateRequestDto requestDto) {
        // 1. 리뷰 존재 여부 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("해당 후기를 찾을 수 없습니다. id: " + reviewId));

        // 2. 현재 로그인한 사용자 정보 조회
        User writer = userRepository.findById(authDto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. id: " + authDto.getUserId()));

        // 3. 댓글 엔티티 생성 및 저장
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
}
