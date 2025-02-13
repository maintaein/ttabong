import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import type { Review, ReviewDetail, UpdateReviewRequest, ReviewEditResponse } from '@/types/reviewType';
import { reviewApi } from '@/api/reviewApi';

interface ReviewStore {
  reviews: Review[];
  reviewDetail: ReviewDetail | null;
  isLoading: boolean;
  error: string | null;
  recruitReviews: Review[];
  recruitId: number | null;
  orgId: number | null;
  fetchReviews: () => Promise<void>;
  fetchReviewDetail: (id: number) => Promise<void>;
  addComment: (reviewId: number, content: string) => Promise<void>;
  fetchRecruitReviews: (recruitId: number) => Promise<void>;
  deleteReview: (reviewId: number) => Promise<void>;
  updateReview: (reviewId: number, data: UpdateReviewRequest) => Promise<ReviewEditResponse>;
  setReviewInfo: (recruitId: number, orgId: number) => void;
  resetReviewInfo: () => void;
  updateVisibility: (reviewId: number, isPublic: boolean) => Promise<void>;
  updateComment: (commentId: number, content: string) => Promise<void>;
  deleteComment: (commentId: number) => Promise<void>;
}

export const useReviewStore = create<ReviewStore>()(
  persist(
    (set) => ({
      reviews: [],
      reviewDetail: null,
      isLoading: false,
      error: null,
      recruitReviews: [],
      recruitId: null,
      orgId: null,

      setReviewInfo: (recruitId, orgId) => set({ recruitId, orgId }),
      resetReviewInfo: () => set({ recruitId: null, orgId: null }),

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

      updateReview: async (reviewId: number, data: UpdateReviewRequest) => {
        try {
          const response = await reviewApi.updateReview(reviewId, data);
          set((state) => ({
            reviews: state.reviews.map(review => 
              review.review.reviewId === reviewId 
                ? { 
                    ...review,
                    review: { 
                      ...review.review,
                      title: data.title,
                      content: data.content
                    },
                    images: data.images
                  }
                : review
            ),
            reviewDetail: state.reviewDetail?.reviewId === reviewId 
              ? {
                  ...state.reviewDetail,
                  title: data.title,
                  content: data.content,
                  images: data.images,
                  isPublic: data.isPublic
                }
              : state.reviewDetail
          }));
          return response;
        } catch (error) {
          console.error('리뷰 수정 실패:', error);
          throw error;
        }
      },

      updateVisibility: async (reviewId: number, isPublic: boolean) => {
        try {
          const response = await reviewApi.updateVisibility(reviewId, isPublic);
          set((state) => ({
            reviewDetail: state.reviewDetail?.reviewId === reviewId 
              ? { ...state.reviewDetail, isPublic: response.isPublic }
              : state.reviewDetail
          }));
        } catch (error) {
          console.error('공개 여부 수정 실패:', error);
          throw error;
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
          console.error('댓글 수정 실패:', error);
          throw error;
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
          console.error('댓글 삭제 실패:', error);
          throw error;
        }
      }
    }),
    {
      name: 'review-storage',
      storage: createJSONStorage(() => localStorage)
    }
  )
);