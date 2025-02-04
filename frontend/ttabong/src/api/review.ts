import axios from 'axios';
import type { Review } from '@/types/review';

const BASE_URL = 'https://ttabong.store/api';

export const reviewApi = {
  getReviews: async (): Promise<Review[]> => {
    const response = await axios.get(`${BASE_URL}/reviews`);
    return response.data;
  },
}; 