import { buildMediaUrl } from '../utils/buildMediaUrl';

interface VideoPlayerProps {
  title: string;
  videoUrl: string;
  thumbnailUrl: string;
}

// The HTML video player. The thumbnail is shown (poster) until the user presses play.
function VideoPlayer({ title, videoUrl, thumbnailUrl }: VideoPlayerProps) {
  return (
    <video
      controls
      className="w-100"
      aria-label={title}
      src={buildMediaUrl(videoUrl)}
      poster={buildMediaUrl(thumbnailUrl)}
    >
      Your browser cannot play this video.
    </video>
  );
}

export default VideoPlayer;
