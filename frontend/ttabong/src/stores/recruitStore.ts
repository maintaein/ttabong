import { create } from 'zustand';
import { recruitApi } from '@/api/recruitApi';
import type { OrgRecruit, Application } from '@/types/recruitType';

interface RecruitStore {
  myRecruits: Application[] | null;
  orgRecruits: OrgRecruit[] | null;
  isLoading: boolean;
  error: string | null;
  fetchMyRecruits: () => Promise<void>;
  fetchOrgRecruits: () => Promise<void>;
}

export const useRecruitStore = create<RecruitStore>((set) => ({
  myRecruits: null,
  orgRecruits: null,
  isLoading: false,
  error: null,
  fetchMyRecruits: async () => {
    try {
      set({ isLoading: true, error: null });
      const recruits = await recruitApi.getMyRecruits();
      set({ myRecruits: recruits });
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
})); 