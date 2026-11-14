import { Link } from 'react-router-dom';
import type { VideoSummary } from '../store/videosSlice';
import { buildMediaUrl } from '../utils/buildMediaUrl';
import { formatDuration } from '../utils/formatDuration';

interface VideoCardProps {
  video: VideoSummary;
}

// One video of the home grid: thumbnail, title and duration, linking to the video page
function VideoCard({ video }: VideoCardProps) {
  return (
    <Link to={'/videos/' + video.id} className="card h-100 text-decoration-none">
      <img src={buildMediaUrl(video.thumbnailUrl)} alt={video.title} className="card-img-top" />
      <div className="card-body">
        <h2 className="card-title h6">{video.title}</h2>
        <span className="text-muted">{formatDuration(video.duration)}</span>
      </div>
    </Link>
  );
}

export default VideoCard;
