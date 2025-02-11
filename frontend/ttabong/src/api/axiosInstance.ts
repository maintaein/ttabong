import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import config from '@/config';

export interface ApiResponse<T = any> {
  status: number;
  message: string;
  access_token?: string;
  data?: T;
}

export interface ApiError {
  status: number;
  message: string;
  type: 'VALIDATION' | 'AUTH' | 'SERVER' | 'NETWORK';
}

interface ErrorResponse {
  message: string;
}

export const axiosInstance = axios.create({
  baseURL: config.baseURL + config.apiPrefix,
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
  (response) => response,
  async (error: AxiosError<ErrorResponse>) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('access_token');
      window.location.href = '/login?message=로그인이 필요합니다.';
      return Promise.reject(new Error('로그인이 필요합니다.'));
    }

    const apiError: ApiError = {
      status: error.response?.status || 500,
      message: error.response?.data?.message || '서버 오류가 발생했습니다.',
      type: getErrorType(error)
    };
    throw apiError;
  }
);

function getErrorType(error: AxiosError): ApiError['type'] {
  if (!error.response) return 'NETWORK';
  if (error.response.status === 400) return 'VALIDATION';
  if (error.response.status === 401 || error.response.status === 403) return 'AUTH';
  return 'SERVER';
}

export default axiosInstance; 