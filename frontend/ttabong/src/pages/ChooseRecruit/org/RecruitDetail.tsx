import React, { useEffect, useState } from 'react';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card } from '@/components/ui/card';
import type { OrgRecruit, OrgRecruitStatus, VolunteerApplicationStatus } from '@/types/recruitType';
import { recruitApi } from '@/api/recruitApi';
import { useToast } from "@/hooks/use-toast";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { useUserStore } from '@/stores/userStore';
import { useRecruitStore } from '@/stores/recruitStore';

const formatTime = (time: number) => {
  const hours = Math.floor(time);
  const minutes = Math.round((time - hours) * 60);
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
};

const STATUS_MAP = {
  RECRUITING: '모집중',
  RECRUITMENT_CLOSED: '모집마감',
  ACTIVITY_COMPLETED: '활동완료'
} as const;

const RecruitDetail: React.FC = () => {
  const navigate = useNavigate();
  const { recruitId } = useParams();
  const location = useLocation();
  const [recruit, setRecruit] = useState<OrgRecruit | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [currentImageIndex] = useState(0);
  const { userType } = useUserStore();
  const { applyRecruit, cancelApplication } = useRecruitStore();
  const { toast } = useToast();

  const slideVariants = {
    enter: (direction: number) => ({
      x: direction > 0 ? 1000 : -1000,
      opacity: 0
    }),
    center: {
      zIndex: 1,
      x: 0,
      opacity: 1
    },
    exit: (direction: number) => ({
      zIndex: 0,
      x: direction < 0 ? 1000 : -1000,
      opacity: 0
    })
  };

  const swipeConfidenceThreshold = 10000;
  const swipePower = (offset: number, velocity: number) => {
    return Math.abs(offset) * velocity;
  };

  const [[page, direction], setPage] = useState([0, 0]);

  const handlePrevImage = () => {
    setPage([page - 1, -1]);
  };

  const handleNextImage = () => {
    setPage([page + 1, 1]);
  };

  useEffect(() => {
    const fetchRecruitDetail = async () => {
      try {
        if (location.state?.recruit) {
          setRecruit(location.state.recruit);
          setIsLoading(false);
          return;
        }
        
        const data = await recruitApi.getRecruitDetail(Number(recruitId));
        const transformedData: OrgRecruit = {
          group: data.group,
          template: {
            ...data.template,
            volunteerTypes: [],
            volunteerField: []
          },
          recruit: {
            ...data.recruit,
            status: data.recruit.status === '모집중' ? 'RECRUITING' : 
                   data.recruit.status === '모집마감' ? 'RECRUITMENT_CLOSED' : 
                   data.recruit.status as OrgRecruitStatus
          },
          application: data.application ? {
            applicationId: data.application.applicationId,
            status: data.application.status === '승인 대기' ? 'PENDING' :
                    data.application.status as VolunteerApplicationStatus
          } : undefined,
          organization: {
            orgId: data.organization.orgId,
            name: data.organization.name || data.organization.orgName || ''
          }
        };
        setRecruit(transformedData);
      } catch (error) {
        console.error('공고 상세 조회 실패:', error);
        toast({
          variant: "destructive",
          title: "오류",
          description: "공고 정보를 불러오는데 실패했습니다"
        });
      } finally {
        setIsLoading(false);
      }
    };

    if (recruitId) {
      fetchRecruitDetail();
    }
  }, [recruitId, location.state, toast]);

  const handleApply = async () => {
    try {
      if (!recruitId) return;
      await applyRecruit(Number(recruitId));
      const updatedDetail = useRecruitStore.getState().recruitDetail;
      if (updatedDetail) {
        const transformedData: OrgRecruit = {
          ...updatedDetail,
          template: {
            ...updatedDetail.template,
            volunteerTypes: [],
            volunteerField: []
          },
          recruit: {
            ...updatedDetail.recruit,
            status: updatedDetail.recruit.status as OrgRecruitStatus
          },
          application: updatedDetail.application ? {
            applicationId: updatedDetail.application.applicationId,
            status: updatedDetail.application.status as VolunteerApplicationStatus
          } : undefined
        };
        setRecruit(transformedData);
      }
      toast({
        title: "신청 완료",
        description: "봉사 신청이 완료되었습니다."
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "오류",
        description: "봉사 신청에 실패했습니다."
      });
    }
  };

  const handleCancel = async () => {
    try {
      if (!recruit?.application?.applicationId || !recruitId) return;
      await cancelApplication(recruit.application.applicationId);
      
      // 취소 후 상세 정보 다시 불러오기
      const data = await recruitApi.getRecruitDetail(Number(recruitId));
      const transformedData: OrgRecruit = {
        group: data.group,
        template: {
          ...data.template,
          volunteerTypes: [],
          volunteerField: []
        },
        recruit: {
          ...data.recruit,
          status: data.recruit.status as OrgRecruitStatus
        },
        application: data.application ? {
          applicationId: data.application.applicationId,
          status: data.application.status as VolunteerApplicationStatus
        } : undefined,
        organization: data.organization
      };
      setRecruit(transformedData);
      
      toast({
        title: "신청 취소",
        description: "봉사 신청이 취소되었습니다."
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "오류",
        description: "봉사 신청 취소에 실패했습니다."
      });
    }
  };

  const displayStatus = recruit?.recruit.status ? STATUS_MAP[recruit.recruit.status] : '';

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-6 max-w-3xl">
        <div className="flex justify-between items-center mb-6">
          <Button variant="ghost" onClick={() => navigate(-1)}>
            ← 뒤로가기
          </Button>
        </div>
        <div>로딩 중...</div>
      </div>
    );
  }
  console.log(recruit);
  console.log(recruit?.recruit?.status);
  console.log(recruit?.template);
  console.log(recruit?.group);
  console.log(recruitId);

  if (!recruit?.recruit?.status || !recruit?.template || !recruit?.group || !recruitId) {
    return (
      <div className="container mx-auto px-4 py-6 max-w-3xl">
        <div className="flex justify-between items-center mb-6">
          <Button variant="ghost" onClick={() => navigate(-1)}>
            ← 뒤로가기
          </Button>
        </div>
        <div>잘못된 접근입니다.</div>
      </div>
    );
  }

  return (
    <div className="container max-w-2xl mx-auto px-4 py-4 space-y-4">
      {/* 상단 헤더 */}
      <div className="flex justify-between items-center mb-6">
        <Button 
          variant="ghost" 
          onClick={() => navigate(-1)}
        >
          ← 뒤로가기
        </Button>
        <Badge>{displayStatus}</Badge>
      </div>

      {/* 메인 컨텐츠 */}
      <div className="space-y-6">
        {/* 사진 영역 */}
        {recruit.template.images && recruit.template.images.length > 0 ? (
          <Card className="p-6 overflow-hidden relative">
            <div className="relative h-[200px] overflow-hidden">
              <AnimatePresence initial={false} custom={direction}>
                <motion.img
                  key={page}
                  src={recruit.template.images[Math.abs(page % recruit.template.images.length)]}
                  custom={direction}
                  variants={slideVariants}
                  initial="enter"
                  animate="center"
                  exit="exit"
                  transition={{
                    x: { type: "spring", stiffness: 300, damping: 30 },
                    opacity: { duration: 0.2 }
                  }}
                  drag="x"
                  dragConstraints={{ left: 0, right: 0 }}
                  dragElastic={1}
                  onDragEnd={(_, info) => {
                    const swipe = swipePower(info.offset.x, info.velocity.x);
                    if (swipe < -swipeConfidenceThreshold) {
                      handleNextImage();
                    } else if (swipe > swipeConfidenceThreshold) {
                      handlePrevImage();
                    }
                  }}
                  className="absolute w-full h-full object-cover rounded-lg"
                />
              </AnimatePresence>
            </div>
            {recruit.template.images.length > 1 && (
              <>
                <Button
                  variant="ghost"
                  size="icon"
                  className="absolute left-8 top-1/2 transform -translate-y-1/2"
                  onClick={handlePrevImage}
                >
                  <ChevronLeft className="h-6 w-6" />
                </Button>
                <Button
                  variant="ghost"
                  size="icon"
                  className="absolute right-8 top-1/2 transform -translate-y-1/2"
                  onClick={handleNextImage}
                >
                  <ChevronRight className="h-6 w-6" />
                </Button>
                <div className="absolute bottom-8 left-1/2 transform -translate-x-1/2 flex gap-2">
                  {recruit.template.images.map((_, index) => (
                    <div
                      key={index}
                      className={`h-2 w-2 rounded-full ${
                        index === currentImageIndex ? 'bg-primary' : 'bg-gray-300'
                      }`}
                    />
                  ))}
                </div>
              </>
            )}
          </Card>
        ) : (
          <Card className="p-6 bg-gray-50">
            <div className="h-[200px] flex items-center justify-center text-gray-400">
              등록된 사진이 없습니다
            </div>
          </Card>
        )}

        {/* 기본 정보 */}
        <Card className="p-6">
          <h1 className="text-2xl font-bold mb-2">{recruit.template.title}</h1>
          <p className="text-gray-600 mb-4">{recruit.group.groupName}</p>
          
          <div className="grid grid-cols-2 gap-6 text-sm">
            <div>
              <h3 className="font-semibold">봉사 일시</h3>
              <p>{recruit.recruit.activityDate}</p>
              <p>{formatTime(recruit.recruit.activityStart)} ~ {formatTime(recruit.recruit.activityEnd)}</p>
            </div>
            <div>
              <h3 className="font-semibold">모집 현황</h3>
              <p>{recruit.recruit.participateVolCount} / {recruit.recruit.maxVolunteer}명</p>
            </div>
            <div>
              <h3 className="font-semibold">모집 마감일</h3>
              <p>{new Date(recruit.recruit.deadline).toLocaleDateString()}</p>
            </div>
            <div>
              <h3 className="font-semibold">봉사 분야</h3>
              <p>{recruit.template.volunteerField?.join(', ') || '미지정'}</p>
            </div>
          </div>
        </Card>
        {/* 상세 정보 */}
        <Card className="p-6">
          <h2 className="text-xl font-semibold mb-4">봉사 상세 정보</h2>
          <div className="space-y-4">
            <div>
              <h3 className="font-semibold">봉사 내용</h3>
              <p className="whitespace-pre-wrap mt-2 text-gray-600">{recruit.template.description}</p>
            </div>
            <div>
              <h3 className="font-semibold">봉사 장소</h3>
              <p className="mt-2 text-gray-600">
                {recruit.template.activityLocation === '재택'
                  ? '재택 근무' 
                  : recruit.template.activityLocation}
              </p>
            </div>
            <div>
              <h3 className="font-semibold">봉사자 유형</h3>
              <div className="flex gap-2 flex-wrap mt-2">
                {recruit.template.volunteerTypes?.map((type) => (
                  <Badge key={type} variant="outline">{type}</Badge>
                ))}
              </div>
            </div>
          </div>
        </Card>

        {/* 담당자 정보 */}
        <Card className="p-6">
          <h2 className="text-xl font-semibold mb-4">담당자 정보</h2>
          <div className="space-y-3">
            <div>
              <h3 className="font-semibold mb-1">담당자</h3>
              <p className="text-gray-600">{recruit.template.contactName}</p>
            </div>
            <div>
              <h3 className="font-semibold mb-1">연락처</h3>
              <p className="text-gray-600">{recruit.template.contactPhone}</p>
            </div>
          </div>
        </Card>
      </div>

      {userType === 'volunteer' && (
        <div className="mt-6">
          {recruit?.application?.status === 'PENDING' ? (
            <Button 
              variant="destructive" 
              className="w-full"
              onClick={handleCancel}
            >
              신청 취소하기
            </Button>
          ) : (
            <Button 
              className="w-full"
              onClick={handleApply}
              disabled={
                recruit?.recruit.status !== 'RECRUITING' ||
                recruit?.recruit.participateVolCount >= recruit?.recruit.maxVolunteer
              }
            >
              {recruit?.recruit.status !== 'RECRUITING'
                ? STATUS_MAP.RECRUITMENT_CLOSED
                : recruit?.recruit.participateVolCount >= recruit?.recruit.maxVolunteer
                  ? '정원 마감'
                  : '신청하기'}
            </Button>
          )}
        </div>
      )}

      {recruit?.recruit.status === 'ACTIVITY_COMPLETED' && (
        <div className="fixed bottom-0 left-0 right-0 p-4 bg-background border-t">
          <div className="container max-w-2xl mx-auto">
            <Button 
              className="w-full"
              onClick={() => navigate('/review-write', {
                state: { 
                  recruitId: recruit.recruit.recruitId,
                  isOrgReview: true
                }
              })}
            >
              기관 후기 작성
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};

export default RecruitDetail; 