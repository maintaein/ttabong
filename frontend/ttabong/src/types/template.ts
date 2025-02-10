// 템플릿 기본 타입
export interface Template {
  template_id: number;
  group_id: number;
  title: string;
  description: string;
  activity_location: string;
  category_main: string;
  category_sub: string;
  status: 'all' | 'active' | 'inactive';
  images: string[];
  contact_name: string;
  contact_phone: string;
  created_at: string;
  volunteerCount: number;
}

// 그룹 타입
export interface Group {
  group_id: number;
  name: string;
  templates: Template[];
}

// 템플릿 작성/수정 시 사용되는 폼 데이터 타입
export interface TemplateFormData {
  groupId: number | null;
  title: string;
  description: string;
  images: string[];
  volunteerTypes: string[];
  volunteerCount: number;
  locationType: '주소' | '재택' | '';
  address: string;
  detailAddress: string;
  contactName: string;
  contactPhone: {
    areaCode: string;
    middle: string;
    last: string;
  };
  template_id: number;
  created_at: string;
  startDate: Date | null;
  endDate: Date | null;
  volunteerDate: Date | null;
  startTime: string;
  endTime: string;
  volunteerField: string[];
}

// 각 Step 컴포넌트의 Props 타입
export interface StepProps {
  templateData: TemplateFormData;
  setTemplateData: React.Dispatch<React.SetStateAction<TemplateFormData>>;
}

// Daum 우편번호 API 응답 타입 추가
export interface DaumPostcodeData {
  address: string;
  addressType: string;
  bname: string;
  buildingName: string;
  zonecode: string;
}

// API 응답 타입
export interface APITemplate {
  templateId: number;
  orgId: number;
  categoryId: number;
  title: string;
  activityLocation: string;
  status: string;
  imageId: string;
  contactName: string;
  contactPhone: string;
  description: string;
  createdAt: string;
}

export interface APIGroup {
  groupId: number;
  groupName: string;
  templates: APITemplate[];
}

// API 요청 타입
export interface CreateTemplateRequest {
  groupId: number;
  orgId: number;
  categoryId: number;
  title: string;
  activityLocation: string;
  status: string;
  images: string[];
  imageCount: number;
  contactName: string;
  contactPhone: string;
  description: string;
  volunteerField: string[];
  volunteerTypes: string[];
  volunteerCount: number;
}

// 데이터 변환 유틸리티 함수
export const transformTemplateData = (localData: TemplateFormData): CreateTemplateRequest => {
  return {
    groupId: localData.groupId || 0,
    orgId: 5,
    categoryId: 3,
    title: localData.title,
    activityLocation: `${localData.address} ${localData.detailAddress}`.trim(),
    status: "ALL",
    images: localData.images,
    imageCount: localData.images.length,
    contactName: localData.contactName,
    contactPhone: `${localData.contactPhone.areaCode}-${localData.contactPhone.middle}-${localData.contactPhone.last}`,
    description: localData.description,
    volunteerField: localData.volunteerField,
    volunteerTypes: localData.volunteerTypes,
    volunteerCount: localData.volunteerCount,
  };
};

