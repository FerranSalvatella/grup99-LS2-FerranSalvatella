import { configureStore } from '@reduxjs/toolkit';
import videosReducer from './videosSlice';

// Global state of the app (Redux Toolkit, as recommended by the Redux docs)
export const store = configureStore({
  reducer: {
    videos: videosReducer,
  },
});

// Types used by the typed hooks in hooks.ts
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
