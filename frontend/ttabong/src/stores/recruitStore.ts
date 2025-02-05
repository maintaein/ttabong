import { create } from 'zustand';
import type { Application } from '@/types/recruitType';
import { recruitApi } from '@/api/recruitApi';

interface RecruitStore {
  myRecruits: Application[];
  isLoading: boolean;
  error: string | null;
  fetchMyRecruits: () => Promise<void>;
}

export const useRecruitStore = create<RecruitStore>((set) => ({
  myRecruits: [],
  isLoading: false,
  error: null,

  fetchMyRecruits: async () => {
    set({ isLoading: true });
    try {
      const recruits = await recruitApi.getMyRecruits();
      set({ myRecruits: recruits, error: null });
    } catch (error) {
      set({ error: '봉사내역을 불러오는데 실패했습니다.' });
    } finally {
      set({ isLoading: false });
    }
  },
})); 