import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import type { ReviewDetail } from '@/types/review';
import { useNavigate } from 'react-router-dom';

interface ReviewContentProps {
  reviewId: number;
  title: string;
  content: string;
  category: ReviewDetail['category'];
  isOrganization: boolean;
  orgReviewId?: number;
}

export function ReviewContent({ 
  reviewId, 
  title, 
  content, 
  category, 
  isOrganization,
  orgReviewId 
}: ReviewContentProps) {
  const navigate = useNavigate();

  return (
    <Card className="border-0 shadow-none">
      <CardContent className="p-4 space-y-4">
        <Badge variant="secondary">{category.name}</Badge>
        <h2 className="text-xl font-bold">{title}</h2>
        <p className="text-gray-600 whitespace-pre-wrap">{content}</p>
        
        {isOrganization ? (
          <Button 
            className="w-full" 
            variant="outline"
            onClick={() => navigate(`/review-find/${reviewId}/reviews`)}
          >
            이 봉사활동의 다른 후기 보기
          </Button>
        ) : orgReviewId && (
          <Button 
            className="w-full" 
            variant="outline"
            onClick={() => navigate(`/review-find/${orgReviewId}`)}
          >
            기관 후기 보러가기
          </Button>
        )}
      </CardContent>
    </Card>
  );
} 