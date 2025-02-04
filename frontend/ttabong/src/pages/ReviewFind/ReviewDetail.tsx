import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useReviewStore } from '@/stores/review';
import { ReviewHeader } from './components/ReviewHeader';
import { ReviewGallery } from './components/ReviewGallery';
import { ReviewContent } from './components/ReviewContent';
import { ReviewComments } from './components/ReviewComments';


export default function ReviewDetail() {
  const { reviewId } = useParams();
  const { reviewDetail, isLoading, error, fetchReviewDetail, addComment } = useReviewStore();
  const [commentContent, setCommentContent] = useState('');

  useEffect(() => {
    if (reviewId) fetchReviewDetail(Number(reviewId));
  }, [reviewId, fetchReviewDetail]);

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;
  if (!reviewDetail) return null;

  const isOrganization = !reviewDetail.orgReviewId && Boolean(reviewDetail.recruit?.recruitId);

  const handleSubmitComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!commentContent.trim() || !reviewId) return;
    await addComment(Number(reviewId), commentContent);
    setCommentContent('');
  };

  return (
    <div className="pb-32">
      <ReviewHeader writer={reviewDetail.writer} organization={reviewDetail.organization} />
      <ReviewGallery 
        images={reviewDetail.images} 
        isOrganization={isOrganization}
      />
      <ReviewContent
        reviewId={reviewDetail.reviewId}
        title={reviewDetail.title}
        content={reviewDetail.content}
        category={reviewDetail.category}
        isOrganization={isOrganization}
        orgReviewId={reviewDetail.orgReviewId}
        recruit={reviewDetail.recruit}
      />
      <ReviewComments
        comments={reviewDetail.comments}
        commentContent={commentContent}
        onCommentChange={(e) => setCommentContent(e.target.value)}
        onSubmit={handleSubmitComment}
      />
    </div>
  );
}
