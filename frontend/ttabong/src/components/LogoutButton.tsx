import { useNavigate } from 'react-router-dom';
import { useUserStore } from '@/stores/userStore';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';

export const LogoutButton = () => {
  const navigate = useNavigate();
  const { logout } = useUserStore();

  const handleLogout = async () => {
    try {
      await logout();
      toast.success('로그아웃되었습니다');
      navigate('/login');
    } catch (error) {
      toast.error('로그아웃 중 오류가 발생했습니다');
    }
  };

  return (
    <div className="pt-6 border-t">
      <Button 
        variant="destructive"
        className="w-full"
        onClick={handleLogout}
      >
        로그아웃
      </Button>
    </div>
  );
}; 