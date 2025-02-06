import React from 'react';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useReviewStore } from '@/stores/reviewStore';
import { Input } from '@/components/ui/input';
import { Search } from 'lucide-react';
import { ReviewGalleryGrid } from '@/pages/ReviewFind/components/ReviewGalleryGrid';
import { PageLayout } from '@/layout/PageLayout';

export default function ReviewFind() {
  const navigate = useNavigate();
  const { reviews, isLoading, error, fetchReviews } = useReviewStore();

  useEffect(() => {
    fetchReviews();
  }, [fetchReviews]);

  const handleReviewClick = (reviewId: number) => {
    navigate(`/review-find/${reviewId}`);
  };

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;

  return (
    <PageLayout>
      <div className="container mx-auto px-4 pt-6">
        <div className="mb-6 relative max-w-md mx-auto">
          <Input
            type="search"
            placeholder="리뷰 검색..."
            className="pl-10"
          />
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
        </div>
        <ReviewGalleryGrid 
          reviews={reviews} 
          onReviewClick={handleReviewClick}
        />
      </div>
    </PageLayout>
  );
}
