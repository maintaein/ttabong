import { http, HttpResponse } from 'msw';

// 리뷰 목록 데이터 (50개로 증가)
const reviews = Array.from({ length: 50 }, (_, i) => ({
  review: {
    reviewId: 1000 + i,
    recruitId: 500 + i,
    title: i % 3 === 0 
      ? "환경 정화 활동 후기" 
      : i % 3 === 1 
        ? "노인 복지센터 방문 후기"
        : "아동 센터 봉사 후기",
    content: i % 3 === 0 
      ? "이번 봉사는 정말 뜻깊은 경험이었습니다!" 
      : i % 3 === 1
        ? "어르신들과 함께 시간을 보내며 많은 것을 배웠습니다."
        : "아이들과 함께한 즐거운 시간이었습니다.",
    isDeleted: false,
    updatedAt: new Date(2025, 1, 10 + Math.floor(i/10)).toISOString(),
    createdAt: new Date(2025, 1, 5 + Math.floor(i/10)).toISOString()
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

export const handlers = [
  // 리뷰 목록 조회
  http.get('/api/reviews', ({ request }) => {
    const url = new URL(request.url);
    const cursor = Number(url.searchParams.get('cursor')) || 0;
    const limit = Number(url.searchParams.get('limit')) || 9;
    
    const startIndex = cursor === 0 ? 0 : reviews.findIndex(r => r.review.reviewId === cursor) + 1;
    const endIndex = startIndex + limit;
    const slicedReviews = reviews.slice(startIndex, endIndex);
    
    return HttpResponse.json({
      reviews: slicedReviews,
      hasMore: endIndex < reviews.length
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
    const reviewData = await request.json() as Record<string, any>;
    return HttpResponse.json({
      reviewId: Math.random(),
      ...reviewData
    }, { status: 201 });
  }),

  // 리뷰 삭제 API
  http.patch('/api/reviews/:reviewId/delete', ({ params }) => {
    const { reviewId } = params;
    console.log(`Review ${reviewId} deleted`);
    return new HttpResponse(null, { status: 204 });
  }),

  // 리뷰 공개여부 토글 API
  http.patch('/api/reviews/:reviewId/toggle-public', async ({ request, params }) => {
    const { reviewId } = params;
    const { isPublic } = await request.json() as { isPublic: boolean };
    console.log(`Review ${reviewId} visibility toggled from ${isPublic} to ${!isPublic}`);
    return new HttpResponse(null, { status: 204 });
  }),
]; 