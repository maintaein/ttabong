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

interface ReviewResponse {
  reviews: Review[];
  hasMore: boolean;
}

export const reviewApi = {
  getReviews: async (cursor: number, limit: number): Promise<ReviewResponse> => {
    const response = await axiosInstance.get(`/reviews?cursor=${cursor}&limit=${limit}`);
    return response.data;
  },
  
  getReviewDetail: async (id: number): Promise<ReviewDetail> => {
    const response = await axiosInstance.get(`/reviews/${id}`);
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
  
  toggleReviewPublic: async (reviewId: number, isPublic: boolean): Promise<void> => {
    await axiosInstance.patch(`/reviews/${reviewId}/toggle-public`, { isPublic });
  },

  addComment: async (reviewId: number, content: string): Promise<Comment> => {
    const response = await axiosInstance.post(`/reviews/${reviewId}/comments`, { content });
    return response.data;
  },
  
  updateComment: async (commentId: number, content: string): Promise<Comment> => {
    const response = await axiosInstance.patch(`/comments/${commentId}`, { content });
    return response.data;
  },
  
  deleteComment: async (commentId: number): Promise<void> => {
    await axiosInstance.patch(`/comments/${commentId}/delete`);
  },
}; 