import React from 'react';
import { useNavigate } from 'react-router-dom';
import { RecruitList } from './RecruitList';
import type { Application } from '@/types/recruitType';
import { useRecruitStore } from '@/stores/recruitStore';
import { useReviewStore } from '@/stores/reviewStore';

export const VolunteerView: React.FC = () => {
  const navigate = useNavigate();
  const { myRecruits, isLoading, error, fetchMyRecruits } = useRecruitStore();
  const setReviewInfo = useReviewStore(state => state.setReviewInfo);

  React.useEffect(() => {
    fetchMyRecruits();
  }, [fetchMyRecruits]);

  const handleRecruitClick = async (application: Application) => {
    const orgId = application.template.group.groupId;
    const recruitId = application.recruit.recruitId;
    
    if (orgId && recruitId) {
      await Promise.resolve(setReviewInfo(recruitId, orgId));
      navigate('/review-write');
    }
  };

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;
  if (!myRecruits) return <div className="flex justify-center items-center h-[50vh]">봉사 내역이 없습니다.</div>;

  return (
    <div className="container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-6">나의 봉사내역</h1>
      <RecruitList 
        recruits={myRecruits}
        onRecruitClick={handleRecruitClick}
      />
    </div>
  );
}; 