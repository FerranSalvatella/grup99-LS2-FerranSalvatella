import '@testing-library/jest-dom';
import { TextDecoder, TextEncoder } from 'util';

// React Router 7 needs TextEncoder/TextDecoder, which the jsdom test environment does not provide
Object.assign(global, { TextDecoder, TextEncoder });

import fetchMock from 'jest-fetch-mock';

jest.mock('./src/utils/Env', () => ({
  getEnv: () => ({
    API_BASE_URL: 'other',
    MEDIA_BASE_URL: 'development',
  }),
}));

fetchMock.enableMocks();
