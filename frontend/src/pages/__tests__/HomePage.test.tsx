import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import axios from 'axios';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import { setupStore } from '../../store/store';
import HomePage from '../HomePage';

jest.mock('axios', () => ({
  __esModule: true,
  default: { get: jest.fn() },
}));
const mockedGet = axios.get as jest.Mock;

function createVideos(howMany: number) {
  const videos = [];
  for (let index = 1; index <= howMany; index++) {
    videos.push({ id: index, title: 'Video number ' + index, thumbnailUrl: '/media/' + index + '.webp', duration: 30 });
  }
  return videos;
}

function renderHomePage() {
  render(
    <Provider store={setupStore()}>
      <MemoryRouter>
        <HomePage />
      </MemoryRouter>
    </Provider>
  );
}

describe('HomePage', () => {
  beforeEach(() => {
    mockedGet.mockReset();
  });

  it('shows a loading message while the videos arrive', () => {
    mockedGet.mockReturnValue(new Promise(() => {}));

    renderHomePage();

    expect(screen.getByText('Loading videos...')).toBeInTheDocument();
  });

  it('shows each video with its thumbnail and title', async () => {
    mockedGet.mockResolvedValue({ data: createVideos(2) });

    renderHomePage();

    expect(await screen.findByText('Video number 1')).toBeInTheDocument();
    expect(screen.getByText('Video number 2')).toBeInTheDocument();
    expect(screen.getByRole('img', { name: 'Video number 1' })).toBeInTheDocument();
  });

  it('shows a clear message when the API fails', async () => {
    mockedGet.mockRejectedValue(new Error('Network Error'));

    renderHomePage();

    expect(await screen.findByText('Could not load the videos.')).toBeInTheDocument();
  });

  it('shows a message when there are no videos', async () => {
    mockedGet.mockResolvedValue({ data: [] });

    renderHomePage();

    expect(await screen.findByText('There are no videos yet.')).toBeInTheDocument();
  });

  it('asks for the next page when the user clicks "Next page"', async () => {
    const user = userEvent.setup();
    mockedGet.mockResolvedValue({ data: createVideos(12) });
    renderHomePage();
    await screen.findByText('Video number 1');

    await user.click(screen.getByRole('button', { name: 'Next page' }));

    expect(mockedGet).toHaveBeenLastCalledWith('other/videos?page=1&size=12');
  });

  it('disables "Previous page" on the first page', async () => {
    mockedGet.mockResolvedValue({ data: createVideos(12) });

    renderHomePage();
    await screen.findByText('Video number 1');

    expect(screen.getByRole('button', { name: 'Previous page' })).toBeDisabled();
  });

  it('disables "Next page" when the page is not full', async () => {
    mockedGet.mockResolvedValue({ data: createVideos(3) });

    renderHomePage();
    await screen.findByText('Video number 1');

    expect(screen.getByRole('button', { name: 'Next page' })).toBeDisabled();
  });
});
