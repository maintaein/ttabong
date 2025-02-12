import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import config from '@/config';

export interface ApiResponse<T = any> {
  data: T;
  message: string;
}

export interface ApiError {
  status: number;
  message: string;
  type?: 'VALIDATION' | 'AUTH' | 'SERVER';
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
  (error: AxiosError<{ message: string; type?: string }>) => {
    const apiError: ApiError = {
      status: error.response?.status || 500,
      message: error.response?.data?.message || '서버 오류가 발생했습니다.',
      type: error.response?.data?.type as ApiError['type']
    };
    throw apiError;
  }
);

export default axiosInstance; 