import axiosInstance from './axiosInstance';
import type { Application } from '@/types/recruitType';

export const recruitApi = {
  getMyRecruits: async (): Promise<Application[]> => {
    const response = await axiosInstance.get('/applications/mine');
    return response.data;
  },
}; 