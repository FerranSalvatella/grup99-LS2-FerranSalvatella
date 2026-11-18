import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import axios from 'axios';
import { getEnv } from '../utils/Env';

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

// PG-21: load a page of video summaries from the backend.
export const fetchVideos = createAsyncThunk<VideoSummary[], number>('videos/fetchVideos', async (page) => {
  const response = await axios.get<VideoSummary[]>(
    getEnv().API_BASE_URL + '/videos?page=' + page + '&size=' + PAGE_SIZE
  );
  return response.data;
});

const videosSlice = createSlice({
  name: 'videos',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder.addCase(fetchVideos.pending, (state) => {
      state.status = 'loading';
    });
    builder.addCase(fetchVideos.fulfilled, (state, action) => {
      state.status = 'success';
      state.videos = action.payload;
    });
    builder.addCase(fetchVideos.rejected, (state) => {
      state.status = 'error';
    });
  },
});

export default videosSlice.reducer;
