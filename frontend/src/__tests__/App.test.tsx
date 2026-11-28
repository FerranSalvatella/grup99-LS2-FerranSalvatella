import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import axios from 'axios';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import App from '../App';
import { setupStore } from '../store/store';

// The home page loads videos: the API is faked so the tests do not need the backend
jest.mock('axios', () => ({
  __esModule: true,
  default: { get: jest.fn(), post: jest.fn() },
}));
const mockedGet = axios.get as jest.Mock;
const mockedPost = axios.post as jest.Mock;

function renderAppAt(url: string) {
  render(
    <Provider store={setupStore()}>
      <MemoryRouter initialEntries={[url]}>
        <App />
      </MemoryRouter>
    </Provider>
  );
}

const videoSeven = {
  id: 7,
  title: 'Learning Java in 30 seconds',
  description: 'A very short Java lesson.',
  author: 'Code Channel',
  tags: ['java'],
  videoUrl: '/media/0.mp4',
  thumbnailUrl: '/media/0.webp',
  duration: 30.5,
};

// The fake API answers the detail of video 7, an empty list for the home page,
// and 401 for "who is logged in" (nobody has a session)
function answerLikeTheApi(url: string) {
  if (url === 'other/auth/me') {
    return Promise.reject({ response: { status: 401 } });
  }
  if (url === 'other/videos/7') {
    return Promise.resolve({ data: videoSeven });
  }
  return Promise.resolve({ data: [] });
}

describe('App routes', () => {
  beforeEach(() => {
    mockedGet.mockImplementation(answerLikeTheApi);
  });

  it('shows the home page at /', async () => {
    renderAppAt('/');

    expect(screen.getByRole('heading', { name: 'Latest videos' })).toBeInTheDocument();
    expect(await screen.findByText('There are no videos yet.')).toBeInTheDocument();
  });

  it('shows the video page of the id of the url at /videos/7', async () => {
    renderAppAt('/videos/7');

    expect(await screen.findByRole('heading', { name: 'Learning Java in 30 seconds' })).toBeInTheDocument();
  });

  it('shows the not found page for an unknown url', () => {
    renderAppAt('/this-page-does-not-exist');

    expect(screen.getByRole('heading', { name: 'Page not found' })).toBeInTheDocument();
  });

  it('shows the login page at /login', () => {
    renderAppAt('/login');

    expect(screen.getByRole('heading', { name: 'Log in' })).toBeInTheDocument();
  });

  it('shows the register page at /register', () => {
    renderAppAt('/register');

    expect(screen.getByRole('heading', { name: 'Create an account' })).toBeInTheDocument();
  });

  it('after login goes to the home page and shows the name of the user in the menu', async () => {
    mockedPost.mockResolvedValue({ data: { id: 1, username: 'anna', email: 'anna@example.com' } });
    renderAppAt('/login');

    await userEvent.type(screen.getByLabelText('Email'), 'anna@example.com');
    await userEvent.type(screen.getByLabelText('Password'), 'secret123');
    await userEvent.click(screen.getByRole('button', { name: 'Log in' }));

    expect(await screen.findByRole('heading', { name: 'Latest videos' })).toBeInTheDocument();
    expect(within(screen.getByRole('navigation')).getByText('anna')).toBeInTheDocument();
  });

  it('shows the navigation bar on every page', async () => {
    renderAppAt('/videos/7');

    expect(screen.getByRole('navigation')).toBeInTheDocument();
    await screen.findByRole('heading', { name: 'Learning Java in 30 seconds' });
  });

  // ----- PG-29: the session is restored when the app starts (for example after F5) -----

  it('asks the backend who is logged in when the app starts', async () => {
    renderAppAt('/');

    expect(mockedGet).toHaveBeenCalledWith('other/auth/me', { withCredentials: true });
    await screen.findByText('There are no videos yet.');
  });

  it('shows the name of the user in the menu when the session is still open', async () => {
    mockedGet.mockImplementation((url: string) => {
      if (url === 'other/auth/me') {
        return Promise.resolve({ data: { id: 1, username: 'anna', email: 'anna@example.com' } });
      }
      return Promise.resolve({ data: [] });
    });

    renderAppAt('/');

    expect(await within(screen.getByRole('navigation')).findByText('anna')).toBeInTheDocument();
  });
});
