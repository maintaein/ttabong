const config = {
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  apiPrefix: '/api',
  timeout: 5000,
} as const;

export default config;
