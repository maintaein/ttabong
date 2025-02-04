import { useNavigate } from 'react-router-dom';
import type { Review } from '@/types/review';

interface ReviewGalleryGridProps {
  reviews: Review[];
  basePath?: string;
}

export function ReviewGalleryGrid({ reviews, basePath = '/review-find' }: ReviewGalleryGridProps) {
  const navigate = useNavigate();

  return (
    <div className="grid grid-cols-3 gap-1">
      {reviews.map((review) => (
        <div
          key={review.reviewId}
          className="aspect-square group relative cursor-pointer"
          onClick={() => navigate(`${basePath}/${review.reviewId}`)}
        >
          <img
            src={review.thumbnailImageUrl}
            alt={`리뷰 ${review.reviewId}`}
            className="w-full h-full object-cover"
            loading="lazy"
          />
          <div className="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition-opacity" />
        </div>
      ))}
    </div>
  );
} 