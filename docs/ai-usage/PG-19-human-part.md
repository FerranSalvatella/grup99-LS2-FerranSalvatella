# PG-19 — Human part of the development

**Ticket:** PG-19 · Frontend base: Redux store and pages
**Prompt used for the AI part:** [`prompts/03-tests-and-boilerplate-for-a-ticket.md`](../../prompts/03-tests-and-boilerplate-for-a-ticket.md)

**What the ticket asks (Jira)**
* Add Redux Toolkit: `configureStore` and the typed hooks `useAppDispatch` / `useAppSelector`.
* Add React Router with 2 pages: Home and Video.
* Add a top menu.
* Done when the store, the menu and the 2 pages work and have tests.

---

## 1. How the ticket was split

| Part                                                         | Who   | Why                                                              |
|--------------------------------------------------------------|-------|------------------------------------------------------------------|
| Routes in `App.tsx` (which URL shows which page)             | Human | Navigation structure of the whole app; asked in the exam         |
| `NavBar` (top menu)                                          | Human | First own component; decides which links the app has             |
| `VideoPage` (reads the id of the URL)                        | Human | Shows how a URL parameter reaches a component (`useParams`)      |
| Install `@reduxjs/toolkit`, `react-redux`, `react-router-dom` | AI    | `npm install` commands                                           |
| `store/store.ts`, `store/hooks.ts`, `store/videosSlice.ts`   | AI    | Copied from the Redux Toolkit TypeScript guide; no decisions     |
| `HomePage`, `NotFoundPage` (empty pages)                     | AI    | Skeletons filled in PG-21                                        |
| `main.tsx` with `Provider` and `BrowserRouter`               | AI    | Standard setup from the Redux and React Router docs              |
| Jest setup: `TextEncoder` polyfill and CSS mock              | AI    | Test configuration needed by React Router 7 and CSS imports      |
| Tests (`App`, `NavBar`, store)                               | AI    | Specification of the human part; reviewed by the human           |

The placeholder test `components/__tests__/app.test.tsx` (`expect(true).toBe(true)`) was removed: the real
`src/__tests__/App.test.tsx` replaces it.

---

## 2. What the human has to do

### 2.1 `components/NavBar.tsx`
A `<nav>` with the name of the app (`ProTube`) and a `Link` called `Home` that goes to `/`.

**Decisions to defend:** why `Link` and not `<a href>` (no page reload, the SPA keeps its state); which links the menu
will have when login arrives (Sprint 2).

### 2.2 `pages/VideoPage.tsx`
Reads the `id` from the URL with `useParams` and shows the heading `Video <id>`. PG-23 replaces the heading with the
player and the details from `GET /api/videos/{id}`.

### 2.3 Routes in `App.tsx`
Replace the old template content with the menu and the routes:

| URL            | Page           |
|----------------|----------------|
| `/`            | `HomePage`     |
| `/videos/:id`  | `VideoPage`    |
| anything else  | `NotFoundPage` |

The menu is shown on every page. `App` must **not** contain a router: `main.tsx` already wraps it in
`BrowserRouter` (so the tests can use `MemoryRouter`).

**Decisions to defend:** why the `*` route does not hide the other routes (React Router picks the most specific
match, not the first one); why `BrowserRouter` lives in `main.tsx`.

---

## 3. How the human checks the work

```bash
cd frontend
npm run test     # 17 tests, coverage threshold met
npm run lint
npm run build
npm run dev      # http://localhost:5173 , /videos/7 , /anything
```

---

## 4. What the human must be able to explain in the exam

1. How `/videos/7` reaches `VideoPage` and how the component gets the `7`.
2. Difference between `Link` and `<a href>` in a single-page app.
3. What `Provider` does and why the store is created once.
4. Why the typed hooks (`useAppSelector`, `useAppDispatch`) are used instead of the plain ones.
5. Why the tests use `MemoryRouter` instead of `BrowserRouter`.
