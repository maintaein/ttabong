import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import type { Application } from '@/types/recruitType';
import { useRecruitStore } from '@/stores/recruitStore';
import { cn } from '@/lib/utils';
import { useReviewWriteStore } from '@/stores/reviewWriteStore';


const STATUS_MAP = {
  PENDING: { label: '대기', className: 'bg-yellow-100 text-yellow-700' },
  APPROVED: { label: '승인', className: 'bg-green-100 text-green-700' },
  COMPLETED: { label: '완료', className: 'bg-blue-100 text-blue-700' },
  REJECTED: { label: '거절', className: 'bg-red-100 text-red-700' },
} as const;

export default function ChooseRecruit() {
  const navigate = useNavigate();
  const { myRecruits, isLoading, error, fetchMyRecruits } = useRecruitStore();
  const setReviewInfo = useReviewWriteStore(state => state.setReviewInfo);

  useEffect(() => {
    fetchMyRecruits();
  }, [fetchMyRecruits]);

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;

  const handleCardClick = async (application: Application) => {
    const orgId = application.template.group.groupId;
    const recruitId = application.recruit.recruitId;
    
    if (orgId && recruitId) {
      await Promise.resolve(setReviewInfo(recruitId, orgId));
      navigate('/review-write');
    }
  };

  return (
    <div className="container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-6">나의 봉사내역</h1>
      <div className="space-y-4">
        {myRecruits.map((application) => (
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
}

