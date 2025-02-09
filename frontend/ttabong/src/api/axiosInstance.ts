import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import config from '@/config';

export interface ApiResponse<T = any> {
  data: T;
  message: string;
}

export interface ApiError {
  status: number;
  message: string;
  type: 'VALIDATION' | 'AUTH' | 'SERVER' | 'NETWORK';
}

interface ErrorResponse {
  message: string;
}

const axiosInstance = axios.create({
  baseURL: `${config.baseURL}${config.apiPrefix}`,
  timeout: config.timeout,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  withCredentials: true 
});

// 요청 인터셉터
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => config,
  (error: AxiosError) => Promise.reject(error)
);

// 응답 인터셉터
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ErrorResponse | string>) => {
    const apiError: ApiError = {
      status: error.response?.status || 500,
      message: typeof error.response?.data === 'string' 
        ? error.response.data 
        : error.response?.data?.message || '서버 오류가 발생했습니다.',
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