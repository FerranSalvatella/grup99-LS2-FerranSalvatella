# PG-21 — Human part of the development

**Ticket:** PG-21 · US-04b Frontend: home page with the videos
**Prompt used for the AI part:** [`prompts/03-tests-and-boilerplate-for-a-ticket.md`](../../prompts/03-tests-and-boilerplate-for-a-ticket.md)

**Acceptance criteria (Jira)**
* The home page shows each video with its thumbnail and title.
* If there are many videos, I can go to the next page without errors.
* If the API fails, I see a clear error message.
* If there are no videos, I see a message that says so.

---

## 1. How the ticket was split

| Part                                                         | Who   | Why                                                                     |
|--------------------------------------------------------------|-------|-------------------------------------------------------------------------|
| `fetchVideos` thunk and its 3 cases in `videosSlice.ts`      | Human | Core of Redux: how the app calls the API and stores loading/success/error |
| `HomePage.tsx`: load the videos, the 4 states, pagination    | Human | Main screen of the app; uses `useEffect`, the store and the page state |
| `VideoCard` (thumbnail, title, duration, link)               | AI    | Presentational component, no decisions                                  |
| `formatDuration`, `buildMediaUrl` helpers                    | AI    | Small utilities with their own tests                                    |
| `setupStore()` in `store.ts`, `PAGE_SIZE` constant           | AI    | Lets every test use a fresh store (Redux testing guide)                 |
| Install `@testing-library/user-event` (dev)                  | AI    | Recommended in the course React testing slides                          |
| Tests (slice, `HomePage`, `VideoCard`, helpers, `App`)       | AI    | Specification of the human part; reviewed by the human                  |

---

## 2. What the human has to do

### 2.1 `fetchVideos` in `store/videosSlice.ts`
A `createAsyncThunk` named `'videos/fetchVideos'` that receives the page number and calls
`GET <API_BASE_URL>/videos?page=<page>&size=12` with `axios`, returning the list of videos.
Then, in `extraReducers`, three cases:
* `pending` → status `'loading'`
* `fulfilled` → status `'success'` and save the videos
* `rejected` → status `'error'`

**Decisions to defend:** why the API call lives in a thunk and not inside the component; why the status has four
values (`idle`, `loading`, `success`, `error`).

### 2.2 `pages/HomePage.tsx`
* Keeps the current page in local state (`useState`, starts at 0).
* With `useEffect`, dispatches `fetchVideos(page)` when the page changes.
* Reads `videos` and `status` from the store with `useAppSelector`.
* Shows exactly one of: `Loading videos...`, `Could not load the videos.`, `There are no videos yet.`,
  or the grid of `VideoCard`s (each with a `key`).
* When the load succeeded, shows the buttons `Previous page` (disabled on page 0) and `Next page`
  (disabled when the page has fewer than 12 videos).

**Decisions to defend:** why the page number is local state and the videos are in Redux; what the dependency array
of `useEffect` does; why `key={video.id}` and not the index.

---

## 3. How the human checks the work

```bash
cd frontend
npm run test   # 36 tests, coverage threshold met
npm run lint
npm run build
```
With the backend running (`docker compose up -d db`, `./mvnw spring-boot:run`) and `npm run dev`:
the home shows the videos with thumbnails; `Next page` / `Previous page` work; with the backend stopped, the error
message appears.

---

## 4. What the human must be able to explain in the exam

1. The path of the data: click → `dispatch(fetchVideos(1))` → axios → backend → `fulfilled` → store → re-render.
2. What `createAsyncThunk` and `extraReducers` do.
3. Why `useEffect` has `[dispatch, page]` as dependencies.
4. How the loading, error and empty states are chosen before the `return`.
5. Why the tests fake `axios` instead of calling the real backend.
