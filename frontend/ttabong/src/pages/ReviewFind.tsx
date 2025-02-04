import { useEffect } from 'react';
import { useReviewStore } from '@/stores/review';
import { Input } from '@/components/ui/input';

export default function ReviewFind() {
  const { reviews, isLoading, error, fetchReviews } = useReviewStore();

  useEffect(() => {
    fetchReviews();
  }, [fetchReviews]);

  if (isLoading) return <div>로딩 중...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="container mx-auto p-4">
      <div className="mb-6">
        <Input
          type="search"
          placeholder="리뷰 검색..."
          className="max-w-md mx-auto"
        />
      </div>
      
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {reviews.map((review) => (
          <div
            key={review.reviewId}
            className="aspect-square overflow-hidden rounded-lg shadow-md hover:shadow-lg transition-shadow"
          >
            <img
              src={review.thumbnailImageUrl}
              alt={`리뷰 ${review.reviewId}`}
              className="w-full h-full object-cover"
              loading="lazy"
            />
          </div>
        ))}
      </div>
    </div>
  );
}
