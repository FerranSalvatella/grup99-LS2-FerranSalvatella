import { getEnv } from './Env';

const MEDIA_PREFIX = '/media';

// The backend returns media paths like "/media/1.webp".
// MEDIA_BASE_URL already ends with "/media", so the prefix is removed before joining both parts.
export function buildMediaUrl(mediaPath: string): string {
  let pathInsideMedia = mediaPath;
  if (mediaPath.startsWith(MEDIA_PREFIX)) {
    pathInsideMedia = mediaPath.substring(MEDIA_PREFIX.length);
  }

  return getEnv().MEDIA_BASE_URL + pathInsideMedia;
}
