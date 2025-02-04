import { http, HttpResponse } from 'msw';

// 리뷰 목록 데이터
const reviews = Array.from({ length: 20 }, (_, i) => ({
  reviewId: i + 1,
  thumbnailImageUrl: `https://picsum.photos/300/300?random=${i + 1}`,
}));

// 리뷰 상세 데이터 생성 함수
function generateReviewDetail(id: number) {
  return {
    reviewId: id,
    title: `따뜻한 마음으로 함께한 봉사활동 ${id}`,
    content: "이 봉사활동은 정말 보람찼습니다!",
    isDeleted: false,
    isPublic: true,
    attended: true,
    createdAt: "2024-02-02T12:00:00",
    images: [
      `https://picsum.photos/800/600?random=${id}`,
      `https://picsum.photos/800/600?random=${id + 100}`
    ],
    recruit: {
      recruitId: id + 100,
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
      writerProfileImage: `https://picsum.photos/100/100?random=${id}`
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
    orgReviewId: id % 2 === 0 ? id + 1000 : undefined, // 짝수 ID는 개인 리뷰, 홀수 ID는 기관 리뷰
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
}

// 봉사활동별 리뷰 목록 데이터 생성 함수
function generateRecruitReviews(recruitId: number) {
  return Array.from({ length: 6 }, (_, i) => ({
    reviewId: recruitId * 100 + i,
    thumbnailImageUrl: `https://picsum.photos/300/300?random=${recruitId * 100 + i}`,
  }));
}

export const handlers = [
  // 리뷰 목록 조회
  http.get('/api/reviews', () => {
    return HttpResponse.json(reviews);
  }),

  // 리뷰 상세 조회
  http.get('/api/reviews/:id', ({ params }) => {
    const { id } = params;
    return HttpResponse.json(generateReviewDetail(Number(id)));
  }),

  // 댓글 작성
  http.post('/api/reviews/:id/comments', async ({ request }) => {
    const { content } = await request.json() as { content: string };
    return HttpResponse.json({
      commentId: Math.floor(Math.random() * 1000),
      writerId: 1,
      writerName: "테스트 사용자",
      content,
      createdAt: new Date().toISOString()
    }, { status: 201 });
  }),

  // 봉사활동별 리뷰 목록 조회
  http.get('/api/recruits/:recruitId/reviews', ({ params }) => {
    const { recruitId } = params;
    return HttpResponse.json(generateRecruitReviews(Number(recruitId)));
  }),
]; 