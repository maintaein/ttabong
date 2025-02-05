import { useEffect } from 'react';
import { useReviewStore } from '@/stores/review';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Search, PenSquare } from 'lucide-react';
import { ReviewGalleryGrid } from '@/pages/ReviewFind/components/ReviewGalleryGrid';
import { useNavigate } from 'react-router-dom';
import { PageLayout } from '@/layout/PageLayout';

export default function ReviewFind() {
  const navigate = useNavigate();
  const { reviews, isLoading, error, fetchReviews } = useReviewStore();

  useEffect(() => {
    fetchReviews();
  }, [fetchReviews]);

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;

  return (
    <PageLayout>
      <div className="container mx-auto px-4 pt-6 relative">
        <div className="mb-6 relative max-w-md mx-auto">
          <Input
            type="search"
            placeholder="리뷰 검색..."
            className="pl-10"
          />
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
        </div>
        <ReviewGalleryGrid reviews={reviews} />
        
        <Button 
          className="fixed bottom-20 right-[max(calc((100%-600px)/2+1rem),1rem)] rounded-full w-14 h-14 shadow-lg"
          onClick={() => navigate('/review-write')}
        >
          <PenSquare className="w-6 h-6" />
          <span className="sr-only">후기 작성</span>
        </Button>
      </div>
    </PageLayout>
  );
}
