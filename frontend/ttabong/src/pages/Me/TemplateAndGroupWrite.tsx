import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Button } from "@/components/ui/button";
import Step0GroupSelection from "@/pages/Me/TemplateComponents/Step0GroupSelection";
import Step1AnnouncementDetails from "@/pages/Me/TemplateComponents/Step1AnnouncementDetails";
import Step2RecruitmentConditions from "@/pages/Me/TemplateComponents/Step2RecruitmentConditions";
import Step3VolunteerLocation from "@/pages/Me/TemplateComponents/Step3VolunteerLocation";
import Step4ContactInfo from "@/pages/Me/TemplateComponents/Step4ContactInfo";
import { motion, AnimatePresence } from "framer-motion";
import { TemplateFormData } from '@/types/template';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { useScroll } from '@/contexts/ScrollContext';
import { useTemplateStore } from '@/stores/templateStore';
import { templateApi } from '@/api/templateApi';
import { CreateTemplateRequest } from '@/types/template';
import { useToast } from "@/hooks/use-toast";

const steps = [
  "공고 내용 입력(1/2)",
  "공고 내용 입력(2/2)",
  "모집 조건 설정",
  "봉사지 정보 입력",
  "담당자 정보 입력"
];

const TemplateAndGroupWrite: React.FC = () => {
  const [step, setStep] = useState(0);
  const [isCompleted, setIsCompleted] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const templateId = location.state?.templateId;
  const { scrollToTop } = useScroll();
  const { createTemplate: createTemplateApi } = useTemplateStore();
  const { toast } = useToast();

  // 🔹 모든 step의 데이터를 하나의 state로 관리
  const [templateData, setTemplateData] = useState<TemplateFormData>({
    groupId: null,
    title: "",
    description: "",
    images: [],
    volunteerTypes: [],
    volunteerCount: 10,
    locationType: "",
    address: "",
    detailAddress: "",
    contactName: "",
    contactPhone: {
      areaCode: "010",
      middle: "",
      last: ""
    },
    template_id: Date.now(),
    created_at: new Date().toISOString().split('T')[0],
    startDate: null,
    endDate: null,
    volunteerDate: null,
    startTime: "",
    endTime: "",
    volunteerField: []
  });

  // 상태 추가
  const [showImageDialog, setShowImageDialog] = useState(false);
  const [imageFiles, setImageFiles] = useState<File[]>([]);

  useEffect(() => {
    if (isCompleted) {
      setTimeout(() => {
        navigate("/main");
      }, 2000);
    }
  }, [isCompleted, navigate]);

  // 초기 데이터 로드
  useEffect(() => {
    if (templateId) {
      const loadTemplate = async () => {
        try {
          const template = await templateApi.getTemplate(templateId);
          setTemplateData({
            ...templateData,
            groupId: template.groupId,
            title: template.title || "",
            description: template.description || "",
            images: template.images || [],
            volunteerTypes: Array.isArray(template.volunteerTypes) 
              ? template.volunteerTypes 
              : template.volunteerTypes?.split(", ") || [],
            volunteerCount: template.volunteerCount || 10,
            locationType: template.activityLocation === "재택" ? "재택" : "주소",
            address: template.activityLocation !== "재택" 
              ? template.activityLocation.split(" ").slice(0, -1).join(" ")
              : "",
            detailAddress: template.activityLocation !== "재택"
              ? template.activityLocation.split(" ").slice(-1)[0]
              : "",
            contactName: template.contactName || "",
            contactPhone: {
              areaCode: template.contactPhone?.split("-")[0] || "010",
              middle: template.contactPhone?.split("-")[1] || "",
              last: template.contactPhone?.split("-")[2] || ""
            },
            volunteerField: Array.isArray(template.volunteerField)
              ? template.volunteerField
              : template.volunteerField?.split(", ") || [],
            startDate: template.startDate ? new Date(template.startDate) : null,
            endDate: template.endDate ? new Date(template.endDate) : null,
            volunteerDate: template.volunteerDate ? new Date(template.volunteerDate) : null,
            startTime: template.startTime || "",
            endTime: template.endTime || ""
          });
        } catch (error) {
          console.error('템플릿 로드 실패:', error);
          toast({
            variant: "destructive",
            title: "오류",
            description: "템플릿을 불러오는데 실패했습니다."
          });
        }
      };
      loadTemplate();
    }
  }, [templateId]);

  // 템플릿 생성 및 저장 함수
  const createTemplate = async () => {
    try {
      // 1. Presigned URL 요청
      const presignedUrls = await templateApi.getPresignedUrls();
      console.log('Presigned URLs:', presignedUrls);
      
      // 2. 이미지 업로드 (presigned URL 사용)
      const uploadedImageUrls = await Promise.all(
        imageFiles.map(async (image, index) => {
          console.log('Uploading image:', image);
          console.log('Using presigned URL:', presignedUrls.images[index]);
          
          await fetch(presignedUrls.images[index], {
            method: 'PUT',
            body: image,
            headers: {
              'Content-Type': image.type || 'image/webp'
            }
          });
          
          const imageUrl = presignedUrls.images[index].split('?')[0];
          console.log('Generated image URL:', imageUrl);
          return imageUrl;
        })
      );
      
      console.log('Final uploaded image URLs:', uploadedImageUrls);

      // 3. 업로드된 이미지 URL로 templateData 업데이트
      const updatedTemplateData: CreateTemplateRequest = {
        groupId: templateData.groupId || 0,
        orgId: 5,
        categoryId: 3,
        title: templateData.title,
        activityLocation: templateData.locationType === "재택" ? "재택" : `${templateData.address} ${templateData.detailAddress}`.trim(),
        status: "ALL",
        images: uploadedImageUrls,
        imageCount: uploadedImageUrls.length,
        contactName: templateData.contactName,
        contactPhone: `${templateData.contactPhone.areaCode}-${templateData.contactPhone.middle}-${templateData.contactPhone.last}`,
        description: templateData.description,
        volunteerField: templateData.volunteerField,
        volunteerTypes: templateData.volunteerTypes,
        volunteerCount: templateData.volunteerCount
      };
      
      console.log('Final request data:', updatedTemplateData);

      // 4. 템플릿 생성
      await createTemplateApi(updatedTemplateData);
      toast({
        title: "템플릿과 공고가 생성되었습니다.",
        description: "2초 후 메인 페이지로 이동합니다."
      });
      setIsCompleted(true);
      setTimeout(() => {
        navigate('/main');
      }, 2000);
    } catch (error) {
      console.error('실패:', error);
      toast({
        title: "템플릿 생성에 실패했습니다.",
        description: "다시 시도해주세요."
      });
    }
  };

  const validateStep0 = () => {
    const errors: string[] = [];

    if (!templateData.groupId) {
      errors.push("공고 그룹을 선택해주세요.");
    }
    if (!templateData.startDate || !templateData.endDate) {
      errors.push("모집 기간을 설정해주세요.");
    }
    if (!templateData.volunteerDate) {
      errors.push("봉사일을 선택해주세요.");
    }
    if (!templateData.startTime || !templateData.endTime) {
      errors.push("봉사 시간을 설정해주세요.");
    }
    if (templateData.volunteerField.length === 0) {
      errors.push("봉사 분야를 하나 이상 선택해주세요.");
    }

    if (templateData.startDate && templateData.endDate) {
      if (templateData.startDate > templateData.endDate) {
        errors.push("모집 시작일이 종료일보다 늦을 수 없습니다.");
      }
      
      if (templateData.volunteerDate) {
        if (templateData.volunteerDate < templateData.endDate) {
          errors.push("봉사일은 모집 마감일과 같거나 이후여야 합니다.");
        }
      }
    }

    if (errors.length > 0) {
      errors.forEach(error => toast({
        variant: "destructive",
        title: "오류",
        description: error
      }));
      return false;
    }
    return true;
  };

  // Step1 검증 함수 추가
  const validateStep1 = () => {
    const errors: string[] = [];

    if (!templateData.title.trim()) {
      errors.push("공고 제목을 입력해주세요.");
    }
    if (!templateData.description.trim()) {
      errors.push("공고 내용을 입력해주세요.");
    }

    if (errors.length > 0) {
      errors.forEach(error => toast({
        variant: "destructive",
        title: "오류",
        description: error
      }));
      return false;
    }

    // 이미지가 없는 경우 다이얼로그 표시
    if (templateData.images.length === 0) {
      setShowImageDialog(true);
      return false;
    }

    return true;
  };

  // Step2 검증 함수
  const validateStep2 = () => {
    const errors: string[] = [];

    if (templateData.volunteerTypes.length === 0) {
      errors.push("봉사자 유형을 하나 이상 선택해주세요.");
    }

    if (errors.length > 0) {
      errors.forEach(error => toast({
        variant: "destructive",
        title: "오류",
        description: error
      }));
      return false;
    }
    return true;
  };

  // Step3 검증 함수
  const validateStep3 = () => {
    const errors: string[] = [];

    if (!templateData.locationType) {
      errors.push("봉사지 유형을 선택해주세요.");
    }

    if (templateData.locationType === "주소") {
      if (!templateData.address) {
        errors.push("주소를 검색해주세요.");
      }
      if (!templateData.detailAddress) {
        errors.push("상세 주소를 입력해주세요.");
      }
    }

    if (errors.length > 0) {
      errors.forEach(error => toast({
        variant: "destructive",
        title: "오류",
        description: error
      }));
      return false;
    }
    return true;
  };

  // Step4 검증 함수
  const validateStep4 = () => {
    const errors: string[] = [];

    if (!templateData.contactName.trim()) {
      errors.push("담당자 이름을 입력해주세요.");
    }

    const { middle, last } = templateData.contactPhone;
    if (!middle || !last) {
      errors.push("연락처를 모두 입력해주세요.");
    } else {
      // 전화번호 형식 검증 (4자리씩)
      if (middle.length !== 4 || last.length !== 4) {
        errors.push("전화번호를 올바른 형식으로 입력해주세요.");
      }
    }

    if (errors.length > 0) {
      errors.forEach(error => toast({
        variant: "destructive",
        title: "오류",
        description: error
      }));
      return false;
    }
    return true;
  };

  const nextStep = () => {
    let isValid = true;

    switch (step) {
      case 0:
        isValid = validateStep0();
        break;
      case 1:
        isValid = validateStep1();
        break;
      case 2:
        isValid = validateStep2();
        break;
      case 3:
        isValid = validateStep3();
        break;
      case 4:
        isValid = validateStep4();
        break;
    }

    if (isValid) {
      if (step < steps.length - 1) {
        scrollToTop();
        setStep(step + 1);
      } else {
        createTemplate();
      }
    }
  };

  // 이미지 없이 진행하는 함수
  const proceedWithoutImage = () => {
    setShowImageDialog(false);
    setStep(step + 1);
  };

  const prevStep = () => {
    if (step > 0) {
      scrollToTop();
      setStep(step - 1);
    } else {
      // step이 0일 때는 main 페이지로 이동
      navigate('/main');
    }
  };

  return (
    <div className="flex flex-col h-full">
      {/* 메인 컨텐츠 영역 */}
      <div className="flex-1 bg-white p-4 mb-24">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold">{isCompleted ? "완료되었습니다" : steps[step]}</h2>
          {!isCompleted && (
            <span className="text-gray-500 text-sm font-semibold">{step + 1} / {steps.length}</span>
          )}
        </div>

        <AnimatePresence mode="wait">
          <motion.div
            key={isCompleted ? "completed" : step}
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -50 }}
            transition={{ duration: 0.3 }}
          >
            {isCompleted ? (
              <div className="text-center text-lg font-semibold text-blue-500">공고 작성이 완료되었습니다!</div>
            ) : (
              <>
                {step === 0 && <Step0GroupSelection templateData={templateData} setTemplateData={setTemplateData} />}
                {step === 1 && (
                  <Step1AnnouncementDetails 
                    templateData={templateData} 
                    setTemplateData={setTemplateData}
                    imageFiles={imageFiles}
                    setImageFiles={setImageFiles}
                  />
                )}
                {step === 2 && <Step2RecruitmentConditions templateData={templateData} setTemplateData={setTemplateData} />}
                {step === 3 && <Step3VolunteerLocation templateData={templateData} setTemplateData={setTemplateData} />}
                {step === 4 && <Step4ContactInfo templateData={templateData} setTemplateData={setTemplateData} />}
              </>
            )}
          </motion.div>
        </AnimatePresence>
      </div>

      {/* 하단 고정 버튼 */}
      {!isCompleted && (
        <div className="fixed inset-x-0 bottom-[72px] mx-4">
          <div className="max-w-[500px] mx-auto w-full bg-white p-4 border rounded-lg shadow-md">
            <div className="flex justify-between items-center gap-4">
              <Button 
                disabled={step === 0} 
                onClick={prevStep} 
                className="w-1/3 bg-gray-300 text-black py-4 text-lg"
              >
                이전
              </Button>
              <Button 
                onClick={nextStep} 
                className="w-1/3 bg-blue-500 text-white py-4 text-lg"
              >
                {step === steps.length - 1 ? "완료" : "다음"}
              </Button>
            </div>
          </div>
        </div>
      )}
      
      {/* 이미지 관련 다이얼로그 추가 */}
      <Dialog open={showImageDialog} onOpenChange={setShowImageDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>사진 추가 안내</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            사진을 추가하면 봉사자들의 참여를 더 효과적으로 유도할 수 있습니다.<br/>
            사진 없이 진행하시겠습니까?
          </div>

          <DialogFooter className="flex gap-2">
            <Button
              variant="outline"
              onClick={() => setShowImageDialog(false)}
            >
              아니오
            </Button>
            <Button
              onClick={proceedWithoutImage}
            >
              예
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

    </div>
  );
};

export default TemplateAndGroupWrite;
