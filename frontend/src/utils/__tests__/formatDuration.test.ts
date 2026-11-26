import { formatDuration } from '../formatDuration';

describe('formatDuration', () => {
  it('adds a zero before seconds smaller than ten', () => {
    expect(formatDuration(65)).toBe('1:05');
  });

  it('rounds the seconds', () => {
    expect(formatDuration(30.6)).toBe('0:31');
  });

  it('shows minutes bigger than 9', () => {
    expect(formatDuration(725)).toBe('12:05');
  });
});
