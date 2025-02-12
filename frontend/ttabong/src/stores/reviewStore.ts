import { create } from 'zustand';
import type { Review, ReviewDetail } from '@/types/reviewType';
import { reviewApi } from '@/api/reviewApi';

interface ReviewStore {
  reviews: Review[];
  reviewDetail: ReviewDetail | null;
  isLoading: boolean;
  error: string | null;
  fetchReviews: () => Promise<void>;
  fetchReviewDetail: (id: number) => Promise<void>;
  addComment: (reviewId: number, content: string) => Promise<void>;
  recruitReviews: Review[];
  fetchRecruitReviews: (recruitId: number) => Promise<void>;
  deleteReview: (reviewId: number) => Promise<void>;
}

export const useReviewStore = create<ReviewStore>((set) => ({
  reviews: [],
  reviewDetail: null,
  isLoading: false,
  error: null,
  recruitReviews: [],
  
  fetchReviews: async () => {
    set({ isLoading: true });
    try {
      const reviews = await reviewApi.getReviews();
      set({ reviews, error: null });
    } catch (error) {
      console.error('리뷰 불러오기 실패:', error);
      set({ error: '리뷰를 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }

  },

  fetchReviewDetail: async (id: number) => {
    set({ isLoading: true });
    try {
      const reviewDetail = await reviewApi.getReviewDetail(id);
      set({ reviewDetail, error: null });
    } catch (error) {
      console.error('리뷰 상세 정보 불러오기 실패:', error);
      set({ error: '리뷰 상세 정보를 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }

  },

  addComment: async (reviewId: number, content: string) => {
    try {
      const newComment = await reviewApi.addComment(reviewId, content);
      set((state) => ({
        reviewDetail: state.reviewDetail ? {
          ...state.reviewDetail,
          comments: [...state.reviewDetail.comments, newComment]
        } : null
      }));
    } catch (error) {
      console.error('댓글 작성 실패:', error);
      set({ error: '댓글 작성에 실패했습니다.' });
    }
  },


  fetchRecruitReviews: async (recruitId: number) => {
    set({ isLoading: true });
    try {
      const response = await reviewApi.getRecruitReviews(recruitId);
      set({ recruitReviews: response, error: null });
    } catch (error) {
      console.error('리뷰 목록 불러오기 실패:', error);
      set({ error: '리뷰 목록을 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }

  },

  deleteReview: async (reviewId: number) => {
    try {
      await reviewApi.deleteReview(reviewId);
      set((state) => ({
        reviews: state.reviews.filter(review => review.review.reviewId !== reviewId)
      }));
    } catch (error) {
      console.error('리뷰 삭제 실패:', error);
      throw error;
    }
  },
})); 