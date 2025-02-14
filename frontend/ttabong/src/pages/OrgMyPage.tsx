import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
import { useUserStore } from '@/stores/userStore';
import { toast } from 'react-hot-toast';
import type { ApiError } from '@/api/axiosInstance';
import { MoreVertical } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

// 기관 데이터 타입 정의
interface Organization {
  businessRegNumber: string;
  name: string;
  representativeName: string;
  address: string;
  email: string;
  createdAt: string;
  avatarUrl?: string;
}

const OrgMyPage: React.FC = () => {
  const navigate = useNavigate();
  const { logout, isLoading: isLogoutLoading } = useUserStore();
  const [organization, setOrganization] = useState<Organization | null>(null);
  const [isOrgLoading, setIsOrgLoading] = useState<boolean>(true);
  const [error] = useState<string | null>(null);


  useEffect(() => {
    // 데이터 가져오기 (임시 더미 데이터) - 로딩 시간을 500ms로 단축
    const timer = setTimeout(() => {
      setOrganization({
        businessRegNumber: "123-45-67890",
        name: "행복나눔 봉사센터",
        representativeName: "김봉사",
        address: "서울특별시 강남구 봉사로 123",
        email: "contact@volunteer-center.org",
        createdAt: "2023-05-10",
        avatarUrl: "https://via.placeholder.com/150",
      });
      setIsOrgLoading(false);
    }, 500);
    
    return () => clearTimeout(timer);
  }, []);

  const handleLogout = async () => {
    try {
      await logout();
      toast.success('로그아웃되었습니다.');
      navigate('/login');
    } catch (error) {
      const apiError = error as ApiError;
      toast.error(apiError.message);
      console.error('로그아웃 실패:', error);
    }
  };

  if (isOrgLoading) return <div className="p-4 text-center">로딩 중...</div>;
  if (error) return <div className="p-4 text-red-500">{error}</div>;
  if (!organization) return null;

  return (
    <div className="flex flex-col h-full">
      {/* 기존 컨텐츠 영역 */}
      <div className="flex-1 bg-gray-50/50 p-4 mb-24">
        <div>
          {/* 기관 정보 섹션 */}
          <div className="p-4 bg-white rounded-md shadow-md">
            <div className="flex justify-between items-start">
              <div className="flex items-center gap-4">
                <img src={organization.avatarUrl} alt="기관 로고" className="w-14 h-14 rounded-full" />
                <div>
                  <h2 className="text-lg font-semibold">{organization.name}</h2>
                  <p className="text-sm text-gray-500">대표자: {organization.representativeName}</p>
                  <p className="text-sm text-gray-500">주소: {organization.address}</p>
                </div>
              </div>
              
              {/* 삼단바 메뉴 */}
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" size="sm">
                    <MoreVertical className="h-5 w-5" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-48">
                  <DropdownMenuItem onClick={() => navigate("/org-detail")}>
                    기관 상세 정보
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={() => navigate("/add-recruit")}>
                    공고 관리
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={() => navigate("/main")}>
                    템플릿 관리
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem 
                    className="text-red-500 focus:text-red-500" 
                    onClick={handleLogout}
                    disabled={isLogoutLoading}
                  >
                    {isLogoutLoading ? '로그아웃 중...' : '로그아웃'}
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          </div>

          {/* 탭 메뉴 */}
          <Tabs defaultValue="unwritten" className="w-full mt-4">
            <TabsList className="grid w-full grid-cols-2 h-auto p-1 bg-muted/50">
              <TabsTrigger value="unwritten" className="py-2.5">후기 미작성 봉사</TabsTrigger>
              <TabsTrigger value="gallery" className="py-2.5">아름다운 갤러리</TabsTrigger>
            </TabsList>

            <TabsContent value="unwritten">
              <div className="p-4 bg-white rounded-md">
                <div className="text-center text-gray-500">후기를 작성하지 않은 봉사가 없습니다.</div>
              </div>
            </TabsContent>

            <TabsContent value="gallery">
              <div className="grid grid-cols-3 gap-2 p-4 bg-white rounded-md">
                {[...Array(6)].map((_, index) => (
                  <div key={index} className="aspect-square bg-gray-100 rounded-md flex items-center justify-center">
                    <p className="text-gray-500">이미지 {index + 1}</p>
                  </div>
                ))}
              </div>
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </div>
  );
};

export default OrgMyPage;
