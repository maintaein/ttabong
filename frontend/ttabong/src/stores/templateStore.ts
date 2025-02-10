import { create } from 'zustand';
import { templateApi } from '@/api/templateApi';
import type { APIGroup, TemplateFormData } from '@/types/template';
import { transformTemplateData } from '@/types/template';
import { toast } from 'react-hot-toast';

interface TemplateStore {
  groups: APIGroup[];
  isLoading: boolean;
  error: string | null;
  fetchTemplates: (cursor?: number) => Promise<void>;
  createTemplate: (data: TemplateFormData) => Promise<{ templateId: number }>;
  createGroup: (groupName: string) => Promise<{ groupId: number; groupName: string }>;
  deleteTemplate: (templateId: number) => Promise<void>;
  deleteGroup: (groupId: number) => Promise<void>;
}

export const useTemplateStore = create<TemplateStore>((set, get) => ({
  groups: [],
  isLoading: false,
  error: null,

  fetchTemplates: async (cursor) => {
    set({ isLoading: true });
    try {
      const response = await templateApi.getTemplates(cursor);
      set({ groups: response.groups, error: null });
    } catch (error) {
      console.error('템플릿 목록 로드 실패:', error);
      set({ error: '템플릿 목록을 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }
  },

  createTemplate: async (templateData: TemplateFormData) => {
    set({ isLoading: true });
    try {
      const apiData = transformTemplateData(templateData);
      const response = await templateApi.createTemplate(apiData);
      return response;
    } catch (error) {
      console.error('템플릿 생성 실패:', error);
      set({ error: '템플릿 생성에 실패했습니다.' });
      throw error;
    } finally {
      set({ isLoading: false });
    }
  },

  createGroup: async (groupName: string) => {
    try {
      const response = await templateApi.createGroup(groupName);
      get().fetchTemplates(); // 그룹 목록 새로고침
      return {
        groupId: response.groupId,
        groupName
      };
    } catch (error) {
      console.error('그룹 생성 실패:', error);
      toast.error('그룹 생성에 실패했습니다.');
      throw error;
    }
  },

  deleteTemplate: async (templateId: number) => {
    try {
      await templateApi.deleteTemplate(templateId);
      const response = await templateApi.getTemplates();
      set({ groups: response.groups, error: null });
    } catch (error) {
      console.error('템플릿 삭제 실패:', error);
      set({ error: '템플릿 삭제에 실패했습니다.' });
    }
  },

  deleteGroup: async (groupId: number) => {
    try {
      await templateApi.deleteGroup(groupId, 5); // orgId는 현재 로그인한 기관 ID
      const response = await templateApi.getTemplates();
      set({ groups: response.groups, error: null });
    } catch (error) {
      console.error('그룹 삭제 실패:', error);
      set({ error: '그룹 삭제에 실패했습니다.' });
    }
  }
})); 