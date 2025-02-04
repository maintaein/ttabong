interface Review {
  reviewId: number;
  thumbnailImageUrl: string;
}

interface Comment {
  commentId: number;
  writerId: number;
  writerName: string;
  content: string;
  createdAt: string;
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

export type { Review, ReviewDetail, Comment };