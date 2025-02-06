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
  const { reviewDetail, isLoading, error, fetchReviewDetail, addComment, deleteReview, toggleReviewPublic, updateComment, deleteComment } = useReviewStore();
  const [isOpen, setIsOpen] = useState(false);

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

  const handleTogglePublic = async () => {
    if (!reviewDetail) return;
    
    setIsOpen(false);
    
    setTimeout(() => {
      toggleReviewPublic(reviewDetail.reviewId, reviewDetail.isPublic);
    }, 300);
  };

  return (
    <div className="pb-32">
      <div className="p-4 border-b">
        <div className="flex items-center justify-between">
          <ReviewHeader 
            writer={reviewDetail.writer} 
            organization={reviewDetail.organization} 
            isPublic={reviewDetail.isPublic} 
          />
          {isOwner && (
            <DropdownMenu open={isOpen} onOpenChange={setIsOpen}>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon" className="h-8 w-8">
                  <MoreVertical className="h-4 w-4" />
                  <span className="sr-only">더보기 메뉴</span>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem onClick={handleEdit}>
                  수정하기
                </DropdownMenuItem>
                <DropdownMenuItem onClick={handleDelete} className="text-destructive">
                  삭제하기
                </DropdownMenuItem>
                <DropdownMenuItem onClick={handleTogglePublic}>
                  {reviewDetail.isPublic ? '비공개하기' : '공개하기'}
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          )}
        </div>
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
      
      <div className="px-4 mt-4 flex gap-2">
        {reviewDetail.recruit?.recruitId && (
          <Button 
            variant="outline" 
            className="flex-1"
            onClick={() => navigate(`/recruits/${reviewDetail.recruit.recruitId}`)}
          >
            봉사 공고 보러가기
          </Button>
        )}
        
        {isOrganization ? (
          <Button 
            variant="outline"
            className="flex-1"
            onClick={() => navigate(`/review-find/${reviewDetail.recruit.recruitId}/reviews`)}
          >
            다른 후기 보기
          </Button>
        ) : reviewDetail.orgReviewId && (
          <Button 
            variant="outline"
            className="flex-1"
            onClick={() => navigate(`/review-find/${reviewDetail.orgReviewId}`)}
          >
            기관 후기 보기
          </Button>
        )}
      </div>

      <ReviewComments
        comments={reviewDetail.comments}
        commentContent={commentContent}
        onCommentChange={(e) => setCommentContent(e.target.value)}
        onSubmit={handleSubmitComment}
        onUpdateComment={updateComment}
        onDeleteComment={deleteComment}
      />
    </div>
  );
}
