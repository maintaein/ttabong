import React from 'react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';
import type { RecruitItem } from '@/types/recruit';
import { Button } from "@/components/ui/button";
import { useNavigate } from 'react-router-dom';

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

const formatTime = (time: number) => {
  const hours = Math.floor(time);
  const minutes = Math.round((time - hours) * 60);
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
};

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

  return (
    <Card 
      className={cn(
        "p-4 hover:shadow-md transition-shadow cursor-pointer",
        isSelected && "border-primary"
      )}
      onClick={handleClick}
    >
      <div className="flex justify-between items-start">
        {isEditing && (
          <input
            type="checkbox"
            checked={isSelected}
            onChange={onSelect}
            className="mr-2"
            onClick={(e) => e.stopPropagation()}
          />
        )}
        <div className="space-y-2">
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">{group.groupName}</p>
            <h3 className="font-semibold">{template.title}</h3>
          </div>
          <div className="space-y-1">
            <p className="text-sm">
              활동일: {recruitData.activityDate} {formatTime(recruitData.activityStart)}~{formatTime(recruitData.activityEnd)}
            </p>
            <p className="text-sm">
              신청현황: {recruitData.participateVolCount}/{recruitData.maxVolunteer}명
            </p>
            <p className="text-sm text-muted-foreground">
              마감일: {new Date(recruitData.deadline).toLocaleDateString()}
            </p>
          </div>
        </div>
        <Badge 
          variant="secondary"
          className={cn(
            "ml-2",
            STATUS_MAP[recruitData.status as keyof typeof STATUS_MAP]?.className || ''
          )}
        >
          {STATUS_MAP[recruitData.status as keyof typeof STATUS_MAP]?.label || recruitData.status}
        </Badge>
        <div className="flex gap-2">
          {!isEditing && (
            <Button
              variant="outline"
              size="sm"
              onClick={handleEditClick}
            >
              수정
            </Button>
          )}
        </div>
      </div>
    </Card>
  );
}; 