import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { jwtDecode } from 'jwt-decode';
import { userApi } from '@/api/userApi';
import type { ApiError } from '@/api/axiosInstance';
import type { JwtPayload } from '@/types/userType';
import { 
  VolunteerRegisterRequest, 
  OrganizationRegisterRequest 
} from '@/types/userType';

interface UserState {
  userId: string | null;
  userType: 'volunteer' | 'organization' | null;
  isLoading: boolean;
  error: string | null;
  login: (email: string, password: string, userType: 'volunteer' | 'organization') => Promise<string>;
  logout: () => Promise<void>;
  clearError: () => void;
  registerVolunteer: (data: VolunteerRegisterRequest) => Promise<void>;
  registerOrganization: (data: OrganizationRegisterRequest) => Promise<void>;
}

export const useUserStore = create<UserState>()(
  persist(
    (set) => ({
      userId: null,
      userType: null,
      isLoading: false,
      error: null,

      login: async (email, password, userType) => {
        set({ isLoading: true, error: null });
        try {
          const response = await userApi.login({ email, password, userType });
          const decoded = jwtDecode<JwtPayload>(response.access_token);
          
          localStorage.setItem('access_token', response.access_token);
          set({
            userId: decoded.sub,
            userType: decoded.userType,
            error: null
          });
          return response.message;
        } catch (error) {
          const apiError = error as ApiError;
          set({ error: apiError.message });
          throw apiError;
        } finally {
          set({ isLoading: false });
        }
      },

      logout: async () => {
        try {
          await userApi.logout();
          set({
            userId: null,
            userType: null,
            error: null
          });
        } catch (error) {
          set({ error: '로그아웃에 실패했습니다.' });
          throw error;
        }
      },

      clearError: () => set({ error: null }),

      registerVolunteer: async (data) => {
        set({ isLoading: true, error: null });
        try {
          await userApi.registerVolunteer(data);
        } catch (error) {
          const apiError = error as ApiError;
          set({ error: apiError.message });
          throw apiError;
        } finally {
          set({ isLoading: false });
        }
      },

      registerOrganization: async (data) => {
        set({ isLoading: true, error: null });
        try {
          await userApi.registerOrganization(data);
        } catch (error) {
          const apiError = error as ApiError;
          set({ error: apiError.message });
          throw apiError;
        } finally {
          set({ isLoading: false });
        }
      },
    }),
    {
      name: 'user-storage',
    }
  )
); 