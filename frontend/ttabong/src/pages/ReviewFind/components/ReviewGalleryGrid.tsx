import React from 'react';
import { Card } from '@/components/ui/card';
import type { ReviewListItem } from '@/types/reviewType';

interface ReviewGalleryGridProps {
  reviews: ReviewListItem[];
  onReviewClick: (reviewId: number) => void;
}

export const ReviewGalleryGrid: React.FC<ReviewGalleryGridProps> = ({ reviews = [], onReviewClick }) => {
  if (!reviews?.length) {
    return <div className="text-center text-muted-foreground">아직 작성된 후기가 없습니다.</div>;
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {reviews.map((review) => (
        <Card
          key={review.review.reviewId}
          className="cursor-pointer hover:shadow-md transition-shadow"
          onClick={() => onReviewClick(review.review.reviewId)}
        >
          <div className="aspect-video relative overflow-hidden">
            {review.images && (
              <img
                src={review.images}
                alt={review.review.title}
                className="object-cover w-full h-full"
              />
            )}
          </div>
          <div className="p-4">
            <h3 className="font-semibold truncate">{review.review.title}</h3>
            <p className="text-sm text-muted-foreground truncate">{review.review.content}</p>
          </div>
        </Card>
      ))}
    </div>
  );
};   