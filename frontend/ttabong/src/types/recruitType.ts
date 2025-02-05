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
}

interface Recruit {
  recruitId: number;
  deadline: string;
  activityDate: string;
  activityTime: string;
  maxVolunteer: number;
  participateVolCount: number;
  status: 'RECRUITING' | 'CLOSED';
  isDeleted: boolean;
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