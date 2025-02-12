export interface User {
  userId: number;
  email: string;
  name: string;
  password: string;
  phone: string;
  profileImage: string | null;
  isDeleted: boolean;
  createdAt: string;
}

export interface CreateUserRequest {
  email: string;
  name: string;
  password: string;
  phone: string;
  profileImage?: string;
}

export interface UpdateUserRequest {
  name?: string;
  password?: string;
  phone?: string;
  profileImage?: string;
}

export interface UserResponse {
  userId: number;
  email: string;
  name: string;
  phone: string;
  profileImage: string | null;
  isDeleted: boolean;
  createdAt: string;
}

export interface Volunteer {
  volunteerId: number;
  userId: number;
  preferredTime: string;
  interestTheme: string;
  durationTime: string;
  region: string;
  birthDate: string;
  gender: string;
  recommendedCount: number;
  notRecommendedCount: number;
}

export interface CreateVolunteerRequest {
  preferredTime: string;
  interestTheme: string;
  durationTime: string;
  region: string;
  birthDate: string;
  gender: string;
}

export interface UpdateVolunteerRequest {
  preferredTime?: string;
  interestTheme?: string;
  durationTime?: string;
  region?: string;
}

export interface UserWithVolunteer extends UserResponse {
  volunteer: Volunteer;
} 

export interface Organization {
  orgId: number;
  userId: number;
  businessRegNumber: string;
  orgName: string;
  representativeName: string;
  orgAddress: string;
  createdAt: string;
}

export interface CreateOrganizationRequest {
  businessRegNumber: string;
  orgName: string;
  representativeName: string;
  orgAddress: string;
}

export interface UpdateOrganizationRequest {
  orgName?: string;
  representativeName?: string;
  orgAddress?: string;
}

export interface UserWithOrganization extends UserResponse {
  organization: Organization;
}

export interface LoginRequest {
  email: string;
  password: string;
  userType: 'volunteer' | 'organization';
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  userType: 'volunteer' | 'organization';
  message: string;
}

export interface VolunteerRegisterRequest extends CreateUserRequest, Partial<CreateVolunteerRequest> {}

export interface OrganizationRegisterRequest extends CreateUserRequest, CreateOrganizationRequest {}