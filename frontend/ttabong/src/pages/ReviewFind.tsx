import { useEffect, useRef, useCallback, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useReviewStore } from '@/stores/reviewStore';
import { Input } from '@/components/ui/input';
import { Search } from 'lucide-react';
import { ReviewGalleryGrid } from '@/pages/ReviewFind/components/ReviewGalleryGrid';
import { PageLayout } from '@/layout/PageLayout';

const ReviewFind: React.FC = () => {
  const navigate = useNavigate();
  const { reviews, isLoading, error, hasMore, fetchReviews } = useReviewStore();
  const observer = useRef<IntersectionObserver>();
  const [isInitialLoad, setIsInitialLoad] = useState(true);

  const lastReviewRef = useCallback((node: HTMLDivElement) => {
    if (isLoading) return;
    
    if (observer.current) observer.current.disconnect();
    
    observer.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && hasMore) {
        console.log('Fetching more reviews...'); // 디버깅용
        const lastReviewId = reviews[reviews.length - 1]?.review.reviewId;
        fetchReviews(lastReviewId);
      }
    });
    
    if (node) observer.current.observe(node);
  }, [isLoading, hasMore, reviews, fetchReviews]);

  useEffect(() => {
    if (isInitialLoad) {
      console.log('Initial load...'); // 디버깅용
      fetchReviews(0);
      setIsInitialLoad(false);
    }
  }, [fetchReviews, isInitialLoad]);

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
        
        <div className="space-y-4">
          <ReviewGalleryGrid 
            reviews={reviews} 
            onReviewClick={(id) => navigate(`/review-find/${id}`)}
            lastReviewRef={lastReviewRef}
          />
          {isLoading && (
            <div className="flex justify-center py-4">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
            </div>
          )}
          {error && (
            <div className="text-destructive text-center py-4">{error}</div>
          )}
          {!hasMore && reviews.length > 0 && (
            <div className="text-center py-4 text-muted-foreground">
              모든 리뷰를 불러왔습니다
            </div>
          )}
        </div>
      </div>
    </PageLayout>
  );
};

export default ReviewFind;
