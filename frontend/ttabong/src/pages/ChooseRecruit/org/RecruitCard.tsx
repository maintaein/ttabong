import React from 'react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';
import type { RecruitItem } from '@/types/recruit';
import { Button } from "@/components/ui/button";
import { useNavigate } from 'react-router-dom';
import { formatTime, formatDate, formatTimeRange, formatDeadline } from '@/lib/dateUtils';

const STATUS_MAP = {
  'RECRUITING': { label: '모집중', className: 'bg-green-100 text-green-700' },
  'CLOSED': { label: '모집마감', className: 'bg-yellow-100 text-yellow-700' },
  'COMPLETED': { label: '활동완료', className: 'bg-blue-100 text-blue-700' },
} as const;

interface RecruitCardProps {
  recruit: RecruitItem;
  isEditing: boolean;
  isSelected: boolean;
  onSelect: () => void;
}

export const RecruitCard: React.FC<RecruitCardProps> = ({ 
  recruit, 
  isEditing,
  isSelected,
  onSelect 
}) => {
  const navigate = useNavigate();
  const { group, template, recruit: recruitData } = recruit;

  const handleEditClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    navigate('/template-and-group-write', {
      state: { 
        isRecruitEdit: true,
        recruitId: recruitData.recruitId,
        templateId: template.templateId,
        template: template,
        recruitData: {
          deadline: recruitData.deadline,
          activityDate: recruitData.activityDate,
          activityStart: recruitData.activityStart,
          activityEnd: recruitData.activityEnd,
          maxVolunteer: recruitData.maxVolunteer,
          groupId: group.groupId,
          status: recruitData.status
        }
      }
    });
  };

  const handleClick = (e: React.MouseEvent) => {
    if (isEditing) {
      e.preventDefault();
      onSelect();
      return;
    }
    navigate(`/recruits/${recruitData.recruitId}`, { state: { recruit } });
  };

  const handleReviewClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    navigate('/review-write', {
      state: { 
        recruitId: recruitData.recruitId,
        isOrgReview: true
      }
    });
  };

  return (
    <Card 
      className={cn(
        "p-4 hover:shadow-md transition-shadow cursor-pointer",
        isSelected && "border-primary"
      )}
      onClick={handleClick}
    >
      <div className="flex flex-col space-y-4">
        <div className="flex items-start justify-between">
          <div className="flex-1 min-w-0">
            <p className="text-sm text-muted-foreground truncate">{group.groupName}</p>
            <h3 className="font-semibold line-clamp-2">{template.title}</h3>
          </div>
          <Badge 
            variant="secondary"
            className={cn(
              "ml-2 shrink-0",
              STATUS_MAP[recruitData.status as keyof typeof STATUS_MAP]?.className || ''
            )}
          >
            {STATUS_MAP[recruitData.status as keyof typeof STATUS_MAP]?.label || recruitData.status}
          </Badge>
        </div>

        <div className="grid grid-cols-2 gap-2 text-sm">
          <div>
            <span className="text-muted-foreground">활동일시</span>
            <p>{formatDate(recruitData.activityDate)}</p>
            <p>{formatTimeRange(recruitData.activityStart, recruitData.activityEnd)}</p>
          </div>
          <div>
            <span className="text-muted-foreground">신청현황</span>
            <p>{recruitData.participateVolCount}/{recruitData.maxVolunteer}명</p>
          </div>
          <div className="col-span-2">
            <span className="text-muted-foreground">마감일</span>
            <p>{formatDeadline(recruitData.deadline)}</p>
          </div>
        </div>

        <div className="flex justify-end gap-2">
          {isEditing && (
            <input
              type="checkbox"
              checked={isSelected}
              onChange={onSelect}
              className="h-4 w-4 mt-2"
              onClick={(e) => e.stopPropagation()}
            />
          )}
          {!isEditing && (
            <Button
              variant="outline"
              size="sm"
              onClick={handleEditClick}
            >
              수정
            </Button>
          )}
          {recruitData.status === 'ACTIVITY_COMPLETED' && !isEditing && (
            <Button
              size="sm"
              onClick={handleReviewClick}
            >
              후기 작성
            </Button>
          )}
        </div>
      </div>
    </Card>
  );
}; 