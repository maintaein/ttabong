import React from 'react';

const OrgMainPage: React.FC = () => {
  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">봉사 공고 관리</h1>
      <div className="grid gap-4">
        {/* 기관의 봉사 공고 목록 */}
        <div className="bg-white rounded-lg shadow p-4">
          <h2 className="text-xl font-semibold mb-4">진행중인 봉사 공고</h2>
          {/* 공고 목록 컴포넌트 */}
        </div>
        
        <div className="bg-white rounded-lg shadow p-4">
          <h2 className="text-xl font-semibold mb-4">지원자 현황</h2>
          {/* 지원자 통계 컴포넌트 */}
        </div>
      </div>
    </div>
  );
};

export default OrgMainPage; 