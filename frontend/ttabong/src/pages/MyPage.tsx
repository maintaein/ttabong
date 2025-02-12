import { useUserStore } from '@/stores/userStore';
import { LogoutButton } from '@/components/LogoutButton';

export default function MyPage() {
  const { userId, userType } = useUserStore();  

  if (!userId) return null; 

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">내 정보</h1>
      {/* 사용자 정보 표시 */}
      <div className="space-y-6">
        <div className="space-y-4">
          <p>사용자 ID: {userId}</p>
          <p>사용자 유형: {userType === 'volunteer' ? '개인회원' : '기관회원'}</p>
        </div>
        <LogoutButton />
      </div>
    </div>
  );
}
