import axiosInstance from './axiosInstance';
import type { Review, ReviewDetail, Comment } from '@/types/reviewType';

export const reviewApi = {
  getReviews: async (): Promise<Review[]> => {
    const response = await axiosInstance.get('/reviews');
    return response.data;
  },
  
  getReviewDetail: async (id: number): Promise<ReviewDetail> => {
    const response = await axiosInstance.get(`/reviews/${id}`);
    return response.data;
  },
  
  addComment: async (reviewId: number, content: string): Promise<Comment> => {
    const response = await axiosInstance.post(`/reviews/${reviewId}/comments`, { content });
    return response.data;
  },
  
  getRecruitReviews: async (recruitId: number): Promise<Review[]> => {
    const response = await axiosInstance.get(`/recruits/${recruitId}/reviews`);
    return response.data;
  },
  
  updateReview: async (reviewId: number, data: Partial<ReviewDetail>): Promise<ReviewDetail> => {
    const response = await axiosInstance.patch(`/reviews/${reviewId}`, data);
    return response.data;
  },
  
  deleteReview: async (reviewId: number): Promise<void> => {
    await axiosInstance.patch(`/reviews/${reviewId}/delete`);
  },
}; 