import type { Review } from '@/types/reviewType';

interface ReviewGalleryGridProps {
  reviews: Review[];
  onReviewClick: (id: number) => void;
  lastReviewRef?: (node: HTMLDivElement) => void;
}

export function ReviewGalleryGrid({ reviews, onReviewClick, lastReviewRef }: ReviewGalleryGridProps) {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      {reviews.map((review, index) => (
        <div
          key={`${review.review.reviewId}_${index}`}
          ref={index === reviews.length - 1 ? lastReviewRef : undefined}
          onClick={() => onReviewClick(review.review.reviewId)}
          className="cursor-pointer"
        >
          <div className="aspect-square group relative cursor-pointer">
            <img
              src={review.images[0]}
              alt={`리뷰 ${review.review.reviewId}`}
              className="w-full h-full object-cover"
              loading="lazy"
            />
            <div className="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition-opacity" />
          </div>
        </div>
      ))}
    </div>
  );
} 