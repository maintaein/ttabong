interface Review {
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

interface Comment {
  commentId: number;
  content: string;
  writerId: number;
  writerName: string;
  createdAt: string;
  updatedAt: string;
}

interface ReviewDetail {
  reviewId: number;
  title: string;
  content: string;
  isDeleted: boolean;
  isPublic: boolean;
  attended: boolean;
  createdAt: string;
  images: string[];
  recruit: {
    recruitId: number;
    activityDate: string;
    activityTime: string;
    status: string;
  };
  category: {
    categoryId: number;
    name: string;
  };
  writer: {
    writerId: number;
    writerName: string;
    writerProfileImage: string;
  };
  template: {
    templateId: number;
    title: string;
    activityLocation: string;
    status: string;
    group: {
      groupId: number;
      groupName: string;
    };
  };
  organization: {
    orgId: number;
    orgName: string;
  };
  orgReviewId?: number;
  comments: Comment[];
}

interface UpdateReviewRequest {
  title: string;
  content: string;
  isPublic: boolean;
  images: string[];        // 업로드에 사용된 presignedUrl 목록
  imageCount: number;
}

interface ReviewEditResponse {
  cacheId: number;
  writerId: number;
  title: string;
  content: string;
  isPublic: boolean;
  getImages: string[];      // 기존 이미지 URL (다운로드용)
  presignedUrl: string[];   // 새 이미지 업로드용 URL
}

interface VisibilityResponse {
  message: string;
  reviewId: number;
  isPublic: boolean;
  updatedAt: string;
}

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

interface CommentDeleteResponse {
  message: string;
  commentId: number;
}

export type { 
  Review, 
  ReviewDetail, 
  Comment, 
  UpdateReviewRequest, 
  ReviewEditResponse, 
  VisibilityResponse, 
  RecruitReview,
  CommentDeleteResponse 
};