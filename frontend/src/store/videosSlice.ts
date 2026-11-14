import { createSlice } from '@reduxjs/toolkit';

// Number of videos per page, the same default as GET /api/videos in the backend
export const PAGE_SIZE = 12;

// One video of the list, as GET /api/videos returns it
export interface VideoSummary {
  id: number;
  title: string;
  thumbnailUrl: string;
  duration: number;
}

export type LoadingStatus = 'idle' | 'loading' | 'success' | 'error';

export interface VideosState {
  videos: VideoSummary[];
  status: LoadingStatus;
}

const initialState: VideosState = {
  videos: [],
  status: 'idle',
};

const videosSlice = createSlice({
  name: 'videos',
  initialState,
  reducers: {},
});

export default videosSlice.reducer;
