import axiosInstance from './axiosInstance';
import type { Review, ReviewDetail, Comment, ReviewEditResponse, VisibilityResponse, RecruitReview, UpdateReviewRequest, CommentDeleteResponse, MyReview } from '@/types/reviewType';

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
    const response = await axiosInstance.post(`/reviews/comments/${reviewId}`, { content });
    return response.data;
  },
  
  getRecruitReviews: async (recruitId: number): Promise<RecruitReview[]> => {
    const response = await axiosInstance.get(`/reviews/recruits/${recruitId}`);
    return response.data;
  },
  
  updateReview: async (reviewId: number, data: UpdateReviewRequest): Promise<ReviewEditResponse> => {
    const response = await axiosInstance.patch(`/reviews/${reviewId}/edit`, data);
    return response.data;
  },
  
  deleteReview: async (reviewId: number): Promise<void> => {
    await axiosInstance.patch(`/reviews/${reviewId}/delete`);
  },
  
  uploadReviewImage: async (presignedUrl: string, file: File): Promise<void> => {
    await fetch(presignedUrl, {
      method: 'PUT',
      body: file,
      headers: {
        'Content-Type': file.type
      }
    });
  },
  
  updateVisibility: async (reviewId: number, isPublic: boolean): Promise<VisibilityResponse> => {
    const response = await axiosInstance.patch(`/reviews/${reviewId}/visibility`, { isPublic });
    return response.data;
  },
  
  updateComment: async (commentId: number, content: string): Promise<Comment> => {
    const response = await axiosInstance.patch(`/reviews/comments/${commentId}`, { content });
    return response.data;
  },
  
  deleteComment: async (commentId: number): Promise<CommentDeleteResponse> => {
    const response = await axiosInstance.patch(`/reviews/comments/${commentId}/delete`);
    return response.data;
  },
  
  getMyReviews: async (): Promise<MyReview[]> => {
    const response = await axiosInstance.get('/reviews/mine');
    return response.data;
  }
}; 