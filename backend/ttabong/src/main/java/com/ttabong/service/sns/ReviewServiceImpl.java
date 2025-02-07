package com.ttabong.service.sns;

import com.ttabong.dto.sns.request.ReviewCreateRequestDto;
import com.ttabong.dto.sns.response.ReviewCreateResponseDto;
import com.ttabong.dto.sns.response.ReviewDeleteResponseDto;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.sns.Review;
import com.ttabong.entity.user.Organization;
import com.ttabong.entity.user.User;
import com.ttabong.repository.recruit.RecruitRepository;
import com.ttabong.repository.sns.ReviewRepository;
import com.ttabong.repository.user.OrganizationRepository;
import com.ttabong.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RecruitRepository recruitRepository;

    /* 후기 생성 */
    @Override
    public ReviewCreateResponseDto createReview(ReviewCreateRequestDto requestDto) {

        //필수값 입력 test
        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("내용 부분은 필수 입력 값입니다");
        }

        // 필수 리소스 체크
        Recruit recruit = recruitRepository.findById(requestDto.getRecruitId())
                .orElseThrow(() -> new EntityNotFoundException("해당 공고가 존재하지 않습니다."));
        Organization org = organizationRepository.findById(requestDto.getOrgId())
                .orElseThrow(() -> new EntityNotFoundException("해당 기관이 존재하지 않습니다."));
        User writer = userRepository.findById(requestDto.getWriterId())
                .orElseThrow(() -> new EntityNotFoundException("해당 작성자가 존재하지 않습니다."));

        Review review = Review.builder()
                .recruit(recruit)
                .org(org)
                .writer(writer)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .isPublic(requestDto.getIsPublic())
                .isDeleted(false)
                .thumbnailImg(null)
                .imgCount(requestDto.getImageCount())
                .createdAt(Instant.now())
                .build();

        review = reviewRepository.save(review);

        return new ReviewCreateResponseDto("후기 생성 성공", review.getId(), review.getWriter().getId());
    }

    /* 삭제 */
    @Transactional
    @Override
    public ReviewDeleteResponseDto deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 후기를 찾을 수 없습니다. ID: " + reviewId));

        Review updatedReview = review.toBuilder()
                .isDeleted(true)
                .build();

        reviewRepository.save(updatedReview);

        return new ReviewDeleteResponseDto("삭제 성공하였습니다.", updatedReview.getId(), updatedReview.getTitle(), updatedReview.getContent());
    }
}
