import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Button } from "@/components/ui/button";
import Step0GroupSelection from "@/pages/Me/TemplateComponents/Step0GroupSelection";
import Step1AnnouncementDetails from "@/pages/Me/TemplateComponents/Step1AnnouncementDetails";
import Step2RecruitmentConditions from "@/pages/Me/TemplateComponents/Step2RecruitmentConditions";
import Step3VolunteerLocation from "@/pages/Me/TemplateComponents/Step3VolunteerLocation";
import Step4ContactInfo from "@/pages/Me/TemplateComponents/Step4ContactInfo";
import { motion, AnimatePresence } from "framer-motion";
import { Group, Template, TemplateFormData } from '@/types/template';
import { toast } from "sonner";
import { Toaster } from "react-hot-toast";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { useScroll } from '@/contexts/ScrollContext';

const steps = [
  "공고 내용 입력(1/2)",
  "공고 내용 입력(2/2)",
  "모집 조건 설정",
  "봉사지 정보 입력",
  "담당자 정보 입력"
];

const TemplateAndGroupWrite = () => {
  const [step, setStep] = useState(0);
  const [isCompleted, setIsCompleted] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const templateId = location.state?.templateId;
  const { scrollToTop } = useScroll();

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

  useEffect(() => {
    if (isCompleted) {
      setTimeout(() => {
        navigate("/template-and-group", {
          state: { newTemplate: true }
        });
      }, 2000);
    }
  }, [isCompleted, navigate]);

  // 초기 데이터 로드
  useEffect(() => {
    if (templateId) {
      const storedTemplates = localStorage.getItem("volunteerTemplates");
      if (storedTemplates) {
        const templates = JSON.parse(storedTemplates);
        const existingTemplate = templates.find((t:Template) => t.template_id === templateId);
        
        if (existingTemplate) {
          setTemplateData({
            ...templateData, // 현재 날짜/시간 유지
            groupId: existingTemplate.group_id,
            title: existingTemplate.title,
            description: existingTemplate.description,
            images: existingTemplate.images || [],
            volunteerTypes: existingTemplate.category_sub.split(", "),
            volunteerCount: existingTemplate.volunteerCount || 10,
            locationType: existingTemplate.activity_location === "재택" ? "재택" : "주소",
            address: existingTemplate.activity_location !== "재택" 
              ? existingTemplate.activity_location.split(" ").slice(0, -1).join(" ")
              : "",
            detailAddress: existingTemplate.activity_location !== "재택"
              ? existingTemplate.activity_location.split(" ").slice(-1)[0]
              : "",
            contactName: existingTemplate.contact_name,
            contactPhone: {
              areaCode: existingTemplate.contact_phone.split("-")[0],
              middle: existingTemplate.contact_phone.split("-")[1],
              last: existingTemplate.contact_phone.split("-")[2]
            },
            template_id: Date.now(), // 새로운 ID 생성
            volunteerField: existingTemplate.volunteer_field?.split(", ") || []
          });
        }
      }
    }
  }, [templateId]);

  // 템플릿 생성 및 저장 함수
  const createTemplate = () => {
    const storedTemplates = localStorage.getItem("volunteerTemplates") || "[]";
    const templates = JSON.parse(storedTemplates);
    
    const newTemplate = {
      template_id: templateData.template_id,
      group_id: templateData.groupId,
      title: templateData.title,
      activity_location: templateData.locationType === "재택" 
        ? "재택" 
        : `${templateData.address} ${templateData.detailAddress}`,
      category_main: templates.find((g:Group) => g.group_id === templateData.groupId)?.name || "",
      category_sub: templateData.volunteerTypes.join(", "),
      status: "all",
      images: templateData.images,
      contact_name: templateData.contactName,
      contact_phone: `${templateData.contactPhone.areaCode}-${templateData.contactPhone.middle}-${templateData.contactPhone.last}`,
      description: templateData.description,
      created_at: templateData.created_at,
      startDate: templateData.startDate?.toISOString().split('T')[0] || "",
      endDate: templateData.endDate?.toISOString().split('T')[0] || "",
      volunteerDate: templateData.volunteerDate?.toISOString().split('T')[0] || "",
      startTime: templateData.startTime,
      endTime: templateData.endTime,
      volunteer_field: templateData.volunteerField.join(", "),
      volunteerCount: templateData.volunteerCount
    };

    if (templateId) {
      // 수정 모드: 기존 템플릿 업데이트
      const updatedTemplates = templates.map((t:Template) => 
        t.template_id === templateId ? newTemplate : t
      );
      localStorage.setItem("volunteerTemplates", JSON.stringify(updatedTemplates));
    } else {
      // 새로운 템플릿 추가
      templates.push(newTemplate);
      localStorage.setItem("volunteerTemplates", JSON.stringify(templates));
    }

    setIsCompleted(true);
    
    // 2초 후 목록 페이지로 이동
    setTimeout(() => {
      navigate("/template-and-group", {
        state: { newTemplate: true }
      });
    }, 2000);
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
      errors.forEach(error => toast.error(error));
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
      errors.forEach(error => toast.error(error));
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
      errors.forEach(error => toast.error(error));
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
      errors.forEach(error => toast.error(error));
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
      errors.forEach(error => toast.error(error));
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
                {step === 1 && <Step1AnnouncementDetails templateData={templateData} setTemplateData={setTemplateData} />}
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

      <Toaster position="top-center" />
    </div>
  );
};

export default TemplateAndGroupWrite;
