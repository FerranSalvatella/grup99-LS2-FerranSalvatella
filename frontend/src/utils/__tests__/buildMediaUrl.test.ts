import { buildMediaUrl } from '../buildMediaUrl';

// In the tests MEDIA_BASE_URL is "development" (see jest.setup.ts)
describe('buildMediaUrl', () => {
  it('joins the media base url and a path that starts with /media', () => {
    expect(buildMediaUrl('/media/1.webp')).toBe('development/1.webp');
  });

  it('joins the media base url and a path without /media', () => {
    expect(buildMediaUrl('/2.mp4')).toBe('development/2.mp4');
  });
});
