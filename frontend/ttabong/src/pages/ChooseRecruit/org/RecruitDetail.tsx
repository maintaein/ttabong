import React, { useEffect, useState } from 'react';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import type { OrgRecruit, OrgRecruitStatus, VolunteerApplicationStatus } from '@/types/recruitType';
import { recruitApi } from '@/api/recruitApi';
import { ChevronLeft, ChevronRight } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { useRecruitStore } from '@/stores/recruitStore';
import { useToast } from "@/hooks/use-toast";
import { useUserStore } from '@/stores/userStore';

const convertTimeToString = (time: number) => {
  const hours = Math.floor(time);
  const minutes = Math.round((time - hours) * 60);
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
};

const convertStringToTime = (timeString: string) => {
  const [hours, minutes] = timeString.split(':').map(Number);
  return hours + (minutes / 60);
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
  const { recruitDetail, isLoading, error, fetchRecruitDetail } = useRecruitStore();
  const [currentImageIndex] = useState(0);
  const [[page, direction], setPage] = useState([0, 0]);
  const [isEditing, setIsEditing] = useState(location.state?.isEditing || false);
  const [formData, setFormData] = useState({
    deadline: '',
    activityDate: '',
    activityStart: 0,
    activityEnd: 0,
    maxVolunteer: 0,
    title: '',
    description: '',
    activityLocation: '',
    volunteerTypes: [] as string[],
    volunteerField: [] as string[],
    contactName: '',
    contactPhone: '',
    images: [] as string[],
    imageCount: 0
  });
  const [recruit, setRecruit] = useState<OrgRecruit | null>(null);
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
          return;
        }
        
        const data = await recruitApi.getRecruitDetail(Number(recruitId), userType || 'volunteer');
        const transformedData: OrgRecruit = {
          group: data.group,
          template: {
            ...data.template,
            volunteerTypes: data.template.volunteerTypes || [],
            volunteerField: data.template.volunteerField || []
          },
          recruit: {
            ...data.recruit,
            status: data.recruit.status as OrgRecruitStatus
          },
          application: data.application ? {
            applicationId: data.application.applicationId,
            status: data.application.status as VolunteerApplicationStatus
          } : undefined,
          organization: {
            orgId: data.organization.orgId,
            name: data.organization.name
          }
        };
        setRecruit(transformedData);
      } catch (error: any) {
        console.error('공고 상세 조회 실패:', error);
        toast({
          variant: "destructive",
          title: "오류",
          description: error.message || "공고 정보를 불러오는데 실패했습니다"
        });
      }
    };

    if (recruitId) {
      fetchRecruitDetail();
    }
  }, [recruitId, location.state, toast, userType]);

  useEffect(() => {
    if (recruitDetail) {
      setFormData({
        deadline: recruitDetail.recruit.deadline,
        activityDate: recruitDetail.recruit.activityDate,
        activityStart: recruitDetail.recruit.activityStart,
        activityEnd: recruitDetail.recruit.activityEnd,
        maxVolunteer: recruitDetail.recruit.maxVolunteer,
        title: recruitDetail.template.title,
        description: recruitDetail.template.description,
        activityLocation: recruitDetail.template.activityLocation,
        volunteerTypes: recruitDetail.template.volunteerTypes || [],
        volunteerField: recruitDetail.template.volunteerField || [],
        contactName: recruitDetail.template.contactName,
        contactPhone: recruitDetail.template.contactPhone,
        images: recruitDetail.template.images,
        imageCount: recruitDetail.template.images.length
      });
    }
  }, [recruitDetail]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await recruitApi.updateRecruit(Number(recruitId), {
        ...formData,
        recruitId: Number(recruitId)
      });
      toast({
        title: "성공",
        description: "공고가 수정되었습니다."
      });
      setIsEditing(false);
      fetchRecruitDetail(Number(recruitId), userType || 'volunteer');
    } catch (error) {
      console.error('공고 수정 실패:', error);
      toast({
        variant: "destructive",
        title: "오류",
        description: "공고 수정에 실패했습니다."
      });
    }
  };

  const handleApply = async () => {
    if (!recruit) return;
    
    try {
      await applyRecruit(recruit.recruit.recruitId);
      // API로 최신 데이터 다시 가져오기
      const data = await recruitApi.getRecruitDetail(Number(recruitId), userType || 'volunteer');
      const transformedData: OrgRecruit = {
        group: data.group,
        template: {
          ...data.template,
          volunteerTypes: data.template.volunteerTypes || [],
          volunteerField: data.template.volunteerField || []
        },
        recruit: {
          ...data.recruit,
          status: data.recruit.status as OrgRecruitStatus
        },
        application: data.application ? {
          applicationId: data.application.applicationId,
          status: data.application.status as VolunteerApplicationStatus
        } : undefined,
        organization: {
          orgId: data.organization.orgId,
          name: data.organization.name
        }
      };
      setRecruit(transformedData);
      
      toast({
        title: "신청 완료",
        description: "봉사 신청이 완료되었습니다."
      }); 
    } catch (error: any) {
      toast({
        variant: "destructive",
        title: "오류",
        description: error.message || "봉사 신청에 실패했습니다."
      });
    }
  };

  const handleCancel = async () => {
    if (!recruit?.application?.applicationId) return;
    
    try {
      await cancelApplication(recruit.application.applicationId);

      const data = await recruitApi.getRecruitDetail(Number(recruitId), userType || 'volunteer');
      const transformedData: OrgRecruit = {
        group: data.group,
        template: {
          ...data.template,
          volunteerTypes: data.template.volunteerTypes || [],
          volunteerField: data.template.volunteerField || []
        },
        recruit: {
          ...data.recruit,
          status: data.recruit.status as OrgRecruitStatus
        },
        application: data.application ? {
          applicationId: data.application.applicationId,
          status: data.application.status as VolunteerApplicationStatus
        } : undefined,
        organization: {
          orgId: data.organization.orgId,
          name: data.organization.name
        }
      };
      setRecruit(transformedData);
      toast({
        title: "취소 완료",
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


  if (isLoading) return <div>로딩 중...</div>;
  if (error) return <div>{error}</div>;
  if (!recruit) return null;

  const { template, recruit: recruitData, organization } = recruit;

  return (
    <div className="container max-w-2xl mx-auto px-4 py-4 space-y-4">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">{recruitDetail?.template.title}</h1>
      </div>

      {isEditing ? (
        <form onSubmit={handleSubmit} className="space-y-6">
          <Card className="p-6">
            <h2 className="text-xl font-semibold mb-4">기본 정보</h2>
            <div className="space-y-4">
              <div className="space-y-2">
                <label className="text-sm font-medium">마감일</label>
                <Input
                  type="datetime-local"
                  value={formData.deadline.slice(0, 16)}
                  onChange={(e) => setFormData(prev => ({
                    ...prev,
                    deadline: e.target.value
                  }))}
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">활동일</label>
                <Input
                  type="date"
                  value={formData.activityDate}
                  onChange={(e) => setFormData(prev => ({
                    ...prev,
                    activityDate: e.target.value
                  }))}
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <label className="text-sm font-medium">시작 시간</label>
                  <Input
                    type="time"
                    step="600"
                    value={convertTimeToString(formData.activityStart)}
                    onChange={(e) => setFormData(prev => ({
                      ...prev,
                      activityStart: convertStringToTime(e.target.value)
                    }))}
                  />
                </div>
                <div className="space-y-2">
                  <label className="text-sm font-medium">종료 시간</label>
                  <Input
                    type="time"
                    step="600"
                    value={convertTimeToString(formData.activityEnd)}
                    onChange={(e) => setFormData(prev => ({
                      ...prev,
                      activityEnd: convertStringToTime(e.target.value)
                    }))}
                  />
                </div>
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">최대 인원</label>
                <Input
                  type="number"
                  min="1"
                  value={formData.maxVolunteer}
                  onChange={(e) => setFormData(prev => ({
                    ...prev,
                    maxVolunteer: Number(e.target.value)
                  }))}
                />
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <h2 className="text-xl font-semibold mb-4">상세 정보</h2>
            <div className="space-y-4">
              <div className="space-y-2">
                <label className="text-sm font-medium">제목</label>
                <Input
                  value={formData.title}
                  onChange={(e) => setFormData(prev => ({
                    ...prev,
                    title: e.target.value
                  }))}
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">봉사 내용</label>
                <Textarea
                  value={formData.description}
                  onChange={(e) => setFormData(prev => ({
                    ...prev,
                    description: e.target.value
                  }))}
                  rows={4}
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">봉사 장소</label>
                <Input
                  value={formData.activityLocation}
                  onChange={(e) => setFormData(prev => ({
                    ...prev,
                    activityLocation: e.target.value
                  }))}
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">봉사자 유형</label>
                {/* 봉사자 유형 선택 UI */}
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">봉사 분야</label>
                {/* 봉사 분야 선택 UI */}
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <h2 className="text-xl font-semibold mb-4">담당자 정보</h2>
            <div className="space-y-4">
              <div className="space-y-2">
                <label className="text-sm font-medium">담당자</label>
                <Input
                  value={formData.contactName}
                  onChange={(e) => setFormData(prev => ({
                    ...prev,
                    contactName: e.target.value
                  }))}
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">연락처</label>
                <Input
                  value={formData.contactPhone}
                  onChange={(e) => setFormData(prev => ({
                    ...prev,
                    contactPhone: e.target.value
                  }))}
                />
              </div>
            </div>
          </Card>

          <div className="sticky bottom-0 p-4 bg-background border-t">
            <div className="container max-w-2xl mx-auto flex gap-2">
              <Button 
                variant="outline" 
                className="flex-1"
                onClick={() => setIsEditing(false)}
              >
                취소
              </Button>
              <Button type="submit" className="flex-1">
                수정 완료
              </Button>
            </div>
          </div>
        </form>
      ) : (
        <div className="space-y-6">
          {/* 상단 헤더 */}
          <div className="flex justify-between items-center mb-6">
            <Button 
              variant="ghost" 
              onClick={() => navigate(-1)}
            >
              ← 뒤로가기
            </Button>
            <Badge>{recruitData.status}</Badge>
          </div>

          {/* 메인 컨텐츠 */}
          <div className="space-y-6">
            {/* 사진 영역 */}
            {template.images && template.images.length > 0 ? (
              <Card className="p-6 overflow-hidden relative">
                <div className="relative h-[200px] overflow-hidden">
                  <AnimatePresence initial={false} custom={direction}>
                    <motion.img
                      key={page}
                      src={template.images[Math.abs(page % template.images.length)]}
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
                {template.images.length > 1 && (
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
                      {template.images.map((_, index) => (
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
              <h1 className="text-2xl font-bold mb-2">{template.title}</h1>
              <p className="text-gray-600 mb-4">{organization.name}</p>
              
              <div className="grid grid-cols-2 gap-6 text-sm">
                <div>
                  <h3 className="font-semibold">봉사 일시</h3>
                  <p>{recruitData.activityDate}</p>
                  <p>{convertTimeToString(recruitData.activityStart)} ~ {convertTimeToString(recruitData.activityEnd)}</p>
                </div>
                <div>
                  <h3 className="font-semibold">모집 현황</h3>
                  <p>{recruitData.participateVolCount} / {recruitData.maxVolunteer}명</p>
                </div>
                <div>
                  <h3 className="font-semibold">모집 마감일</h3>
                  <p>{new Date(recruitData.deadline).toLocaleDateString()}</p>
                </div>
                <div>
                  <h3 className="font-semibold">봉사 분야</h3>
                  <p>{template.volunteerField?.join(', ') || '미지정'}</p>
                </div>
              </div>
            </Card>
            {/* 상세 정보 */}
            <Card className="p-6">
              <h2 className="text-xl font-semibold mb-4">봉사 상세 정보</h2>
              <div className="space-y-4">
                <div>
                  <h3 className="font-semibold">봉사 내용</h3>
                  <p className="whitespace-pre-wrap mt-2 text-gray-600">{template.description}</p>
                </div>
                <div>
                  <h3 className="font-semibold">봉사 장소</h3>
                  <p className="mt-2 text-gray-600">
                    {template.activityLocation === '재택'
                      ? '재택 근무' 
                      : template.activityLocation}
                  </p>
                </div>
                <div>
                  <h3 className="font-semibold">봉사자 유형</h3>
                  <div className="flex gap-2 flex-wrap mt-2">
                    {template.volunteerTypes?.map((type) => (
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
                  <p className="text-gray-600">{template.contactName}</p>
                </div>
                <div>
                  <h3 className="font-semibold mb-1">연락처</h3>
                  <p className="text-gray-600">{template.contactPhone}</p>
                </div>
              </div>
            </Card>
          </div>

          {userType === 'volunteer' && (
            <div className="mt-6">
              {recruit?.application ? (
                <Button 
                  variant="destructive" 
                  className="w-full"
                  onClick={handleCancel}
                  disabled={recruit.application.status !== 'PENDING'}
                >
                  {recruit.application.status === 'PENDING' ? '신청 취소하기' : '취소 불가'}
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
                    ? STATUS_MAP[recruit.recruit.status]
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
      )}
    </div>
  );
};

export default RecruitDetail; 