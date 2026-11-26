// One video with all its information, as GET /api/videos/{id} returns it (VideoDetailResponse in the backend)
export interface VideoDetails {
  id: number;
  title: string;
  description: string;
  author: string;
  tags: string[];
  videoUrl: string;
  thumbnailUrl: string;
  duration: number;
}
