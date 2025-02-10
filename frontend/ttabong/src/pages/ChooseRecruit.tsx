import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import type { Application } from '@/types/recruitType';
import { useRecruitStore } from '@/stores/recruitStore';
import { cn } from '@/lib/utils';
import React, { useState } from 'react';
import { UserTypeSelect } from './ChooseRecruit/UserTypeSelect';
import { OrgView } from './ChooseRecruit/org/OrgView';

type UserType = 'org' | 'volunteer' | null;

const STATUS_MAP = {
  'PENDING': {
    label: '승인 대기',
    className: 'bg-yellow-100 text-yellow-800'
  },
  'APPROVED': {
    label: '승인 완료',
    className: 'bg-green-100 text-green-800'
  },
  'REJECTED': {
    label: '거절됨',
    className: 'bg-red-100 text-red-800'
  },
  'COMPLETED': {
    label: '완료됨',
    className: 'bg-blue-100 text-blue-800'
  }
} as const;

const ChooseRecruit: React.FC = () => {
  const [userType, setUserType] = useState<UserType>(null);
  const navigate = useNavigate();
  const { myRecruits, isLoading, error, fetchMyRecruits } = useRecruitStore();

  useEffect(() => {
    fetchMyRecruits();
  }, [fetchMyRecruits]);

  if (!userType) {
    return <UserTypeSelect onSelect={setUserType} />;
  }

  if (userType === 'org') {
    return <OrgView />;
  }

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;

  const handleCardClick = async (application: Application) => {
    const orgId = application.template.group.groupId;
    const recruitId = application.recruit.recruitId;

    if (orgId && recruitId) {
      navigate(`/review-write?recruitId=${recruitId}&orgId=${orgId}`);
    }
  };

  return (
    <div className="container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-6">나의 봉사내역</h1>
      <div className="space-y-4">
        {(myRecruits ?? []).map((application) => (
          <Card 
            key={application.applicationId}
            className="p-4 hover:shadow-md transition-shadow cursor-pointer"
            onClick={() => handleCardClick(application)}
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
        ))}
      </div>
    </div>
  );
};

export default ChooseRecruit;

