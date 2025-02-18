import axiosInstance from './axiosInstance';
import type { 
  Application, 
  CreateRecruitRequest, 
  UpdateRecruitRequest,
  GetApplicationsParams,
  RecruitDetail
} from '@/types/recruitType';

export const recruitApi = {
  getMyApplications: async (params?: GetApplicationsParams): Promise<Application[]> => {
    const { cursor = 0, limit = 10 } = params || {};
    const queryParams = new URLSearchParams();
    
    queryParams.append('cursor', cursor.toString());
    queryParams.append('limit', limit.toString());
    
    const queryString = queryParams.toString();
    const url = `/vol/applications/recruits?${queryString}`;
    
    const response = await axiosInstance.get(url);
    return response.data; 
  },

  getOrgRecruits: async () => {
    const response = await axiosInstance.get('/org/recruits');
    return response.data;
  },

  createRecruit: async (data: CreateRecruitRequest) => {
    const response = await axiosInstance.post('/org/recruits', data);
    return response.data;
  },

  deleteRecruit: async (recruitIds: number[]) => {
    console.log('Sending delete request:', recruitIds); // 디버깅용
    const response = await axiosInstance.patch('/org/recruits/delete', {
      deletedRecruits: recruitIds
    });
    return response.data;
  },

  updateRecruit: async (recruitId: number, data: UpdateRecruitRequest) => {
    const response = await axiosInstance.patch(`/org/recruits/${recruitId}`, data);
    return response.data;
  },

  getRecruitDetail: async (recruitId: number): Promise<RecruitDetail> => {
    const response = await axiosInstance.get(`/vol/recruits/${recruitId}`);
    return response.data;
  },

  applyRecruit: async (recruitId: number) => {
    const response = await axiosInstance.post('/vol/applications', {
      recruitId
    });
    return response.data;
  },

  cancelApplication: async (applicationId: number) => {
    const response = await axiosInstance.patch(`/vol/applications/${applicationId}`);
    return response.data;
  }
}; 