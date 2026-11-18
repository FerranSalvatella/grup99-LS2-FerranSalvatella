# PG-23 — Human part of the development

**Ticket:** PG-23 · US-05b Frontend: video page with player and info
**Prompt used for the AI part:** [`prompts/03-tests-and-boilerplate-for-a-ticket.md`](../../prompts/03-tests-and-boilerplate-for-a-ticket.md)
**Depends on:** PG-21 (uses `buildMediaUrl` from that branch). Merge the PG-21 PR first.

**Acceptance criteria (Jira)**
* I click a video on the home page and the video page opens and plays it.
* Next to the player I see the title, description and author (comments come in Sprint 3).
* If the video does not exist, I see a "Video not found" page, not a broken player.

---

## 1. How the ticket was split

| Part                                                      | Who   | Why                                                                    |
|-----------------------------------------------------------|-------|------------------------------------------------------------------------|
| `VideoPage.tsx`: read the id, call the API, the 4 states, show the details | Human | Main logic of the screen: `useParams`, `useEffect`, `async/await`, 404 vs other errors |
| `VideoPlayer` (HTML `<video>` with controls and poster)   | AI    | Presentational component, no decisions                                 |
| `api/VideoDetails.ts` (type of the API answer)            | AI    | Copy of the backend `VideoDetailResponse` fields                       |
| Tests (`VideoPage`, `VideoPlayer`, `App`)                 | AI    | Specification of the human part; reviewed by the human                 |

The clicking part of the first criterion is already done: every `VideoCard` of the home (PG-21) links to
`/videos/<id>`.

---

## 2. What the human has to do: `pages/VideoPage.tsx`

* Read `id` from the URL with `useParams`.
* Keep two pieces of local state: the video (`VideoDetails | null`) and the page status
  (`'loading' | 'success' | 'notFound' | 'error'`).
* In `useEffect` (dependency `[id]`), call `GET <API_BASE_URL>/videos/<id>` with `axios` using `async/await` and
  `try/catch`:
  * success → save the video, status `'success'`
  * the API answers **404** → status `'notFound'`
  * any other error → status `'error'`
* Show exactly one of: `Loading video...`, `Video not found.`, `Could not load the video.`, or the page with the
  `VideoPlayer`, the title (`<h1>`), `By <author>`, the description and the list of tags.

**Decisions to defend**
* Why the video is **local state** here (only this page uses it) and the list of the home is in Redux.
* How a 404 is told apart from a network error (`axios.isAxiosError(error)` and `error.response.status`).
* Why `[id]` is the dependency of `useEffect` (going from one video to another reloads the data).
* Why the player is not shown at all when the video does not exist.

---

## 3. How the human checks the work

```bash
cd frontend
npm run test   # 45 tests (with PG-21 done), coverage threshold met
npm run lint
npm run build
```
With the backend and videos running: click a video on the home → the player plays it; `/videos/999999` →
`Video not found.`

---

## 4. What the human must be able to explain in the exam

1. The path from the click on a card to the playing video.
2. `useParams`, `useEffect` with `[id]`, and `async/await` with `try/catch`.
3. The difference between "not found" and "could not load", and how the code knows which one happened.
4. Why this page uses local state and the home page uses Redux.
