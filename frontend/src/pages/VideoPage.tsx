import axios from 'axios';
import { useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import { useParams } from 'react-router-dom';
import type { VideoDetails } from '../api/VideoDetails';
import VideoPlayer from '../components/VideoPlayer';
import { getEnv } from '../utils/Env';

type VideoPageStatus = 'loading' | 'success' | 'notFound' | 'error';

// PG-23 (human part): fetch one video and show one of four possible states.
function VideoPage() {
  const { id } = useParams();
  const [video, setVideo] = useState<VideoDetails | null>(null);
  const [status, setStatus] = useState<VideoPageStatus>('loading');

  useEffect(() => {
    // Ignore responses belonging to a previous id (or an unmounted page).
    let cancelled = false;

    async function loadVideo() {
      setVideo(null);
      setStatus('loading');

      if (!id) {
        setStatus('notFound');
        return;
      }

      try {
        const response = await axios.get<VideoDetails>(getEnv().API_BASE_URL + '/videos/' + id);
        if (!cancelled) {
          setVideo(response.data);
          setStatus('success');
        }
      } catch (error) {
        if (!cancelled) {
          if (axios.isAxiosError(error) && error.response?.status === 404) {
            setStatus('notFound');
          } else {
            setStatus('error');
          }
        }
      }
    }

    void loadVideo();
    return () => {
      cancelled = true;
    };
  }, [id]);

  let content: ReactNode;
  if (status === 'loading') {
    content = <p>Loading video...</p>;
  } else if (status === 'notFound') {
    content = <p>Video not found.</p>;
  } else if (status === 'error') {
    content = <p>Could not load the video.</p>;
  } else if (video !== null) {
    content = (
      <div className="row g-4">
        <div className="col-12 col-lg-8">
          <VideoPlayer title={video.title} videoUrl={video.videoUrl} thumbnailUrl={video.thumbnailUrl} />
        </div>
        <section className="col-12 col-lg-4">
          <h1>{video.title}</h1>
          <p>By {video.author}</p>
          <p>{video.description}</p>
          <ul>
            {video.tags.map((tag) => (
              <li key={tag}>{tag}</li>
            ))}
          </ul>
        </section>
      </div>
    );
  } else {
    content = <p>Loading video...</p>;
  }

  return <main className="container py-4">{content}</main>;
}

export default VideoPage;
