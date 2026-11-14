import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import App from '../App';

function renderAppAt(url: string) {
  render(
    <MemoryRouter initialEntries={[url]}>
      <App />
    </MemoryRouter>
  );
}

describe('App routes', () => {
  it('shows the home page at /', () => {
    renderAppAt('/');

    expect(screen.getByRole('heading', { name: 'Latest videos' })).toBeInTheDocument();
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
