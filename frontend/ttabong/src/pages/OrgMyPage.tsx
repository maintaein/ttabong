import React from 'react';
import { useUserStore } from '@/stores/userStore';
import { LogoutButton } from '@/components/LogoutButton';

const OrgMyPage: React.FC = () => {
  const { userId } = useUserStore();

  if (!userId) return null;

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">기관 정보</h1>
      <div className="space-y-6">
        <div className="space-y-4">
          <p>기관 ID: {userId}</p>
        </div>
        <LogoutButton />
      </div>
    </div>
  );
};

export default OrgMyPage; 