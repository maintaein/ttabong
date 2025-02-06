import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

interface ReviewWriteStore {
  recruitId: number | null;
  orgId: number | null;
  setReviewInfo: (recruitId: number, orgId: number) => void;
  resetReviewInfo: () => void;
}

export const useReviewWriteStore = create<ReviewWriteStore>()(
  persist(
    (set) => ({
      recruitId: null,
      orgId: null,
      setReviewInfo: (recruitId, orgId) => set({ recruitId, orgId }),
      resetReviewInfo: () => set({ recruitId: null, orgId: null }),
    }),
    {
      name: 'review-write-storage',
      storage: createJSONStorage(() => localStorage),
    }
  )
); 