import axiosInstance from './axiosInstance';
import { ApiResponse } from './axiosInstance';
import { 
  LoginRequest, 
  LoginResponse,
  VolunteerRegisterRequest,
  OrganizationRegisterRequest 
} from '@/types/userType';

export const userApi = {
  login: async (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    const response = await axiosInstance.post('/user/login', data);
    return response.data;
  },

  logout: async () => {
    await axiosInstance.post('/user/logout', {}, {
      withCredentials: true
    });
  },

  registerVolunteer: async (data: VolunteerRegisterRequest): Promise<ApiResponse<void>> => {
    const response = await axiosInstance.post('/volunteer/register', data);
    return response.data;
  },

  registerOrganization: async (data: OrganizationRegisterRequest): Promise<ApiResponse<void>> => {
    const response = await axiosInstance.post('/org/register', data);
    return response.data;
  }
}; 