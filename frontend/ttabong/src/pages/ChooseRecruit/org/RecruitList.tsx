import React from 'react';
import { RecruitCard } from './RecruitCard';
import type { OrgRecruit } from '@/types/recruitType';

interface RecruitListProps {
  recruits: OrgRecruit[];
  onDelete: () => void;
}

export const RecruitList: React.FC<RecruitListProps> = ({ recruits, onDelete }) => {
  return (
    <div className="space-y-4">
      {recruits.map((recruit) => (
        <RecruitCard 
          key={recruit.recruit.recruitId}
          recruit={recruit}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
}; 