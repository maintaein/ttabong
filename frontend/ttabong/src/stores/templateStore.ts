import { create } from 'zustand';
import { templateApi } from '@/api/templateApi';
import type { APIGroup, CreateTemplateRequest } from '@/types/template';
import { toast } from 'react-hot-toast';

interface TemplateStore {
  groups: APIGroup[];
  isLoading: boolean;
  error: string | null;
  fetchTemplates: (cursor?: number) => Promise<void>;
  createTemplate: (data: CreateTemplateRequest) => Promise<{ templateId: number }>;
  createGroup: (groupName: string) => Promise<{ groupId: number; groupName: string }>;
  deleteTemplate: (templateId: number) => Promise<void>;
  deleteGroup: (groupId: number) => Promise<void>;
  deleteTemplates: (templateIds: number[]) => Promise<void>;
}

export const useTemplateStore = create<TemplateStore>((set, get) => {
  const { toast } = useToast();
  return {
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

  createTemplate: async (data: CreateTemplateRequest) => {
    try {
      console.log('Template creation request body:', JSON.stringify(data, null, 2));
      const response = await templateApi.createTemplate(data);
      return response;
    } catch (error) {
      console.error('템플릿 생성 실패:', error);
      throw error;
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
        toast({
          title: "오류",
          description: "그룹 생성에 실패했습니다.",
          variant: "destructive",
        });
        throw error;
      }
    },

  deleteTemplate: async (templateId: number) => {
    try {
      await templateApi.deleteTemplates([templateId]);
      const response = await templateApi.getTemplates();
      set({ groups: response.groups, error: null });
    } catch (error) {
      console.error('템플릿 삭제 실패:', error);
      set({ error: '템플릿 삭제에 실패했습니다.' });
    }
  },

  deleteGroup: async (groupId: number) => {
    try {
      await templateApi.deleteGroup(groupId, 5);
      toast.success('그룹이 삭제되었습니다.');
      // 서버에서 새로운 데이터를 받아와서 한 번에 갱신
      const response = await templateApi.getTemplates();
      set({ groups: response.groups, error: null });
    } catch (error) {
      console.error('그룹 삭제 실패:', error);
      toast.error('그룹 삭제에 실패했습니다.');
      throw error;
    }
  },

  deleteTemplates: async (templateIds: number[]) => {
    try {
      await templateApi.deleteTemplates(templateIds);
      toast.success('템플릿이 삭제되었습니다.');
      const response = await templateApi.getTemplates();
      set({ groups: response.groups, error: null });
    } catch (error) {
      console.error('템플릿 삭제 실패:', error);
      toast.error('템플릿 삭제에 실패했습니다.');
      throw error;
    }
  }
})); 