import { useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import VideoCard from '../components/VideoCard';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { fetchVideos, PAGE_SIZE } from '../store/videosSlice';

// PG-21: show the video list, its status, and pagination controls.
function HomePage() {
  const dispatch = useAppDispatch();
  const videos = useAppSelector((state) => state.videos.videos);
  const status = useAppSelector((state) => state.videos.status);
  const [page, setPage] = useState(0);

  useEffect(() => {
    void dispatch(fetchVideos(page));
  }, [dispatch, page]);

  let content: ReactNode;
  if (status === 'idle' || status === 'loading') {
    content = <p>Loading videos...</p>;
  } else if (status === 'error') {
    content = <p>Could not load the videos.</p>;
  } else if (videos.length === 0) {
    content = <p>There are no videos yet.</p>;
  } else {
    content = (
      <div className="row g-4">
        {videos.map((video) => (
          <div className="col-6 col-md-4 col-lg-3" key={video.id}>
            <VideoCard video={video} />
          </div>
        ))}
      </div>
    );
  }

  return (
    <main className="container py-4">
      <h1>Latest videos</h1>
      {content}
      {status === 'success' && (
        <div className="d-flex gap-2 mt-4">
          <button
            type="button"
            className="btn btn-outline-primary"
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
          >
            Previous page
          </button>
          <button
            type="button"
            className="btn btn-outline-primary"
            disabled={videos.length < PAGE_SIZE}
            onClick={() => setPage(page + 1)}
          >
            Next page
          </button>
        </div>
      )}
    </main>
  );
}

export default HomePage;
