import { ReviewHeader } from '@/pages/ReviewFind/components/ReviewHeader';
import type { ReviewDetail } from '@/types/reviewType';

interface ReviewWriteHeaderProps {
  writer: ReviewDetail['writer'];
  organization: ReviewDetail['organization'];
  isPublic: boolean;
}

export function ReviewWriteHeader({ writer, organization, isPublic }: ReviewWriteHeaderProps) {
  return <ReviewHeader writer={writer} organization={organization} isPublic={isPublic} />;
} 