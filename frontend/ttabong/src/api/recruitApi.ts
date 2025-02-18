import axiosInstance from './axiosInstance';
import type { 
  Application, 
  CreateRecruitRequest, 
  UpdateRecruitRequest,
  GetApplicationsParams,
} from '@/types/recruitType';

const RECRUITS_PER_PAGE = 10;  // 한 페이지당 공고 수

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

  getRecruitList: async (cursor: number = 0) => {
    const response = await axiosInstance.get(`/org/recruits?cursor=${cursor}&limit=${RECRUITS_PER_PAGE}`);
    const recruits = response.data.recruits;
    
    return {
      recruits,
      hasMore: recruits.length === RECRUITS_PER_PAGE,
      nextCursor: recruits.length ? recruits[recruits.length - 1].recruit.recruitId : null
    };
  },

  getRecruitDetail: async (recruitId: number, userType: string) => {
    try {
      const endpoint = userType === 'volunteer' ? 'vol' : 'org';
      const response = await axiosInstance.get(`/${endpoint}/recruits/${recruitId}`);
      return response.data;
    } catch (error: any) {
      throw error.response?.data?.message || '공고를 불러오는데 실패했습니다.';
    }
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
  },

  updateRecruitStatus: async (recruitId: number, status: string) => {
    const response = await axiosInstance.patch(`/org/recruits/${recruitId}/status`, {
      status
    });
    return response.data;
  }
}; 