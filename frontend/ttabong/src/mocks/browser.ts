import { setupWorker } from 'msw/browser';
import { handlers } from './handlers';

export const worker = setupWorker(...handlers);

if (import.meta.env.DEV) {
  worker.start({
    onUnhandledRequest: 'bypass',
  });
} 