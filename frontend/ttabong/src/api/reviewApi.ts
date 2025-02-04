import axios from 'axios';
import type { Review, ReviewDetail, Comment } from '@/types/review';

const BASE_URL = '/api';

export const reviewApi = {
  getReviews: async (): Promise<Review[]> => {
    const response = await axios.get(`${BASE_URL}/reviews`);
    return response.data;
  },
  getReviewDetail: async (id: number): Promise<ReviewDetail> => {
    const response = await axios.get(`${BASE_URL}/reviews/${id}`);
    return response.data;
  },
  addComment: async (reviewId: number, content: string): Promise<Comment> => {
    const response = await axios.post(`${BASE_URL}/reviews/${reviewId}/comments`, { content });
    return response.data;
  },
  getRecruitReviews: async (recruitId: number): Promise<Review[]> => {
    const response = await axios.get(`${BASE_URL}/recruits/${recruitId}/reviews`);
    return response.data;
  },
}; 