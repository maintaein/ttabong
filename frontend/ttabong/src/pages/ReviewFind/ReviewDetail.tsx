import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useReviewStore } from '@/stores/reviewStore';
import { ReviewHeader } from './components/ReviewHeader';
import { ReviewGallery } from './components/ReviewGallery';
import { ReviewContent } from './components/ReviewContent';
import { ReviewComments } from './components/ReviewComments';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Button } from '@/components/ui/button';
import { MoreVertical } from 'lucide-react';

export default function ReviewDetail() {
  const { reviewId } = useParams();
  const { reviewDetail, isLoading, error, fetchReviewDetail, addComment, deleteReview } = useReviewStore();

  const [commentContent, setCommentContent] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    if (reviewId) fetchReviewDetail(Number(reviewId));
  }, [reviewId, fetchReviewDetail]);

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;
  if (!reviewDetail) return null;

  const isOrganization = !reviewDetail.orgReviewId && Boolean(reviewDetail.recruit?.recruitId);

  const isOwner = true;

  const handleSubmitComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!commentContent.trim() || !reviewId) return;
    await addComment(Number(reviewId), commentContent);
    setCommentContent('');
  };

  const handleEdit = () => {
    navigate(`/review-write/${reviewDetail?.reviewId}`);
  };

  const handleDelete = async () => {
    if (!reviewDetail?.reviewId) return;
    
    if (window.confirm('정말 삭제하시겠습니까?')) {
      await deleteReview(reviewDetail.reviewId);
      navigate(-1);
    }
  };

  return (
    <div className="pb-32">
      <div className="flex justify-between items-center p-4">
        <ReviewHeader writer={reviewDetail.writer} organization={reviewDetail.organization} />
        
        {isOwner && (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon">
                <MoreVertical className="h-5 w-5" />
                <span className="sr-only">더보기 메뉴</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={handleEdit}>
                수정하기
              </DropdownMenuItem>
              <DropdownMenuItem 
                onClick={handleDelete}
                className="text-destructive"
              >
                삭제하기
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        )}
      </div>
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
