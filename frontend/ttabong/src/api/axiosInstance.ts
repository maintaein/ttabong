import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import config from '@/config';

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
    const token = localStorage.getItem('accessToken');
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
  async (error: AxiosError<{ message: string }>) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && originalRequest) {
      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) throw new Error('리프레시 토큰이 없습니다.');

        const { data } = await axios.post(`${config.baseURL}${config.apiPrefix}/auth/refresh`, {
          refreshToken,
        });

        const { accessToken } = data;
        localStorage.setItem('accessToken', accessToken);

        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        }
        return axios(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(
      new Error(error.response?.data?.message || '서버 오류가 발생했습니다.')
    );
  }
);

export default axiosInstance; 