import React from 'react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';
import type { Application } from '@/types/recruitType';

const STATUS_MAP = {
  PENDING: { label: '대기', className: 'bg-yellow-100 text-yellow-700' },
  APPROVED: { label: '승인', className: 'bg-green-100 text-green-700' },
  COMPLETED: { label: '완료', className: 'bg-blue-100 text-blue-700' },
  REJECTED: { label: '거절', className: 'bg-red-100 text-red-700' },
} as const;

interface RecruitCardProps {
  application: Application;
  onClick: (application: Application) => void;
}

export const RecruitCard: React.FC<RecruitCardProps> = ({ application, onClick }) => {
  return (
    <Card 
      className="p-4 hover:shadow-md transition-shadow cursor-pointer"
      onClick={() => onClick(application)}
    >
      <div className="flex justify-between items-start">
        <div className="space-y-2">
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">{application.template.group.groupName}</p>
            <h3 className="font-semibold">{application.template.title}</h3>
          </div>
          <div className="space-y-1">
            <p className="text-sm">
              {application.recruit.activityDate} {application.recruit.activityTime}
            </p>
            <p className="text-sm text-muted-foreground">{application.template.activityLocation}</p>
          </div>
        </div>
        <Badge 
          variant="secondary"
          className={cn(
            "ml-2",
            STATUS_MAP[application.status].className
          )}
        >
          {STATUS_MAP[application.status].label}
        </Badge>
      </div>
    </Card>
  );
}; 