# Architecture

How ProTube is built: screens, data model, REST API, code organisation and the decisions behind them.
Requirements come from [REQUIREMENT.md](../REQUIREMENT.md). Any change to this document goes through a Pull Request.

## 1. Screens

| Screen                                       | Route               | Who                                 | Story         |
|----------------------------------------------|---------------------|-------------------------------------|---------------|
| Home: grid of videos, paginated              | `/`                 | everyone                            | US-04         |
| Video: player, info and comments             | `/videos/:id`       | everyone (comment: logged-in users) | US-05, 09, 10 |
| Search results                               | `/search?q=...`     | everyone                            | US-08         |
| Register / Login                             | `/register`, `/login` | visitors                          | US-01, US-02  |
| Profile (mine: editable, others: read only)  | `/users/:username`  | everyone                            | US-03         |
| Upload a video                               | `/upload`           | logged-in users                     | US-06         |
| Edit a video                                 | `/videos/:id/edit`  | only the owner                      | US-07         |
| Analytics                                    | `/analytics`        | logged-in users                     | US-11         |

Every screen has the same top bar: logo, search box, and "Log in" or "Upload" / "Profile" depending on the session.

## 2. Data model (PostgreSQL + JPA)

```
User ──< Video ──< Comment ──< Comment (replies, through parent)
  │                   │
  └────< CommentLike >┘
```

| Entity          | Main fields                                                                                       |
|-----------------|---------------------------------------------------------------------------------------------------|
| **User**        | id, username, email, passwordHash, description, avatarUrl, createdAt                              |
| **Video**       | id, title, description, category, tags (list of text), videoFileName, thumbnailFileName, width, height, duration, viewCount, createdAt, owner → User |
| **Comment**     | id, text, createdAt, author → User, video → Video, parent → Comment (empty if it is not a reply)  |
| **CommentLike** | id, user → User, comment → Comment (one like per user and comment)                                |

### Videos from the videoGrabber

On startup, if the database has no videos, the backend reads every `{id}.json` file in the store folder
(`ENV_PROTUBE_STORE_DIR`) and saves it as a `Video`: title, description, tags, category, view count, size and duration.
The `.mp4` and `.webp` files stay in the store folder and are served under `/media/**`.

## 3. REST API

| Method        | Path                                   | Purpose                                     |
|---------------|----------------------------------------|---------------------------------------------|
| GET           | `/api/videos?page=0&size=12`           | Paginated list                              |
| GET           | `/api/videos/{id}`                     | Detail (adds one view)                      |
| GET           | `/api/videos/search?q=...`             | Search in title, description and tags       |
| POST          | `/api/videos`                          | Upload (multipart: file + data)             |
| PUT           | `/api/videos/{id}`                     | Edit metadata (only the owner)              |
| GET / POST    | `/api/videos/{id}/comments`            | List / add comments                         |
| POST          | `/api/comments/{id}/replies`           | Reply to a comment                          |
| POST / DELETE | `/api/comments/{id}/like`              | Like / remove like                          |
| POST          | `/api/auth/register`, `/login`, `/logout` | Session                                  |
| GET           | `/api/auth/me`                         | Who is logged in                            |
| GET / PUT     | `/api/users/{username}`                | See / edit a profile                        |
| GET           | `/api/users/me/analytics`              | Stats of my videos                          |

Media files: `/media/{fileName}` (already configured in `MvcConfig`).

## 4. Code organisation

### Backend (`backend/`)

`controller` → `services` → `repository` → `entity`, plus a `dto` package for the classes that go in and out of the API.

* Controllers only handle HTTP and return DTOs, never entities.
* Services hold the business rules.
* Repositories are Spring Data JPA interfaces.
* Login: Spring Security, session with a cookie, passwords hashed with BCrypt.

### Frontend (`frontend/src/`)

| Folder        | Content                                                                  |
|---------------|--------------------------------------------------------------------------|
| `pages/`      | One component per screen (`HomePage`, `VideoPage`, ...)                  |
| `components/` | Reusable pieces: `NavBar`, `VideoCard`, `VideoGrid`, `VideoPlayer`, `CommentList`, `CommentForm` |
| `store/`      | Redux Toolkit store and slices (logged-in user, ...)                     |
| `api/`        | Calls to the backend                                                     |

Navigation with `react-router-dom`.

## 5. Decisions

| Topic                               | Decision                                                        | Why                                                    |
|-------------------------------------|-----------------------------------------------------------------|--------------------------------------------------------|
| Login type                          | Email + password, Spring Security session                       | It is what US-01/US-02 ask; simplest to explain        |
| HTTP client                         | `axios`                                                         | Already in the project and used by `useAllVideos.ts`   |
| Lombok                              | Not used                                                        | Explicit getters and setters are easier to read        |
| E2E tool                            | Playwright                                                      | The one taught in the course slides                    |
| Owner of the videoGrabber videos    | One `User` per channel, without password (cannot log in)        | Every video has an owner; one simple model             |
| Comments inside the videoGrabber JSON | Imported, with their author as a user without password        | Video pages are not empty in the demo                  |
| Thumbnail on upload                 | Optional; if missing, a default image is shown                  | Generating it with ffmpeg on the server adds complexity|
| Branches                            | Git flow: task branches and PRs to `develop`, releases to `main`| Course Git flow slides                                 |
| Database in development             | PostgreSQL from `compose.yaml` (`docker compose up db`)         | Same database as production (NFR-01)                   |
