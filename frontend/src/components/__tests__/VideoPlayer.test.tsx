import { render, screen } from '@testing-library/react';
import VideoPlayer from '../VideoPlayer';

// In the tests MEDIA_BASE_URL is "development" (see jest.setup.ts)
describe('VideoPlayer', () => {
  it('plays the video file of the media folder', () => {
    render(<VideoPlayer title="Relaxing piano music" videoUrl="/media/1.mp4" thumbnailUrl="/media/1.webp" />);

    const player = screen.getByLabelText('Relaxing piano music');

    expect(player).toHaveAttribute('src', 'development/1.mp4');
  });

  it('shows the thumbnail before the video starts', () => {
    render(<VideoPlayer title="Relaxing piano music" videoUrl="/media/1.mp4" thumbnailUrl="/media/1.webp" />);

    const player = screen.getByLabelText('Relaxing piano music');

    expect(player).toHaveAttribute('poster', 'development/1.webp');
  });

  it('has the browser controls (play, pause, volume)', () => {
    render(<VideoPlayer title="Relaxing piano music" videoUrl="/media/1.mp4" thumbnailUrl="/media/1.webp" />);

    expect(screen.getByLabelText('Relaxing piano music')).toHaveAttribute('controls');
  });
});
