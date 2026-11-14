import { createSlice } from '@reduxjs/toolkit';

// Shape of the videos state. PG-21 adds the call to GET /api/videos that fills it.
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
