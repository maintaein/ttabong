import axiosInstance from './axiosInstance';
import type { CreateTemplateRequest } from '@/types/template';

export const templateApi = {
  // 템플릿 목록 조회
  getTemplates: async (cursor?: number, limit: number = 10) => {
    const response = await axiosInstance.get(`/org/templates`, {
      params: { cursor, limit }
    });
    return response.data;
  },

  // 템플릿 생성
  createTemplate: async (data: CreateTemplateRequest) => {
    const response = await axiosInstance.post('/org/templates', data);
    return response.data as { templateId: number };
  },

  // 그룹 생성
  createGroup: async (groupName: string) => {
    const response = await axiosInstance.post('/org/groups', { groupName });
    return response.data;
  },

  // 템플릿 삭제
  deleteTemplate: async (templateId: number) => {
    const response = await axiosInstance.delete(`/org/templates/${templateId}`);
    return response.data;
  },

  // 그룹 삭제
  deleteGroup: async (groupId: number, orgId: number) => {
    const response = await axiosInstance.patch('/org/groups/delete', {
      groupId,
      orgId
    });
    return response.data;
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