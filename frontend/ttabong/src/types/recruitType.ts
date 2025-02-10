interface Group {
  groupId: number;
  groupName: string;
}

interface Template {
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

export interface Application {
  applicationId: number;
  status: 'PENDING' | 'APPROVED' | 'COMPLETED' | 'REJECTED';
  evaluationDone: boolean;
  isDeleted: boolean;
  createdAt: string;
  template: Template;
  recruit: Recruit;
}

export interface CreateRecruitRequest {
  templateId: number;
  deadline: string;
  activityDate: string;
  activityTime: string;
  maxVolunteer: number;
}

export interface UpdateRecruitRequest {
  deadline?: string;
  activityDate?: string;
  activityTime?: string;
  maxVolunteer?: number;
  status?: 'RECRUITING' | 'CLOSED';
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
    status: '모집중' | '모집마감' | '활동완료';
    maxVolunteer: number;
    participateVolCount: number;
    activityDate: string;
    activityStart: number;
    activityEnd: number;
    deadline: string;
    createdAt: string;
  };
}

export interface OrgRecruitsResponse {
  recruits: OrgRecruit[];
} 