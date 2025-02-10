import type { Review } from '@/types/reviewType';
import { ContentLoading } from '@/components/Loading';

interface ReviewGalleryGridProps {
  reviews: Review[];
  onReviewClick: (reviewId: number) => void;
}

export function ReviewGalleryGrid({ reviews, onReviewClick }: ReviewGalleryGridProps) {
  if (!reviews.length) {
    return (
      <div className="flex flex-col items-center justify-center py-8">
        <p className="text-muted-foreground">아직 작성된 리뷰가 없습니다</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-1">
      {reviews.map((review) => (
        <div
          key={review.review.reviewId}
          className="aspect-square group relative cursor-pointer"
          onClick={() => onReviewClick(review.review.reviewId)}
        >
          <img
            src={review.images[0]}
            alt={`리뷰 ${review.review.reviewId}`}
            className="w-full h-full object-cover"
            loading="lazy"
          />
          <div className="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition-opacity" />
        </div>
      ))}
    </div>
  );
}   