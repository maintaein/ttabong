import { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useReviewStore } from '@/stores/review';
import { ReviewGalleryGrid } from './components/ReviewGalleryGrid';

export default function ReviewDetailList() {
  const { recruitId } = useParams();
  const { recruitReviews, isLoading, error, fetchRecruitReviews } = useReviewStore();

  useEffect(() => {
    if (recruitId) fetchRecruitReviews(Number(recruitId));
  }, [recruitId, fetchRecruitReviews]);

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;

  return (
    <div className="container mx-auto px-4 pt-6">
      <h2 className="text-lg font-bold mb-4">이 봉사활동의 다른 후기들</h2>
      <ReviewGalleryGrid 
        reviews={recruitReviews} 
        basePath={`/review-find/recruit/${recruitId}`} 
      />
    </div>
  );
}
