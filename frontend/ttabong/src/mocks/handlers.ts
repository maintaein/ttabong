import { http, HttpResponse } from 'msw';

const reviews = [
  {
    reviewId: 1,
    thumbnailImageUrl: 'https://picsum.photos/300/300?random=1',
  },
  {
    reviewId: 2,
    thumbnailImageUrl: 'https://picsum.photos/300/300?random=2',
  },
  // ... 더 많은 목데이터
];

export const handlers = [
  http.get('/api/reviews', () => {
    return HttpResponse.json(reviews);
  }),
]; 