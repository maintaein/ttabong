import { OrgRecruit } from '@/types/recruitType';
import { http, HttpResponse } from 'msw';

// 리뷰 목록 데이터
const reviews = Array.from({ length: 20 }, (_, i) => ({
  review: {
    reviewId: 100 + i,
    recruitId: 55 + i,
    title: i % 2 === 0 ? "환경 정화 활동 후기" : "노인 복지센터 방문 후기",
    content: i % 2 === 0 
      ? "이번 봉사는 정말 뜻깊은 경험이었습니다!" 
      : "어르신들과 함께 시간을 보내며 많은 것을 배웠습니다.",
    isDeleted: false,
    updatedAt: "2025-02-10T14:30:00",
    createdAt: "2025-02-05T12:00:00"
  },
  writer: {
    writerId: 10,
    name: "김봉사"
  },
  group: {
    groupId: i % 2 === 0 ? 5 : 7,
    groupName: i % 2 === 0 ? "환경 보호 단체" : "노인 돌봄 봉사단"
  },
  organization: {
    orgId: i % 2 === 0 ? 3 : 4,
    orgName: i % 2 === 0 ? "서울 환경재단" : "대한 복지재단"
  },
  images: [
    `https://picsum.photos/300/300?random=${i}_1`,
    `https://picsum.photos/300/300?random=${i}_2`
  ]
}));

// 리뷰 상세 데이터 생성 함수
function generateReviewDetail(id: number) {
  const baseReview = {
    reviewId: id,
    title: `후기제목 ${id}`,
    content: "이 봉사활동은 정말 보람찼습니다!",
    isDeleted: false,
    isPublic: true,
    attended: true,
    createdAt: "2024-02-02T12:00:00",
    images: [
      `https://picsum.photos/300/300?random=${id}_1`,
      `https://picsum.photos/300/300?random=${id}_2`
    ],
    recruit: {
      recruitId: 101,
      activityDate: "2024-02-15",
      activityTime: "10:00 ~ 14:00",
      status: "RECRUITING"
    },
    category: {
      categoryId: 5,
      name: "환경 보호"
    },
    writer: {
      writerId: 12,
      writerName: "김봉사",
      writerProfileImage: `https://picsum.photos/100/100?random=${id}_writer`
    },
    template: {
      templateId: 1,
      title: "환경 보호 봉사",
      activityLocation: "서울특별시 강남구 테헤란로 123",
      status: "ALL",
      group: {
        groupId: 10,
        groupName: "환경 봉사팀"
      }
    },
    organization: {
      orgId: 50,
      orgName: "서울 환경 봉사 센터"
    },
    comments: [
      {
        commentId: id * 100 + 1,
        writerId: 14,
        writerName: "이봉사",
        content: "정말 멋진 봉사활동 후기네요!",
        createdAt: "2024-02-03T15:00:00"
      },
      {
        commentId: id * 100 + 2,
        writerId: 20,
        writerName: "박봉사",
        content: "저도 참여하고 싶어요!",
        createdAt: "2024-02-04T18:20:00"
      }
    ]
  };

  // id가 짝수일 때만 orgReviewId 추가
  if (id % 2 === 0) {
    return {
      ...baseReview,
      orgReviewId: 99
    };
  }

  return baseReview;
}

// 봉사활동별 리뷰 목록 데이터 생성 함수
function generateRecruitReviews(recruitId: number) {
  return Array.from({ length: 5 }, (_, i) => ({
    review: {
      reviewId: recruitId * 100 + i,
      recruitId,
      title: `봉사활동 후기 ${i + 1}`,
      content: "이번 봉사는 정말 뜻깊은 경험이었습니다!",
      isDeleted: false,
      updatedAt: "2024-02-10T14:30:00",
      createdAt: "2024-02-05T12:00:00"
    },
    writer: {
      writerId: 10 + i,
      name: `봉사자${i + 1}`
    },
    group: {
      groupId: 5,
      groupName: "환경 보호 단체"
    },
    organization: {
      orgId: 3,
      orgName: "서울 환경재단"
    },
    images: [
      `https://picsum.photos/300/300?random=${recruitId}_${i}_1`,
      `https://picsum.photos/300/300?random=${recruitId}_${i}_2`
    ]
  }));
}

interface ReviewSubmission {
  title: string;
  content: string;
  recruitId: number;
  images?: string[];
}

interface MockTemplate {
  templateId: number;
  groupId: number;
  createdAt: string;
  title?: string;
  description?: string;
  images?: string[];
  volunteerTypes?: string[];
  volunteerCount?: number;
  activityLocation?: string;
  contactName?: string;
  contactPhone?: string;
  startDate?: string;
  endDate?: string;
  volunteerDate?: string;
  startTime?: string;
  endTime?: string;
  volunteerField?: string[];
  locationType?: string;
}

// 임시 데이터 저장소
let mockGroups = [
  {
    groupId: 1,
    groupName: "봉사 그룹1",
    templates: [] as MockTemplate[]
  },
  {
    groupId: 2,
    groupName: "봉사 그룹2",
    templates: [] as MockTemplate[]
  }
];

const mockRecruits: OrgRecruit[] = [];

export const handlers = [
  // 리뷰 목록 조회
  http.get('/api/reviews', () => {
    return new HttpResponse(JSON.stringify(reviews), {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    });
  }),

  // 리뷰 상세 조회
  http.get('/api/reviews/:id', ({ params }) => {
    const { id } = params;
    return new HttpResponse(JSON.stringify(generateReviewDetail(Number(id))), {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    });
  }),

  // 댓글 작성
  http.post('/api/reviews/:id/comments', async ({ request }) => {
    const { content } = await request.json() as { content: string };
    return new HttpResponse(JSON.stringify({
      commentId: Math.floor(Math.random() * 1000),
      writerId: 1,
      writerName: "테스트 사용자",
      content,
      createdAt: new Date().toISOString()
    }), {
      status: 201,
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    });
  }),

  // 봉사활동별 리뷰 목록 조회
  http.get('/api/recruits/:recruitId/reviews', ({ params }) => {
    const { recruitId } = params;
    return HttpResponse.json(generateRecruitReviews(Number(recruitId)));
  }),

  // CORS Preflight 처리
  http.options('*', () => {
    return new HttpResponse(null, {
      headers: {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type, Authorization',
      },
    });
  }),

  // 내 봉사활동 목록 조회
  http.get('/api/applications/mine', () => {
    return HttpResponse.json([
      {
        applicationId: 55,
        status: "PENDING",
        evaluationDone: false,
        isDeleted: false,
        createdAt: "2024-02-02T12:00:00",
        template: {
          templateId: 1,
          title: "환경 보호 봉사",
          activityLocation: "서울특별시 강남구 테헤란로 123",
          status: "ALL",
          imageId: "https://example.com/image.jpg",
          contactName: "김봉사",
          contactPhone: "010-1234-5678",
          description: "환경 보호를 위한 봉사활동입니다.",
          isDeleted: false,
          createdAt: "2024-02-02T12:00:00",
          group: {
            groupId: 10,
            groupName: "환경 봉사팀"
          }
        },
        recruit: {
          recruitId: 101,
          deadline: "2024-02-10T23:59:59",
          activityDate: "2024-02-15",
          activityTime: "10:00 ~ 14:00",
          maxVolunteer: 20,
          participateVolCount: 15,
          status: "RECRUITING",
          isDeleted: false,
          createdAt: "2024-02-02T12:00:00"
        }
      },
      {
        applicationId: 56,
        status: "APPROVED",
        evaluationDone: false,
        isDeleted: false,
        createdAt: "2024-02-04T15:00:00",
        template: {
          templateId: 2,
          title: "노인 돌봄 봉사",
          activityLocation: "서울특별시 종로구",
          status: "YOUTH",
          imageId: "https://example.com/image2.jpg",
          contactName: "이봉사",
          contactPhone: "010-5678-1234",
          description: "어르신들을 위한 봉사활동입니다.",
          isDeleted: false,
          createdAt: "2024-02-04T15:00:00",
          group: {
            groupId: 11,
            groupName: "노인 돌봄 팀"
          }
        },
        recruit: {
          recruitId: 201,
          deadline: "2024-03-05T23:59:59",
          activityDate: "2024-03-10",
          activityTime: "13:00 ~ 17:00",
          maxVolunteer: 30,
          participateVolCount: 25,
          status: "RECRUITING",
          isDeleted: false,
          createdAt: "2024-02-10T16:45:00"
        }
      }
    ]);
  }),

  // 리뷰 작성 API
  http.post('/api/reviews', async ({ request }) => {
    const reviewData = await request.json() as ReviewSubmission;
    return HttpResponse.json({
      reviewId: Math.random(),
      ...reviewData
    }, { status: 201 });
  }),

  // 그룹 목록 조회
  http.get('/api/org/templates', () => {
    return HttpResponse.json({
      groups: mockGroups
    });
  }),

  // 그룹 생성
  http.post('/api/org/groups', async ({ request }) => {
    const { groupName } = await request.json() as { groupName: string };
    const newGroup = {
      groupId: Date.now(),
      groupName,
      templates: []
    };
    mockGroups.push(newGroup);
    
    return HttpResponse.json({
      message: "그룹 생성 성공",
      groupId: newGroup.groupId
    }, { status: 201 });
  }),

  // 그룹 삭제
  http.patch('/api/org/groups/delete', async ({ request }) => {
    const { groupId } = await request.json() as { groupId: number; orgId: number };
    mockGroups = mockGroups.filter(group => group.groupId !== groupId);
    
    return HttpResponse.json({
      message: "삭제 성공",
      groupId
    });
  }),

  // 템플릿 생성
  http.post('/api/org/templates', async ({ request }) => {
    const templateData = await request.json() as MockTemplate;
    const targetGroup = mockGroups.find(g => g.groupId === templateData.groupId);
    
    if (targetGroup) {
      const newTemplate = {
        templateId: Date.now(),
        groupId: templateData.groupId,
        title: templateData.title,
        description: templateData.description,
        images: templateData.images || [],
        volunteerTypes: templateData.volunteerTypes || [],
        volunteerCount: templateData.volunteerCount || 10,
        activityLocation: templateData.locationType,
        contactName: templateData.contactName,
        contactPhone: templateData.contactPhone,
        volunteerField: templateData.volunteerField || [],
        startDate: templateData.startDate,
        endDate: templateData.endDate,
        volunteerDate: templateData.volunteerDate,
        startTime: templateData.startTime,
        endTime: templateData.endTime,
        createdAt: new Date().toISOString()
      };
      
      targetGroup.templates.push(newTemplate);
      
      return HttpResponse.json({
        message: "템플릿 생성 성공",
        templateId: newTemplate.templateId
      }, { status: 201 });
    }
    
    return HttpResponse.json({
      message: "그룹을 찾을 수 없습니다."
    }, { status: 404 });
  }),

  // 템플릿 수정
  http.patch('/api/org/templates/:templateId', async ({ params, request }) => {
    const templateId = Number(params.templateId);
    const templateData = await request.json() as MockTemplate;
    
    for (const group of mockGroups) {
      const template = group.templates.find(t => t.templateId === templateId);
      if (template) {
        Object.assign(template, {
          groupId: templateData.groupId,
          title: templateData.title,
          description: templateData.description,
          images: templateData.images || [],
          volunteerTypes: templateData.volunteerTypes || [],
          volunteerCount: templateData.volunteerCount || 10,
          activityLocation: templateData.locationType,
          contactName: templateData.contactName,
          contactPhone: templateData.contactPhone,
          volunteerField: templateData.volunteerField || [],
          startDate: templateData.startDate,
          endDate: templateData.endDate,
          volunteerDate: templateData.volunteerDate,
          startTime: templateData.startTime,
          endTime: templateData.endTime
        });
        return HttpResponse.json({
          message: "템플릿 수정 성공",
          templateId
        });
      }
    }
    
    return HttpResponse.json({
      message: "템플릿을 찾을 수 없습니다."
    }, { status: 404 });
  }),

  // 템플릿 상세 조회
  http.get('/api/org/templates/:templateId', ({ params }) => {
    const templateId = Number(params.templateId);
    for (const group of mockGroups) {
      const template = group.templates.find(t => t.templateId === templateId);
      if (template) {
        return HttpResponse.json(template);
      }
    }
    return HttpResponse.json({ message: "템플릿을 찾을 수 없습니다." }, { status: 404 });
  }),

  // 템플릿 삭제
  http.delete('/api/org/templates/:templateId', async ({ params }) => {
    const templateId = Number(params.templateId);
    
    for (const group of mockGroups) {
      const templateIndex = group.templates.findIndex(t => t.templateId === templateId);
      if (templateIndex !== -1) {
        group.templates.splice(templateIndex, 1);
        return HttpResponse.json({
          message: "템플릿 삭제 성공",
          templateId
        });
      }
    }
    
    return HttpResponse.json({
      message: "템플릿을 찾을 수 없습니다."
    }, { status: 404 });
  }),

  // 공고 생성
  http.post('/api/org/recruits', async ({ request }) => {
    const data = await request.json() as {
      templateId: number;
      deadline: string;
      activityDate: string;
      activityStart: number;
      activityEnd: number;
      maxVolunteer: number;
    };
    console.log('Creating recruit with template:', findTemplateById(data.templateId));
    console.log('Creating recruit with group:', findGroupByTemplateId(data.templateId));
    
    const template = findTemplateById(data.templateId);
    const group = findGroupByTemplateId(data.templateId);
    
    if (!template || !group) {
      return HttpResponse.json(
        { message: "템플릿 또는 그룹을 찾을 수 없습니다." },
        { status: 404 }
      );
    }
    
    // 새 공고 생성
    const newRecruit: OrgRecruit = {
      group: {
        groupId: group.groupId,
        groupName: group.groupName
      },
      template: {
        templateId: data.templateId,
        title: template.title || '',
        description: template.description || '',
        activityLocation: template.activityLocation || '',
        volunteerTypes: template.volunteerTypes || [],
        contactName: template.contactName || '',
        contactPhone: template.contactPhone || '',
        images: template.images || [],
        volunteerField: template.volunteerField || []
      },
      recruit: {
        recruitId: Date.now(),
        status: '모집중',
        maxVolunteer: data.maxVolunteer,
        participateVolCount: 0,
        activityDate: data.activityDate,
        activityStart: data.activityStart,
        activityEnd: data.activityEnd,
        deadline: data.deadline,
        createdAt: new Date().toISOString()
      }
    };

    console.log('New recruit created:', newRecruit);
    mockRecruits.push(newRecruit);
    console.log('Current mockRecruits:', mockRecruits);
    
    return new HttpResponse(
      JSON.stringify({
        message: "공고 생성 완료",
        recruitId: newRecruit.recruit.recruitId
      }), 
      {
        status: 201,
        headers: {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        },
      }
    );
  }),

  // 기관 공고 목록 조회
  http.get('/api/org/recruits', () => {
    return HttpResponse.json({
      recruits: mockRecruits // mockRecruits 배열 필요
    });
  }),

  // 공고 삭제
  http.patch('/api/org/recruits/delete', async ({ request }) => {
    const { deletedRecruits } = await request.json() as { deletedRecruits: number };
    
    const index = mockRecruits.findIndex(
      recruit => recruit.recruit.recruitId === deletedRecruits
    );
    
    if (index !== -1) {
      mockRecruits.splice(index, 1);
      return HttpResponse.json({
        message: "삭제 성공",
        deletedRecruits
      });
    }
    
    return HttpResponse.json({ message: "공고를 찾을 수 없습니다." }, { status: 404 });
  }),

  // 공고 수정
  http.patch('/org/recruits/:recruitId', async ({ params, request }) => {
    const recruitId = Number(params.recruitId);
    const data = await request.json() as {
      deadline?: string;
      activityDate?: string;
      activityStart?: number;
      activityEnd?: number;
      maxVolunteer?: number;
      images?: string[];
      imageCount?: number;
    };
    
    const recruitIndex = mockRecruits.findIndex(
      recruit => recruit.recruit.recruitId === recruitId
    );
    
    if (recruitIndex === -1) {
      return HttpResponse.json(
        { message: "공고를 찾을 수 없습니다." }, 
        { status: 404 }
      );
    }

    // 기존 공고 데이터 업데이트
    const updatedRecruit = mockRecruits[recruitIndex];
    Object.assign(updatedRecruit.recruit, {
      deadline: data.deadline || updatedRecruit.recruit.deadline,
      activityDate: data.activityDate || updatedRecruit.recruit.activityDate,
      activityStart: data.activityStart ?? updatedRecruit.recruit.activityStart,
      activityEnd: data.activityEnd ?? updatedRecruit.recruit.activityEnd,
      maxVolunteer: data.maxVolunteer ?? updatedRecruit.recruit.maxVolunteer
    });

    mockRecruits[recruitIndex] = updatedRecruit;

    return HttpResponse.json({
      message: "수정 성공",
      recruitId
    });
  }),

  // 공고 상세 조회
  http.get('/org/recruits/:recruitId', ({ params }) => {
    const recruitId = Number(params.recruitId);
    console.log('Searching for recruitId:', recruitId);
    console.log('Available recruits:', mockRecruits);
    console.log('mockRecruits type:', typeof mockRecruits);
    console.log('mockRecruits length:', mockRecruits.length);
    const recruit = mockRecruits.find(r => r.recruit.recruitId === recruitId);
    
    console.log('Found recruit:', recruit);
    if (recruit) {
      console.log('Recruit structure:', {
        hasGroup: !!recruit.group,
        hasTemplate: !!recruit.template,
        hasRecruit: !!recruit.recruit,
      });
    }
    
    if (!recruit) {
      return HttpResponse.json(
        { message: "공고를 찾을 수 없습니다." }, 
        { status: 404 }
      );
    }

    return new HttpResponse(JSON.stringify(recruit), {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    });
  }),
];

function findGroupByTemplateId(templateId: number) {
  for (const group of mockGroups) {
    if (group.templates.some(t => t.templateId === templateId)) {
      return group;
    }
  }
  return null;
}

function findTemplateById(templateId: number) {
  for (const group of mockGroups) {
    const template = group.templates.find(t => t.templateId === templateId);
    if (template) return template;
  }
  return null;
} 