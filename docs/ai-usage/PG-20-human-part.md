# PG-20 — Human part of the development

**Ticket:** PG-20 · US-04a Backend: load the videoGrabber videos and list them (`GET /api/videos`)
**Prompt used for the AI part:** [`prompts/03-tests-and-boilerplate-for-a-ticket.md`](../../prompts/03-tests-and-boilerplate-for-a-ticket.md)

This document explains which part of PG-20 is done by a team member (the **human part**), what that person has to
do, and why that part was not left to the AI. It is the record that the README asks for: *"be able to explain why
you didn't use AI in some parts of the project"*.

---

## 1. How the ticket was split

| Part                                                      | Who   | Why                                                                    |
|-----------------------------------------------------------|-------|------------------------------------------------------------------------|
| `VideoLoaderService`: read the `.json` files and save them | Human | Core logic of the ticket; it contains the data mapping and the "no duplicates" rule |
| `AppStartupRunner`: decide when the videos are loaded     | Human | Business rule (only in `dev`, only once)                               |
| `VideoService.getVideos(page, size)`                      | Human | Pagination and conversion to DTO, reused by every later video story    |
| `VideosController`: `GET /api/videos?page=&size=`         | Human | It is the contract between backend and frontend                        |
| `VideoResponse` DTO                                       | AI    | Boilerplate: fields, constructor and getters, no decisions              |
| Sample JSON files for the tests                           | AI    | Test data that copies the videoGrabber format                          |
| Unit tests (loader, service, controller, startup runner)  | AI    | They are the specification of the human part; reviewed by the human     |

**Rule used to split it:** anything that contains a **decision** (what data to keep, when to run, what the API
returns) or that the exam can ask about ("how did you build it, how would you extend it") is human. Anything that is
repetitive and has no decision in it can be AI, always marked in the commit with its prompt.

---

## 2. What the human has to do

### 2.1 `VideoLoaderService` — load the videos from the store

The person writes a Spring service with one public method, `loadVideosFromFolder(Path storeFolder)`, that returns how
many videos were saved.

**Work to do**
- Stop and return 0 if the database already has videos.
- List the files of the store folder and keep only the `.json` ones.
- Read each JSON with Jackson and fill a `Video`: title, description, first category, tags, width, height, duration,
  view count, upload date, and the `.mp4` / `.webp` file names built from the JSON `id`.
- Save each video and count them.

**Decisions the human makes and must be able to defend**
- Which JSON fields go into the database and which are ignored for now (likes, channel, comments).
- Why only the **first** category is kept.
- Why the JSON `id` is used only for file names and not as the database id.
- How to avoid duplicates on every restart (`count()` before loading).
- What happens with a file that is not a `.json`.

### 2.2 `AppStartupRunner` — when the videos are loaded

The person changes the class that Spring runs once on startup so it receives the `VideoLoaderService` through the
constructor and calls it only when `pro_tube.load_initial_data` is `true`, logging how many videos were loaded.

**Decisions to defend**
- Why loading happens in `dev` and not in `prod`.
- Why the `Boolean` value is compared in a null-safe way.

### 2.3 `VideoService` — one page of videos

The person replaces the fake list (`"video1", "video2"`) with a method `getVideos(int page, int size)` that asks the
repository for one page **sorted by id** and turns each `Video` into a `VideoResponse`, with the thumbnail URL
`/media/<file>.webp`.

**Decisions to defend**
- Why the API returns a DTO and not the JPA entity.
- Why the results are sorted (pages must be stable between requests).
- Why the thumbnail URL points to `/media/` (the backend already serves the store folder there).

### 2.4 `VideosController` — the endpoint

The person changes the controller to use constructor injection and to accept `page` and `size` as URL parameters
with default values `0` and `12`.

**Decisions to defend**
- Why 12 videos per page by default (one home page grid).
- Why pages start at 0.

---

## 3. How the human checks the work

1. Run all tests the same way as CI: `cd backend && ./mvnw clean verify -Pcoverage` → 16 tests, 0 failures.
2. Run the app on PostgreSQL (`docker compose up -d db`, then `./mvnw spring-boot:run`) with videos in the store:
   - The log says how many videos were loaded.
   - `http://localhost:8080/api/videos` returns at most 12 videos.
   - `http://localhost:8080/api/videos?page=1&size=2` returns the second page.
   - After a restart the log says 0 videos loaded (no duplicates).
3. Review the code against the `protube-coding-style` skill checklist.

---

## 4. What the human must be able to explain in the exam

1. Why `count()` is checked before loading, and what would happen without it.
2. Why the API returns `VideoResponse` and not `Video`.
3. What `PageRequest.of(0, 12, Sort.by("id"))` does and why the list is sorted.
4. Why the JSON `id` is not the database id.
5. Why constructor injection is used instead of `@Autowired` on fields.
6. How the work was split between AI and human, and why.

---

## 5. Traceability in Git

| Commit                                   | Author of the content | Trailers                                    |
|------------------------------------------|-----------------------|---------------------------------------------|
| `test(videos): add specification tests…` | AI                    | `Refs: PG-20`, `AI-assisted: yes`, `Prompt: prompts/03-…` |
| `feat(videos): load videoGrabber videos…`| Team member           | `Refs: PG-20` only                          |

The Pull Request description repeats this split so the reviewer can see it.
