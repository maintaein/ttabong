import React from 'react';
import { OrgView } from './ChooseRecruit/org/OrgView';
import { useNavigate } from 'react-router-dom';

const AddRecruit: React.FC = () => {
  const navigate = useNavigate();

  const handleRecruitClick = (recruitId: number) => {
    navigate(`/org/recruits/${recruitId}`);  // 상세 페이지로 이동
  };

  return <OrgView onRecruitClick={handleRecruitClick} />;
};

export default AddRecruit; 