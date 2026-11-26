// Turns a duration in seconds into the text "minutes:seconds", for example 65.4 -> "1:05"
export function formatDuration(totalSeconds: number): string {
  const roundedSeconds = Math.round(totalSeconds);
  const minutes = Math.floor(roundedSeconds / 60);
  const seconds = roundedSeconds % 60;

  let secondsText = String(seconds);
  if (seconds < 10) {
    secondsText = '0' + secondsText;
  }

  return minutes + ':' + secondsText;
}
