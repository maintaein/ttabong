import React, { useEffect, useState } from 'react';
import { RecruitList } from './RecruitList';
import { useRecruitStore } from '@/stores/recruitStore';

export const OrgView: React.FC = () => {
  const { orgRecruits, isLoading, error, fetchOrgRecruits } = useRecruitStore();
  const [selectedStatus, setSelectedStatus] = useState<string>('모집중');

  useEffect(() => {
    fetchOrgRecruits();
  }, [fetchOrgRecruits]);

  const filteredRecruits = orgRecruits?.filter(
    recruit => recruit.recruit.status === selectedStatus
  ) || [];

  if (isLoading) return <div className="flex justify-center items-center h-[50vh]">로딩 중...</div>;
  if (error) return <div className="flex justify-center items-center h-[50vh] text-destructive">{error}</div>;

  return (
    <div className="container mx-auto px-4 py-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">봉사 공고 목록</h1>
        <div className="space-x-2">
          {['모집중', '모집마감', '활동완료'].map(status => (
            <button
              key={status}
              onClick={() => setSelectedStatus(status)}
              className={`px-4 py-2 rounded ${
                selectedStatus === status 
                  ? 'bg-blue-500 text-white' 
                  : 'bg-gray-100'
              }`}
            >
              {status}
            </button>
          ))}
        </div>
      </div>
      <RecruitList 
        recruits={filteredRecruits}
        onDelete={fetchOrgRecruits}
      />
    </div>
  );
}; 