import { create } from 'zustand';
import { recruitApi } from '@/api/recruitApi';
import type { OrgRecruit, Application } from '@/types/recruitType';
import type { RecruitItem } from '@/types/recruit';

interface RecruitStore {
  myRecruits: Application[] | null;
  orgRecruits: OrgRecruit[] | null;
  recruits: RecruitItem[];
  isLoading: boolean;
  error: string | null;
  hasMore: boolean;
  fetchMyRecruits: (params?: { cursor?: number; limit?: number }) => Promise<void>;
  fetchOrgRecruits: () => Promise<void>;
  fetchRecruits: (cursor?: number, limit?: number) => Promise<void>;
}

export const useRecruitStore = create<RecruitStore>((set, get) => ({
  myRecruits: null,
  orgRecruits: null,
  recruits: [],
  isLoading: false,
  error: null,
  hasMore: true,

  fetchMyRecruits: async (params) => {
    try {
      set({ isLoading: true, error: null });
      const recruits = await recruitApi.getMyApplications(params);
      
      if (params?.cursor) {
        // 페이지네이션: 기존 데이터에 새 데이터 추가
        const currentRecruits = get().myRecruits || [];
        set({ 
          myRecruits: [...currentRecruits, ...recruits],
          hasMore: recruits.length === (params.limit || 10)
        });
      } else {
        // 첫 로딩: 새로운 데이터로 교체
        set({ 
          myRecruits: recruits,
          hasMore: recruits.length === (params?.limit || 10)
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
  fetchRecruits: async (cursor?: number, limit?: number) => {
    set({ isLoading: true });
    try {
      const response = await recruitApi.getRecruits(cursor, limit);
      console.log('API Response:', response);  // 응답 데이터 확인
      set({ recruits: response.recruits, error: null });
    } catch (error) {
      console.error('Fetch Error:', error);  // 에러 상세 확인
      set({ error: '공고 목록을 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }
  }
})); 