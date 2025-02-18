import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { applicationApi } from '@/api/applicationApi';
import type { ApplicationItem } from '@/types/application';
import { toast } from 'react-hot-toast';

const RecruitManageVolunteers: React.FC = () => {
  const { recruitId } = useParams();
  const [applications, setApplications] = useState<ApplicationItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchApplications = async () => {
      try {
        setIsLoading(true);
        const response = await applicationApi.getRecruitApplications(Number(recruitId));
        setApplications(response.applications);
      } catch (error) {
        console.error('지원자 목록 조회 실패:', error);
        toast.error('지원자 목록을 불러오는데 실패했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    if (recruitId) {
      fetchApplications();
    }
  }, [recruitId]);

  if (isLoading) {
    return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  }

  return (
    <div className="container max-w-2xl mx-auto px-4 py-4">
      <h1 className="text-2xl font-bold mb-6">지원자 관리</h1>
      
      <div className="space-y-4">
        {applications.map((item) => (
          <Card key={item.application.applicationId} className="p-4">
            <div className="flex items-start gap-4">
              <Avatar className="h-12 w-12">
                <AvatarImage src={item.user.profileImage} alt={item.user.name} />
                <AvatarFallback>{item.user.name[0]}</AvatarFallback>
              </Avatar>
              
              <div className="flex-1">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-semibold">{item.user.name}</h3>
                    <p className="text-sm text-muted-foreground">{item.user.email}</p>
                  </div>
                  <Badge variant={item.application.status === '승인 대기' ? 'outline' : 'default'}>
                    {item.application.status}
                  </Badge>
                </div>
                
                <div className="mt-2 text-sm text-muted-foreground">
                  <p>추천수: {item.volunteer.recommendedCount}</p>
                  <p>총 봉사시간: {item.volunteer.totalVolunteerHours}시간</p>
                  <p>신청일: {new Date(item.application.createdAt).toLocaleDateString()}</p>
                </div>
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
};

export default RecruitManageVolunteers;
