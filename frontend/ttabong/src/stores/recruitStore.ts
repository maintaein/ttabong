import { create } from 'zustand';
import { recruitApi } from '@/api/recruitApi';
import type { OrgRecruit, Application, RecruitDetail } from '@/types/recruitType';

// RecruitItem 타입 추가
interface RecruitItem {
  recruit: {
    recruitId: number;
    status: string;
    deadline: string;
    activityDate: string;
    activityStart: number;
    activityEnd: number;
    maxVolunteer: number;
    participateVolCount: number;
    createdAt: string;
  };
  group: {
    groupId: number;
    groupName: string;
  };
  template: {
    templateId: number;
    title: string;
  };
}

interface RecruitStore {
  myRecruits: Application[] | null;
  orgRecruits: OrgRecruit[] | null;
  recruits: RecruitItem[];
  isLoading: boolean;
  error: string | null;
  hasMore: boolean;
  recruitDetail: RecruitDetail | null;
  selectedRecruitId: number | null;
  fetchMyRecruits: (params?: { cursor?: number; limit?: number }) => Promise<void>;
  fetchOrgRecruits: () => Promise<void>;
  cancelApplication: (applicationId: number) => Promise<void>;
  fetchRecruitDetail: (recruitId: number) => Promise<void>;
  setSelectedRecruitId: (id: number) => Promise<void>;
  resetSelectedRecruitId: () => void;
  fetchRecruits: () => Promise<void>;
}

export const useRecruitStore = create<RecruitStore>((set) => ({
  myRecruits: null,
  orgRecruits: null,
  recruits: [],
  isLoading: false,
  error: null,
  hasMore: true,
  recruitDetail: null,
  selectedRecruitId: null,

  fetchMyRecruits: async (params) => {
    try {
      set({ isLoading: true, error: null });
      const response = await recruitApi.getMyApplications(params);
      
      if (params?.cursor) {
        set((state) => ({ 
          myRecruits: [...(state.myRecruits || []), ...response],
          hasMore: response.length === (params.limit || 10)
        }));
      } else {
        set({ 
          myRecruits: response,
          hasMore: response.length === (params?.limit || 10)
        });
      }
    } catch (error) {
      console.error('봉사내역을 불러오는데 실패했습니다:', error);
      set({ error: '봉사내역을 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }
  },
  fetchOrgRecruits: async () => {
    try {
      set({ isLoading: true, error: null });
      const response = await recruitApi.getOrgRecruits();
      set({ orgRecruits: response.recruits });
    } catch (error) {
      console.error('공고 목록을 불러오는데 실패했습니다:', error);
      set({ error: '공고 목록을 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }
  },
  cancelApplication: async (applicationId: number) => {
    try {
      set({ isLoading: true, error: null });
      await recruitApi.cancelApplication(applicationId);
      set((state) => ({
        myRecruits: state.myRecruits?.map(recruit => 
          recruit.applicationId === applicationId 
            ? { ...recruit, status: 'AUTO_CANCEL' }
            : recruit
        ) || null
      }));
    } catch (error) {
      console.error('봉사 신청 취소 실패:', error);
      set({ error: '봉사 신청 취소에 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }
  },
  fetchRecruitDetail: async (recruitId: number) => {
    try {
      set({ isLoading: true, error: null });
      const response = await recruitApi.getRecruitDetail(recruitId);
      set({ recruitDetail: response });
    } catch (error) {
      console.error('공고 상세 정보를 불러오는데 실패했습니다:', error);
      set({ error: '공고 상세 정보를 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }
  },
  setSelectedRecruitId: (id: number) => {
    set({ selectedRecruitId: id });
    return Promise.resolve();
  },
  resetSelectedRecruitId: () => set({ selectedRecruitId: null }),
  fetchRecruits: async () => {
    try {
      set({ isLoading: true, error: null });
      const response = await recruitApi.getOrgRecruits();
      set({ recruits: response.recruits });
    } catch (error) {
      console.error('공고 목록을 불러오는데 실패했습니다:', error);
      set({ error: '공고 목록을 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }
  },
})); 