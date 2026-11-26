import axios from 'axios';
import { setupStore } from '../store';
import { fetchVideos } from '../videosSlice';

// Fake axios: the tests decide what the API answers
jest.mock('axios', () => ({
  __esModule: true,
  default: { get: jest.fn() },
}));
const mockedGet = axios.get as jest.Mock;

const apiVideos = [
  { id: 1, title: 'Learning Java in 30 seconds', thumbnailUrl: '/media/0.webp', duration: 30.5 },
  { id: 2, title: 'Relaxing piano music', thumbnailUrl: '/media/1.webp', duration: 65 },
];

describe('videosSlice', () => {
  beforeEach(() => {
    mockedGet.mockReset();
  });

  it('asks the API for the requested page with 12 videos per page', async () => {
    mockedGet.mockResolvedValue({ data: apiVideos });
    const store = setupStore();

    await store.dispatch(fetchVideos(2));

    // In the tests API_BASE_URL is "other" (see jest.setup.ts)
    expect(mockedGet).toHaveBeenCalledWith('other/videos?page=2&size=12');
  });

  it('saves the videos and the success status when the API answers', async () => {
    mockedGet.mockResolvedValue({ data: apiVideos });
    const store = setupStore();

    await store.dispatch(fetchVideos(0));

    expect(store.getState().videos.status).toBe('success');
    expect(store.getState().videos.videos).toEqual(apiVideos);
  });

  it('saves the error status when the API fails', async () => {
    mockedGet.mockRejectedValue(new Error('Network Error'));
    const store = setupStore();

    await store.dispatch(fetchVideos(0));

    expect(store.getState().videos.status).toBe('error');
  });

  it('saves the loading status while waiting for the API', () => {
    mockedGet.mockReturnValue(new Promise(() => {}));
    const store = setupStore();

    store.dispatch(fetchVideos(0));

    expect(store.getState().videos.status).toBe('loading');
  });
});
