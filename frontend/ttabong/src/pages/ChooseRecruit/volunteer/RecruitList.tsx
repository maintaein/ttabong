import React from 'react';
import { RecruitCard } from './RecruitCard';
import type { Application } from '@/types/recruitType';

interface RecruitListProps {
  recruits: Application[];
  onRecruitClick: (application: Application) => void;
}

export const RecruitList: React.FC<RecruitListProps> = ({ recruits, onRecruitClick }) => {
  return (
    <div className="space-y-4">
      {Array.isArray(recruits) && recruits.map((application) => (
        <RecruitCard 
          key={application.applicationId}
          application={application}
          onClick={onRecruitClick}
        />
      ))}
    </div>
  );
}; 