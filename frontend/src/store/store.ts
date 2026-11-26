import { configureStore } from '@reduxjs/toolkit';
import videosReducer from './videosSlice';

// Creates a new store. The tests call it to get a fresh store in every test.
export function setupStore() {
  return configureStore({
    reducer: {
      videos: videosReducer,
    },
  });
}

// The store used by the app (Redux Toolkit, as recommended by the Redux docs)
export const store = setupStore();

// Types used by the typed hooks in hooks.ts
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
