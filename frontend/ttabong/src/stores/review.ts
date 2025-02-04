import { create } from 'zustand';
import type { Review } from '@/types/review';
import { reviewApi } from '@/api/review';

interface ReviewStore {
  reviews: Review[];
  isLoading: boolean;
  error: string | null;
  fetchReviews: () => Promise<void>;
}

export const useReviewStore = create<ReviewStore>((set) => ({
  reviews: [],
  isLoading: false,
  error: null,
  fetchReviews: async () => {
    set({ isLoading: true });
    try {
      const reviews = await reviewApi.getReviews();
      set({ reviews, error: null });
    } catch (error) {
      set({ error: '리뷰를 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }
  },
})); 