import { render, screen } from '@testing-library/react';
import axios from 'axios';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import VideoPage from '../VideoPage';

// Fake axios: the tests decide what the API answers.
// isAxiosError works like the real one: it checks the isAxiosError flag of the error.
jest.mock('axios', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    isAxiosError: (value: { isAxiosError?: boolean }) => value.isAxiosError === true,
  },
}));
const mockedGet = axios.get as jest.Mock;

const videoDetails = {
  id: 7,
  title: 'Learning Java in 30 seconds',
  description: 'A very short Java lesson.',
  author: 'Code Channel',
  tags: ['java', 'programming'],
  videoUrl: '/media/0.mp4',
  thumbnailUrl: '/media/0.webp',
  duration: 30.5,
};

function renderVideoPageAt(url: string) {
  render(
    <MemoryRouter initialEntries={[url]}>
      <Routes>
        <Route path="/videos/:id" element={<VideoPage />} />
      </Routes>
    </MemoryRouter>
  );
}

describe('VideoPage', () => {
  beforeEach(() => {
    mockedGet.mockReset();
  });

  it('asks the API for the video of the url', async () => {
    mockedGet.mockResolvedValue({ data: videoDetails });

    renderVideoPageAt('/videos/7');
    await screen.findByRole('heading', { name: 'Learning Java in 30 seconds' });

    // In the tests API_BASE_URL is "other" (see jest.setup.ts)
    expect(mockedGet).toHaveBeenCalledWith('other/videos/7');
  });

  it('shows a loading message while the video arrives', () => {
    mockedGet.mockReturnValue(new Promise(() => {}));

    renderVideoPageAt('/videos/7');

    expect(screen.getByText('Loading video...')).toBeInTheDocument();
  });

  it('shows the player with the title, author and description', async () => {
    mockedGet.mockResolvedValue({ data: videoDetails });

    renderVideoPageAt('/videos/7');

    expect(await screen.findByRole('heading', { name: 'Learning Java in 30 seconds' })).toBeInTheDocument();
    expect(screen.getByLabelText('Learning Java in 30 seconds')).toHaveAttribute('src', 'development/0.mp4');
    expect(screen.getByText('By Code Channel')).toBeInTheDocument();
    expect(screen.getByText('A very short Java lesson.')).toBeInTheDocument();
  });

  it('shows the tags of the video', async () => {
    mockedGet.mockResolvedValue({ data: videoDetails });

    renderVideoPageAt('/videos/7');
    await screen.findByRole('heading', { name: 'Learning Java in 30 seconds' });

    expect(screen.getByText('java')).toBeInTheDocument();
    expect(screen.getByText('programming')).toBeInTheDocument();
  });

  it('shows "Video not found." and no player when the API answers 404', async () => {
    mockedGet.mockRejectedValue({ isAxiosError: true, response: { status: 404 } });

    renderVideoPageAt('/videos/999');

    expect(await screen.findByText('Video not found.')).toBeInTheDocument();
    expect(screen.queryByLabelText('Learning Java in 30 seconds')).not.toBeInTheDocument();
  });

  it('shows a clear message when the API fails for another reason', async () => {
    mockedGet.mockRejectedValue(new Error('Network Error'));

    renderVideoPageAt('/videos/7');

    expect(await screen.findByText('Could not load the video.')).toBeInTheDocument();
  });
});
