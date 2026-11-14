import { useParams } from 'react-router-dom';

function VideoPage() {
  const { id } = useParams();

  return <h1>Video {id}</h1>;
}

export default VideoPage;
