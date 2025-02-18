export interface Group {
  groupId: number;
  groupName: string;
}

export interface Template {
  templateId: number;
  title: string;
  activityLocation: string;
  status: 'ALL' | 'YOUTH';
  imageId: string;
  contactName: string;
  contactPhone: string;
  description: string;
  isDeleted: boolean;
  createdAt: string;
  group: Group;
  images?: string[];
  volunteerField?: string[];
}

export interface Recruit {
  recruitId: number;
  templateId: number;
  deadline: string;
  activityDate: string;
  activityTime: string;
  maxVolunteer: number;
  participateVolCount: number;
  status: 'RECRUITING' | 'CLOSED';
  isDeleted: boolean;
  updatedAt: string;
  createdAt: string;
}

export type VolunteerApplicationStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'COMPLETED' | 'AUTO_CANCEL' | 'NO_SHOW';
export type OrgRecruitStatus = 'RECRUITING' | 'RECRUITMENT_CLOSED' | 'ACTIVITY_COMPLETED';

export interface Application {
  applicationId: number;
  status: VolunteerApplicationStatus;
  evaluationDone: boolean;
  createdAt: string;
  template: {
    templateId: number;
    title: string;
    activityLocation: string;
    status: 'ALL' | 'YOUTH';
    imageId: string;
    contactName: string;
    contactPhone: string;
    description: string;
    createdAt: string;
  };
  group: {
    groupId: number;
    groupName: string;
  };
  recruit: {
    recruitId: number;
    deadline: string;
    activityDate: string;
    activityStart: number;
    activityEnd: number;
    maxVolunteer: number;
    participateVolCount: number;
    status: 'RECRUITING' | 'CLOSED';
    createdAt: string;
  };
}

export interface CreateRecruitRequest {
  templateId: number;
  deadline: string;
  activityDate: string;
  activityStart: number;
  activityEnd: number;
  maxVolunteer: number;
}

export interface UpdateRecruitRequest {
  deadline?: string;
  activityDate?: string;
  activityStart?: number;
  activityEnd?: number;
  maxVolunteer?: number;
  images?: string[];
  imageCount?: number;
}

export interface OrgRecruit {
  group: {
    groupId: number;
    groupName: string;
  };
  template: {
    templateId: number;
    title: string;
    description: string;
    activityLocation: string;
    volunteerTypes: string[];
    contactName: string;
    contactPhone: string;
    images?: string[];
    volunteerField?: string[];
  };
  recruit: {
    recruitId: number;
    status: OrgRecruitStatus;
    maxVolunteer: number;
    participateVolCount: number;
    activityDate: string;
    activityStart: number;
    activityEnd: number;
    deadline: string;
    createdAt: string;
  };
  application?: {
    applicationId: number;
    status: VolunteerApplicationStatus;
  };
  organization: {
    orgId: number;
    name: string;
  };
}

export interface OrgRecruitsResponse {
  recruits: OrgRecruit[];
}

export interface GetApplicationsParams {
  cursor?: number;
  limit?: number;
}

export interface RecruitDetail {
  group: {
    groupId: number;
    groupName: string;
  };
  template: {
    templateId: number;
    categoryId: number;
    title: string;
    activityLocation: string;
    status: 'ALL' | 'YOUTH' | 'ACTIVE';
    images: string[];
    imageId?: string;
    contactName: string;
    contactPhone: string;
    description: string;
    createdAt: string;
  };
  recruit: {
    recruitId: number;
    deadline: string;
    activityDate: string;
    activityStart: number;
    activityEnd: number;
    maxVolunteer: number;
    participateVolCount: number;
    status: OrgRecruitStatus | string;
    updatedAt: string;
    createdAt: string;
  };
  organization: {
    orgId: number;
    name: string;
    orgName?: string;
  };
  application?: {
    applicationId: number;
    name?: string;
    status: VolunteerApplicationStatus | string;
  };
} 