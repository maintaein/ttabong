import type { ReviewDetail } from '@/types/reviewType';

interface ReviewWriteHeaderProps {
  writer: ReviewDetail['writer'];
  organization: ReviewDetail['organization'];
  isPublic: boolean;
}

export function ReviewWriteHeader({ writer, organization, isPublic }: ReviewWriteHeaderProps) {
  return (
    <div className="px-4 py-3 flex items-center justify-between">
      <div className="flex items-center gap-3">
        {/* 프로필 이미지 */}
        <div className="relative w-10 h-10 rounded-full overflow-hidden">
          <img
            src={writer.writerProfileImage}
            alt={writer.writerName}
            className="w-full h-full object-cover"
          />
        </div>
        
        {/* 작성자 및 단체 정보 */}
        <div className="flex flex-col">
          <span className="font-medium text-sm">
            {writer.writerName}
          </span>
          <span className="text-muted-foreground text-sm">
            {organization.orgName}
          </span>
        </div>
      </div>

      {/* 공개 여부 표시 */}
      <div className="flex items-center text-sm text-muted-foreground">
        <span>{isPublic ? '전체 공개' : '비공개'}</span>
      </div>
    </div>
  );
} 