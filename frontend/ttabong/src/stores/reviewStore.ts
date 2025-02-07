import { create } from 'zustand';
import type { Review, ReviewDetail } from '@/types/reviewType';
import { reviewApi } from '@/api/reviewApi';

interface ReviewStore {
  reviews: Review[];
  reviewDetail: ReviewDetail | null;
  isLoading: boolean;
  error: string | null;
  hasMore: boolean;
  fetchReviews: (cursor: number) => Promise<void>;
  fetchReviewDetail: (id: number) => Promise<void>;
  addComment: (reviewId: number, content: string) => Promise<void>;
  recruitReviews: Review[];
  fetchRecruitReviews: (recruitId: number) => Promise<void>;
  deleteReview: (reviewId: number) => Promise<void>;
  toggleReviewPublic: (reviewId: number, isPublic: boolean) => Promise<void>;
  updateComment: (commentId: number, content: string) => Promise<void>;
  deleteComment: (commentId: number) => Promise<void>;
}

export const useReviewStore = create<ReviewStore>((set) => ({
  reviews: [],
  reviewDetail: null,
  isLoading: false,
  error: null,
  hasMore: true,
  recruitReviews: [],
  
  fetchReviews: async (cursor: number) => {
    const LIMIT = 9;
    
    set(state => ({
      isLoading: true,
      error: null,
      // 커서가 0이면 초기 로딩이므로 기존 reviews 초기화
      reviews: cursor === 0 ? [] : state.reviews
    }));

    try {
      const response = await reviewApi.getReviews(cursor, LIMIT);
      
      set(state => ({
        reviews: [...state.reviews, ...response.reviews],
        hasMore: response.reviews.length === LIMIT
      }));
    } catch (error) {
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
      set({ error: '댓글 작성에 실패했습니다.' });
    }
  },

  fetchRecruitReviews: async (recruitId: number) => {
    set({ isLoading: true });
    try {
      const response = await reviewApi.getRecruitReviews(recruitId);
      set({ recruitReviews: response, error: null });
    } catch (error) {
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

  toggleReviewPublic: async (reviewId: number, isPublic: boolean) => {
    try {
      await reviewApi.toggleReviewPublic(reviewId, isPublic);
      set((state) => ({
        reviewDetail: state.reviewDetail ? {
          ...state.reviewDetail,
          isPublic: !isPublic
        } : null
      }));
    } catch (error) {
      set({ error: '후기 공개 상태 변경에 실패했습니다.' });
    }
  },

  updateComment: async (commentId: number, content: string) => {
    try {
      const updatedComment = await reviewApi.updateComment(commentId, content);
      set((state) => ({
        reviewDetail: state.reviewDetail ? {
          ...state.reviewDetail,
          comments: state.reviewDetail.comments.map(comment =>
            comment.commentId === commentId ? updatedComment : comment
          )
        } : null
      }));
    } catch (error) {
      set({ error: '댓글 수정에 실패했습니다.' });
    }
  },

  deleteComment: async (commentId: number) => {
    try {
      await reviewApi.deleteComment(commentId);
      set((state) => ({
        reviewDetail: state.reviewDetail ? {
          ...state.reviewDetail,
          comments: state.reviewDetail.comments.filter(
            comment => comment.commentId !== commentId
          )
        } : null
      }));
    } catch (error) {
      set({ error: '댓글 삭제에 실패했습니다.' });
    }
  },
})); 