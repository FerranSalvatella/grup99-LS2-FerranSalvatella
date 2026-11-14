import { store } from '../store';

describe('store', () => {
  it('starts with an empty list of videos', () => {
    const state = store.getState();

    expect(state.videos.videos).toEqual([]);
    expect(state.videos.status).toBe('idle');
  });
});
