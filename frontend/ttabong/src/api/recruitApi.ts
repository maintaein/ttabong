import axiosInstance from './axiosInstance';
import type { Application } from '@/types/recruitType';

export const recruitApi = {
  getMyRecruits: async (): Promise<Application[]> => {
    const response = await axiosInstance.get('/applications/mine');
    return response.data;
  },
  getOrgRecruits: async () => {
    const response = await axiosInstance.get('/org/recruits');
    return response.data;
  },
  createRecruit: async (data: {
    templateId: number;
    deadline: string;
    activityDate: string;
    activityStart: number;
    activityEnd: number;
    maxVolunteer: number;
  }) => {
    const response = await axiosInstance.post('/org/recruits', data);
    return response.data;
  },
  deleteRecruit: async (recruitId: number) => {
    const response = await axiosInstance.patch('/org/recruits/delete', {
      deletedRecruits: recruitId
    });
    return response.data;
  },
  updateRecruit: async (recruitId: number, data: {
    deadline?: string;
    activityDate?: string;
    activityStart?: number;
    activityEnd?: number;
    maxVolunteer?: number;
    images?: string[];
    imageCount?: number;
  }) => {
    const response = await axiosInstance.patch(`/org/recruits/${recruitId}`, data);
    return response.data;
  },
  getRecruitDetail: async (recruitId: number) => {
    const response = await axiosInstance.get(`/org/recruits/${recruitId}`);
    return response.data;
  },
}; 