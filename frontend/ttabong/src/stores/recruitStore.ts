import { create } from 'zustand';
import { recruitApi } from '@/api/recruitApi';
import type { OrgRecruit, Application } from '@/types/recruitType';

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

interface RecruitListItem {  // 목록용 간단한 타입
  recruit: {
    recruitId: number;
    status: string;
    deadline: string;
    maxVolunteer: number;
    participateVolCount: number;
  };
  template: {
    title: string;
  };
}

interface RecruitDetailType {
  template: {
    templateId: number;
    title: string;
    description: string;
    activityLocation: string;
    images: string[];
    contactName: string;
    contactPhone: string;
    volunteerField: string[];
    volunteerTypes: string[];
  };
  recruit: {
    recruitId: number;
    status: string;
    activityDate: string;
    activityStart: number;
    activityEnd: number;
    deadline: string;
    maxVolunteer: number;
    participateVolCount: number;
  };
  organization: {
    orgId: number;
    name: string;
  };
}

interface RecruitStore {
  myRecruits: Application[] | null;
  orgRecruits: OrgRecruit[] | null;
  recruits: RecruitItem[];
  isLoading: boolean;
  error: string | null;
  hasMore: boolean;
  recruitDetail: RecruitDetailType | null;
  selectedRecruitId: number | null;
  recruitList: RecruitListItem[] | null;
  nextCursor: number | null;
  fetchMyRecruits: (params?: { cursor?: number; limit?: number }) => Promise<void>;
  fetchOrgRecruits: () => Promise<void>;
  cancelApplication: (applicationId: number) => Promise<void>;
  fetchRecruitList: (cursor?: number) => Promise<void>;
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
  recruitList: null,
  nextCursor: null,

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
  fetchRecruitList: async (cursor) => {
    set({ isLoading: true });
    try {
      const response = await recruitApi.getRecruitList(cursor);
      
      if (cursor) {
        // 기존 목록에 추가
        set((state) => ({
          recruitList: [...(state.recruitList || []), ...response.recruits],
          hasMore: response.hasMore,
          nextCursor: response.nextCursor,
          error: null
        }));
      } else {
        // 새로운 목록으로 설정
        set({
          recruitList: response.recruits,
          hasMore: response.hasMore,
          nextCursor: response.nextCursor,
          error: null
        });
      }
    } catch (error) {
      console.error('공고 목록 조회 실패:', error);
      set({ error: '공고 목록을 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }
  },
  fetchRecruitDetail: async (recruitId) => {
    set({ isLoading: true });
    try {
      const response = await recruitApi.getRecruitDetail(recruitId);
      set({ recruitDetail: response, error: null });
    } catch (error) {
      console.error('공고 상세 조회 실패:', error);
      set({ error: '공고를 불러오는데 실패했습니다.' });
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