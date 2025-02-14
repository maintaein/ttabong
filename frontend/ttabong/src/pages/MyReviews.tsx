import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent } from '@/components/ui/card';
import { useReviewStore } from '@/stores/reviewStore';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';

export default function MyReviews() {
  const navigate = useNavigate();
  const { myReviews, isLoading, error, fetchMyReviews } = useReviewStore();

  React.useEffect(() => {
    fetchMyReviews();
  }, [fetchMyReviews]);

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;
  if (!myReviews?.length) return <div className="flex justify-center items-center h-[50vh]">작성한 후기가 없습니다.</div>;

  return (
    <div className="container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-6">나의 봉사후기</h1>
      <div className="space-y-4">
        {myReviews.map((item) => (
          <Card 
            key={item.review.reviewId}
            className="cursor-pointer hover:shadow-md transition-shadow"
            onClick={() => navigate(`/review-find/${item.review.reviewId}`)}
          >
            <CardContent className="p-4">
              <div className="flex gap-4">
                {item.images && (
                  <div className="w-[100px] h-[100px] rounded-md overflow-hidden flex-shrink-0">
                    <img 
                      src={item.images} 
                      alt={item.review.title}
                      className="w-full h-full object-cover"
                    />
                  </div>
                )}
                <div className="flex-1">
                  <h3 className="font-semibold mb-2">{item.review.title}</h3>
                  <p className="text-sm text-muted-foreground mb-2">{item.group.groupName}</p>
                  <p className="text-xs text-muted-foreground">
                    {format(new Date(item.review.createdAt), 'PPP', { locale: ko })}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
} 