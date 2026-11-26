import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import VideoCard from '../VideoCard';

const video = { id: 3, title: 'Relaxing piano music', thumbnailUrl: '/media/3.webp', duration: 65 };

describe('VideoCard', () => {
  it('shows the thumbnail with the title as alternative text', () => {
    render(<VideoCard video={video} />, { wrapper: MemoryRouter });

    const thumbnail = screen.getByRole('img', { name: 'Relaxing piano music' });

    expect(thumbnail).toHaveAttribute('src', 'development/3.webp');
  });

  it('shows the title and the duration', () => {
    render(<VideoCard video={video} />, { wrapper: MemoryRouter });

    expect(screen.getByText('Relaxing piano music')).toBeInTheDocument();
    expect(screen.getByText('1:05')).toBeInTheDocument();
  });

  it('links to the video page', () => {
    render(<VideoCard video={video} />, { wrapper: MemoryRouter });

    expect(screen.getByRole('link')).toHaveAttribute('href', '/videos/3');
  });
});
