import axiosInstance from './axiosInstance';
import type { 
  LoginRequest, 
  LoginResponse,
  VolunteerRegisterRequest,
  OrganizationRegisterRequest 
} from '@/types/userType';

export const userApi = {
  login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await axiosInstance.post('/user/login', data);
    return response.data;
  },

  registerVolunteer: async (data: VolunteerRegisterRequest): Promise<void> => {
    await axiosInstance.post('/volunteer/register', data);
  },

  logout: async () => {
    localStorage.removeItem('access_token');
  },

  registerOrganization: async (data: OrganizationRegisterRequest): Promise<void> => {
    await axiosInstance.post('/organization/register', data);
  },
}; 