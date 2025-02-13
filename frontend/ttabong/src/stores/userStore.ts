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

// store 초기화 시 토큰 체크
const initializeAuth = () => {
  try {
    const token = localStorage.getItem('access_token');
    if (!token) return { userId: null, userType: null };

    const decoded = jwtDecode<JwtPayload>(token);
    if (!decoded || !decoded.sub || !decoded.userType) {
      localStorage.removeItem('access_token');
      return { userId: null, userType: null };
    }

    return {
      userId: decoded.sub,
      userType: decoded.userType as 'volunteer' | 'organization',
    };
  } catch (error) {
    console.error('Token initialization error:', error);
    localStorage.removeItem('access_token');
    return { userId: null, userType: null };
  }
};

export const useUserStore = create<UserState>()(
  persist(
    (set) => ({
      ...initializeAuth(),
      isLoading: false,
      error: null,

      login: async (email, password, userType) => {
        set({ isLoading: true, error: null });
        try {
          const response = await userApi.login({ email, password, userType });
          
          if (!response?.accessToken) {
            throw new Error('토큰이 없습니다.');
          }

          const decoded = jwtDecode<JwtPayload>(response.accessToken);
          if (!decoded || !decoded.sub || !decoded.userType) {
            throw new Error('유효하지 않은 토큰입니다.');
          }

          localStorage.setItem('access_token', response.accessToken);
          
          set({
            userId: decoded.sub,
            userType: decoded.userType as 'volunteer' | 'organization',
            error: null
          });
          
          return response.message || '로그인 성공';
        } catch (error) {
          console.error('Login error:', error);
          const apiError = error as ApiError;
          set({ error: apiError.message || '로그인에 실패했습니다.' });
          throw apiError;
        } finally {
          set({ isLoading: false });
        }
      },

      logout: async () => {
        try {
          await userApi.logout();
          localStorage.removeItem('access_token');  // 토큰 제거 추가
          set({
            userId: null,
            userType: null,
            error: null
          });
        } catch (error) {
          console.error('Logout error:', error);
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
          set({ error: apiError.message || '회원가입에 실패했습니다.' });
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
          set({ error: apiError.message || '기관 회원가입에 실패했습니다.' });
          throw apiError;
        } finally {
          set({ isLoading: false });
        }
      },
    }),
    {
      name: 'user-storage',
      partialize: (state) => ({ 
        userId: state.userId,
        userType: state.userType 
      }),
    }
  )
); 