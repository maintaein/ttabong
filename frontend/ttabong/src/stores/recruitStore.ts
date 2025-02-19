import { create } from 'zustand';
import { recruitApi } from '@/api/recruitApi';
import type { 
  OrgRecruit, 
  Application, 
  RecruitDetail,
  OrgRecruitStatus,
  VolunteerApplicationStatus 
} from '@/types/recruitType';

// RecruitItem 타입 추가
interface RecruitItem {
  recruit: {
    recruitId: number;
    status: OrgRecruitStatus;
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

interface RecruitStore {
  myRecruits: Application[] | null;
  orgRecruits: OrgRecruit[] | null;
  recruits: RecruitItem[];
  isLoading: boolean;
  error: string | null;
  hasMore: boolean;
  recruitDetail: RecruitDetail | null;
  selectedRecruitId: number | null;
  recruitList: RecruitListItem[] | null;
  nextCursor: number | null;
  fetchMyRecruits: (params?: { cursor?: number; limit?: number }) => Promise<void>;
  fetchOrgRecruits: () => Promise<void>;
  cancelApplication: (applicationId: number) => Promise<void>;
  fetchRecruitList: (cursor?: number) => Promise<void>;
  fetchRecruitDetail: (recruitId: number, userType?: string) => Promise<void>;
  setSelectedRecruitId: (id: number) => Promise<void>;
  resetSelectedRecruitId: () => void;
  fetchRecruits: () => Promise<void>;
  applyRecruit: (recruitId: number) => Promise<void>;
  updateRecruitStatus: (recruitId: number, newStatus: string) => Promise<void>;
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
    } catch (error: any) {
      console.error('봉사내역 조회 실패:', error);
      set({ error: error.response?.data?.message || '봉사내역을 불러오는데 실패했습니다.' });
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
      const response = await recruitApi.getMyApplications();
      set({ 
        myRecruits: response.map(application => ({
          ...application,
          status: application.applicationId === applicationId 
            ? 'AUTO_CANCEL' as VolunteerApplicationStatus
            : application.status
        })),
        recruitDetail: null 

        
      });
    } catch (error) {
      console.error('봉사 신청 취소 실패:', error);
      set({ error: '봉사 신청 취소에 실패했습니다.' });
      throw error;
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
  fetchRecruitDetail: async (recruitId: number, userType = 'volunteer') => {
    set({ isLoading: true });
    try {
      const response = await recruitApi.getRecruitDetail(recruitId, userType);
      set({ recruitDetail: response, error: null });
    } catch (error: any) {
      console.error('공고 상세 조회 실패:', error);
      set({ error: error.response?.data?.message || '공고를 불러오는데 실패했습니다.' });
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
    } catch (error: any) {
      console.error('공고 목록 조회 실패:', error);
      set({ error: error.response?.data?.message || '공고 목록을 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }
  },
  applyRecruit: async (recruitId: number) => {
    try {
      set({ isLoading: true, error: null });
      await recruitApi.applyRecruit(recruitId);
      const response = await recruitApi.getRecruitDetail(recruitId, 'volunteer');
      
      const transformedData: RecruitDetail = {
        ...response,
        recruit: {
          ...response.recruit,
          status: response.recruit.status as OrgRecruitStatus
        },
        application: response.application ? {
          applicationId: response.application.applicationId,
          name: response.application.name,
          status: response.application.status as VolunteerApplicationStatus
        } : undefined
      };
      
      set({ recruitDetail: transformedData });
    } catch (error: any) {
      console.error('봉사 신청 실패:', error);
      throw error.response?.data?.message || '봉사 신청에 실패했습니다.';
    } finally {
      set({ isLoading: false });
    }
  },
  updateRecruitStatus: async (recruitId: number, newStatus: string) => {
    try {
      // 간단한 상태 변경 API 사용
      await recruitApi.updateRecruitStatus(recruitId, newStatus);

      // 로컬 상태 업데이트
      set(state => ({
        recruits: state.recruits.map(item => 
          item.recruit.recruitId === recruitId 
            ? { ...item, recruit: { ...item.recruit, status: newStatus as OrgRecruitStatus } }
            : item
        )
      }));
    } catch (error) {
      console.error('상태 업데이트 실패:', error);
      throw error;
    }
  },
})); 