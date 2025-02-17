import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import config from '@/config';
import { toast } from 'sonner';

export interface ApiResponse<T = any> {
  data: T;
  message: string;
}

export interface ApiError {
  status: number;
  message: string;
}

const axiosInstance = axios.create({
  baseURL: `${config.baseURL}${config.apiPrefix}`,
  timeout: config.timeout,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => Promise.reject(error)
);

// 응답 인터셉터
axiosInstance.interceptors.response.use(
  response => response,
  (error: AxiosError<{ message: string }>) => {
    const apiError: ApiError = {
      status: error.response?.status || 500,
      message: error.response?.data?.message || '서버 오류가 발생했습니다.'
    };

    // 401 에러 처리
    if (error.response?.status === 401) {
      localStorage.removeItem('access_token');
      toast.error('로그인이 필요합니다.', {
        description: '로그인 페이지로 이동합니다.'
      });
      window.location.href = '/login';
      return Promise.reject(apiError);
    }

    // 기타 에러 메시지 토스트로 표시
    toast.error('오류가 발생했습니다.', {
      description: apiError.message
    });

    throw apiError;
  }
);

export default axiosInstance; 