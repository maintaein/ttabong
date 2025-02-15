import { useState } from 'react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { ChevronDown, ChevronUp } from 'lucide-react';
import type { RecruitDetail } from '@/types/recruitType';

interface RecruitDetailCardProps {
  recruitDetail: RecruitDetail;
}

export function RecruitDetailCard({ recruitDetail }: RecruitDetailCardProps) {
  const [isExpanded, setIsExpanded] = useState(false);

  function formatTime(time: number): string {
    const hours = Math.floor(time);
    const minutes = Math.round((time - hours) * 60);
    return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
  }

  return (
    <Card className="mb-6">
      <div className="p-4">
        <div className="flex justify-between items-center">
          <div>
            <p className="text-sm text-muted-foreground">{recruitDetail.group.groupName}</p>
            <h2 className="text-lg font-semibold">{recruitDetail.template.title}</h2>
          </div>
          <Button 
            variant="ghost" 
            size="sm" 
            onClick={() => setIsExpanded(!isExpanded)}
          >
            {isExpanded ? <ChevronUp /> : <ChevronDown />}
          </Button>
        </div>
        
        <div className="mt-2">
          <p className="text-sm">{recruitDetail.template.activityLocation}</p>
          <p className="text-sm">
            활동시간: {formatTime(recruitDetail.recruit.activityStart)}~{formatTime(recruitDetail.recruit.activityEnd)}
          </p>
        </div>

        {isExpanded && (
          <div className="mt-4 space-y-4 border-t pt-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm font-medium">모집 현황</p>
                <p className="text-sm">{recruitDetail.recruit.participateVolCount}/{recruitDetail.recruit.maxVolunteer}명</p>
              </div>
              <div>
                <p className="text-sm font-medium">모집 마감일</p>
                <p className="text-sm">{recruitDetail.recruit.deadline}</p>
              </div>
            </div>
            <div>
              <p className="text-sm font-medium">담당자 연락처</p>
              <p className="text-sm">{recruitDetail.template.contactName} ({recruitDetail.template.contactPhone})</p>
            </div>
            <div>
              <p className="text-sm font-medium">활동 설명</p>
              <p className="text-sm whitespace-pre-line">{recruitDetail.template.description}</p>
            </div>
            {recruitDetail.template.images?.length > 0 && (
              <div className="grid grid-cols-2 gap-2">
                {recruitDetail.template.images.map((image, index) => (
                  <img 
                    key={index} 
                    src={image} 
                    alt={`활동 이미지 ${index + 1}`} 
                    className="rounded-md w-full h-40 object-cover"
                  />
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </Card>
  );
} 