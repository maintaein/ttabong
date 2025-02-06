import axiosInstance from './axiosInstance';
import type { Review, ReviewDetail, Comment } from '@/types/reviewType';

interface RecruitReview {
  review: {
    reviewId: number;
    recruitId: number;
    title: string;
    content: string;
    isDeleted: boolean;
    updatedAt: string;
    createdAt: string;
  };
  writer: {
    writerId: number;
    name: string;
  };
  group: {
    groupId: number;
    groupName: string;
  };
  organization: {
    orgId: number;
    orgName: string;
  };
  images: string[];
}

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
  
  getRecruitReviews: async (recruitId: number): Promise<RecruitReview[]> => {
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