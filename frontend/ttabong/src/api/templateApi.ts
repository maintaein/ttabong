import axiosInstance from './axiosInstance';
import type { CreateTemplateRequest } from '@/types/template';

// 응답 타입 정의 추가
interface CreateGroupResponse {
  message: string;
  groupId: number;
}

interface PresignedUrlResponse {
  message: string;
  templateId: number | null;
  images: string[];
  imageUrl: string | null;
}

interface CreateTemplateResponse {
  message: string;
  templateId: number;
  images: string[];
  imageUrl: string;
}

export const templateApi = {
  // 템플릿 목록 조회
  getTemplates: async (cursor?: number, limit: number = 10) => {
    const response = await axiosInstance.get(`/org/templates`, {
      params: { cursor, limit }
    });
    console.log('템플릿 목록 응답:', response.data);  // 템플릿 목록 응답 확인
    return response.data;
  },

  // Presigned URL 요청
  getPresignedUrls: async () => {
    const response = await axiosInstance.get<PresignedUrlResponse>('/org/templates/presigned');
    return response.data;
  },

  // 템플릿 생성
  createTemplate: async (data: CreateTemplateRequest) => {
    // 이미지가 이미 URL 문자열이면 그대로 사용
    const requestData = {
      groupId: data.groupId,
      orgId: 5,
      categoryId: 3,
      title: data.title,
      activityLocation: data.activityLocation,
      status: 'ALL',
      contactName: data.contactName,
      contactPhone: data.contactPhone,
      description: data.description,
      images: data.images,  // 이미 URL 문자열 배열
      imageCount: data.images.length
    };

    const response = await axiosInstance.post<CreateTemplateResponse>('/org/templates', requestData);
    return response.data;
  },

  // 그룹 생성
  createGroup: async (groupName: string) => {
    const response = await axiosInstance.post<CreateGroupResponse>('/org/groups', { 
      orgId: 5,  // 고정된 orgId 사용
      groupName
    });
    return response.data;
  },

  // 템플릿 삭제
  deleteTemplates: async (templateIds: number[]) => {
    try {
      const response = await axiosInstance.patch('/org/templates/delete', {
        deletedTemplates: templateIds
      });
      console.log('API 응답 데이터:', response.data);
      return response.data;
    } catch (error) {
      console.error('API 에러:', error);
      throw error;
    }
  },

  // 그룹 삭제
  deleteGroup: async (groupId: number, orgId: number) => {
    try {
      console.log('그룹 삭제 요청:', { groupId, orgId });  // 요청 데이터 확인
      const response = await axiosInstance.patch('/org/groups/delete', {
        groupId,
        orgId
      });
      console.log('그룹 삭제 응답:', response.data);  // 응답 데이터 확인
      return response.data;
    } catch (error) {
      console.error('그룹 삭제 API 에러:', error);
      throw error;
    }
  },

  // 템플릿 상세 조회
  getTemplate: async (templateId: number) => {
    const response = await axiosInstance.get(`/org/templates/${templateId}`);
    return response.data;
  },

  // 템플릿 수정
  updateTemplate: async (templateId: number, data: CreateTemplateRequest) => {
    const response = await axiosInstance.patch(`/org/templates/${templateId}`, data);
    return response.data;
  }
};