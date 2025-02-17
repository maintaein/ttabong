import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { ScrollArea } from '@/components/ui/scroll-area';
import { Card, CardContent } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Building2, ClipboardList, PlusCircle } from 'lucide-react';
import { LogoutButton } from '@/components/LogoutButton';

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

  if (isOrgLoading) return <div className="p-4 text-center">로딩 중...</div>;
  if (error) return <div className="p-4 text-red-500">{error}</div>;
  if (!organization) return null;

  return (
    <div className="h-screen overflow-y-auto">
      <div className="flex items-center justify-center p-4">
        <div className="w-full max-w-md space-y-6">
          {/* 프로필 섹션 */}
          <div className="space-y-2 text-center">
            <Avatar className="h-24 w-24 mx-auto border-4 border-background shadow-lg">
              <AvatarImage src={organization.avatarUrl} alt="기관 프로필" />
              <AvatarFallback className="bg-primary/5">
                <Building2 className="h-12 w-12 text-primary/70" />
              </AvatarFallback>
            </Avatar>
            <h1 className="text-2xl font-semibold tracking-tight mt-4">{organization.name}</h1>
            <p className="text-sm text-muted-foreground">{organization.email}</p>
            <Badge variant="secondary" className="mt-1">기관 회원</Badge>
          </div>

          {/* 봉사 관리 섹션 */}
          <Card className="border shadow-sm">
            <CardContent className="p-6 space-y-4">
              <h2 className="text-lg font-semibold tracking-tight">봉사 관리</h2>
              <div className="grid grid-cols-2 gap-3">
                <Button 
                  variant="outline" 
                  className="h-auto py-6 hover:bg-primary/5"
                  onClick={() => navigate('/add-recruit')}
                >
                  <div className="flex flex-col items-center gap-2">
                    <ClipboardList className="h-5 w-5 text-primary" />
                    <span className="text-sm font-medium">내가 등록한 공고</span>
                  </div>
                </Button>
                <Button 
                  variant="outline" 
                  className="h-auto py-6 hover:bg-primary/5"
                  onClick={() => navigate('/main')}
                >
                  <div className="flex flex-col items-center gap-2">
                    <PlusCircle className="h-5 w-5 text-primary" />
                    <span className="text-sm font-medium">공고 작성</span>
                  </div>
                </Button>
              </div>
            </CardContent>
          </Card>

          {/* 아름다운 갤러리 섹션 */}
          <Card className="border shadow-sm">
            <CardContent className="p-4">
              <div className="flex justify-between items-center mb-3">
                <h2 className="text-lg font-semibold tracking-tight">아름다운 갤러리</h2>
              </div>
              <ScrollArea className="h-[calc(100vh-480px)] min-h-[280px] -mx-2 px-2">
                <div className="grid grid-cols-2 gap-2">
                  {/* 갤러리 이미지들 */}
                  {Array.from({ length: 6 }).map((_, index) => (
                    <div 
                      key={index}
                      className="aspect-square rounded-md overflow-hidden"
                    >
                      <img 
                        src={`/images/gallery-${index + 1}.jpg`} 
                        alt={`갤러리 이미지 ${index + 1}`}
                        className="w-full h-full object-cover"
                      />
                    </div>
                  ))}
                </div>
              </ScrollArea>
            </CardContent>
          </Card>

          <LogoutButton className="w-full h-11" />
        </div>
      </div>
    </div>
  );
};

export default OrgMyPage;
