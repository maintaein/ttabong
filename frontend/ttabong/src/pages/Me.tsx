import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
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

const Me = () => {
  const navigate = useNavigate();
  const [organization, setOrganization] = useState<Organization | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
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
      setIsLoading(false);
    }, 500);
    
    return () => clearTimeout(timer);
  }, []);

  if (isLoading) return <div className="p-4 text-center">로딩 중...</div>;
  if (error) return <div className="p-4 text-red-500">{error}</div>;
  if (!organization) return null;

  return (
    <div className="flex flex-col h-full">
      {/* 기존 컨텐츠 영역 */}
      <div className="flex-1 bg-gray-50/50 p-4 mb-24">
        <div>
          {/* 기관 정보 섹션 */}
          <div className="flex items-center gap-4 p-4 bg-white rounded-md shadow-md">
            <img src={organization.avatarUrl} alt="기관 로고" className="w-14 h-14 rounded-full" />
            <div>
              <h2 className="text-lg font-semibold">{organization.name}</h2>
              <p className="text-sm text-gray-500">사업자등록번호: {organization.businessRegNumber}</p>
              <p className="text-sm text-gray-500">대표자: {organization.representativeName}</p>
              <p className="text-sm text-gray-500">주소: {organization.address}</p>
              <p className="text-sm text-gray-500">이메일: {organization.email}</p>
              <p className="text-sm text-gray-500">가입일: {organization.createdAt}</p>
            </div>
          </div>

          {/* 탭 메뉴 */}
          <Tabs defaultValue="gallery" className="w-full mt-4">
            <TabsList className="grid w-full grid-cols-2 h-auto p-1 bg-muted/50">
              <TabsTrigger value="info" className="py-2.5">기관 정보</TabsTrigger>
              <TabsTrigger value="gallery" className="py-2.5">아름다운 갤러리</TabsTrigger>
            </TabsList>

            <TabsContent value="info">
              <div className="p-4">여기에 기관 정보 상세 내용을 추가할 수 있음.</div>
            </TabsContent>

            <TabsContent value="gallery">
              <div className="grid grid-cols-3 gap-2 p-4">
                {[...Array(6)].map((_, index) => (
                  <div key={index} className="bg-white p-3 shadow rounded-md aspect-square flex items-center justify-center">
                    <p className="text-gray-500">이미지 {index + 1}</p>
                  </div>
                ))}
              </div>
            </TabsContent>
          </Tabs>
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed inset-x-0 bottom-[72px] mx-4">
        <div className="max-w-[500px] mx-auto w-full bg-white p-4 border rounded-lg shadow-md">
          <div className="flex gap-4">
            <Button 
              className="flex-1 py-4 text-lg"
              onClick={() => navigate("/choose-recruit")}
            >
              공고 관리
            </Button>
            <Button 
              className="flex-1 py-4 text-lg"
              onClick={() => navigate("/template-and-group")}
            >
              템플릿 관리
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Me;
