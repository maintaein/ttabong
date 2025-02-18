import axiosInstance from './axiosInstance';
import type { ApplicationsResponse } from '@/types/application';

export const applicationApi = {
  getRecruitApplications: async (recruitId: number): Promise<ApplicationsResponse> => {
    const response = await axiosInstance.get(`/org/recruits/${recruitId}/applications`);
    return response.data;
  },
}; 