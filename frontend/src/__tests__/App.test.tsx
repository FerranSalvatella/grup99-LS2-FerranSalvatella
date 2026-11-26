import { render, screen } from '@testing-library/react';
import axios from 'axios';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import App from '../App';
import { setupStore } from '../store/store';

// The home page loads videos: the API is faked so the tests do not need the backend
jest.mock('axios', () => ({
  __esModule: true,
  default: { get: jest.fn() },
}));
const mockedGet = axios.get as jest.Mock;

function renderAppAt(url: string) {
  render(
    <Provider store={setupStore()}>
      <MemoryRouter initialEntries={[url]}>
        <App />
      </MemoryRouter>
    </Provider>
  );
}

describe('App routes', () => {
  beforeEach(() => {
    mockedGet.mockResolvedValue({ data: [] });
  });

  it('shows the home page at /', async () => {
    renderAppAt('/');

    expect(screen.getByRole('heading', { name: 'Latest videos' })).toBeInTheDocument();
    expect(await screen.findByText('There are no videos yet.')).toBeInTheDocument();
  });

  it('shows the video page with the id of the url at /videos/7', () => {
    renderAppAt('/videos/7');

    expect(screen.getByRole('heading', { name: 'Video 7' })).toBeInTheDocument();
  });

  it('shows the not found page for an unknown url', () => {
    renderAppAt('/this-page-does-not-exist');

    expect(screen.getByRole('heading', { name: 'Page not found' })).toBeInTheDocument();
  });

  it('shows the navigation bar on every page', () => {
    renderAppAt('/videos/7');

    expect(screen.getByRole('navigation')).toBeInTheDocument();
  });
});
