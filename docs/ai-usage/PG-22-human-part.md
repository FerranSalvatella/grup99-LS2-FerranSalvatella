# PG-22 — Human part of the development

**Ticket:** PG-22 · US-05a Backend: get one video (`GET /api/videos/{id}`)
**Prompt used for the AI part:** [`prompts/03-tests-and-boilerplate-for-a-ticket.md`](../../prompts/03-tests-and-boilerplate-for-a-ticket.md)

This document explains which part of PG-22 is done by a team member (the **human part**), what that person has to
do, and why that part was not left to the AI.

**Acceptance criteria (Jira)**
* With a good id, `GET /api/videos/{id}` returns title, description, video URL, thumbnail URL, author and tags.
* With an id that does not exist, it returns `404 Not Found`.

**Decision taken before starting (Product Owner):** the `User` entity arrives in PG-25, so for now the author is a
plain text field `authorName` in `Video`, filled with the channel name of the videoGrabber JSON (`"user"`). PG-25 will
replace it with the relation to `User`.

---

## 1. How the ticket was split

| Part                                                    | Who   | Why                                                              |
|---------------------------------------------------------|-------|------------------------------------------------------------------|
| `VideoNotFoundException` (answers 404)                  | Human | Decides how the API reports errors; reused by every later "not found" |
| `VideoService.getVideoById(id)`                         | Human | Core logic: find the video, handle "not found", build the URLs   |
| `VideosController`: `GET /api/videos/{id}`              | Human | Contract with the frontend video page                           |
| `VideoLoaderService`: save the author                   | Human | Data mapping decision (which JSON field is the author)          |
| `authorName` field in `Video` (field, getter, setter)   | AI    | Boilerplate, decision already taken by the Product Owner        |
| `VideoDetailResponse` DTO                               | AI    | Boilerplate: fields, constructor, getters                       |
| Tests (service, loader, HTTP endpoint)                  | AI    | Specification of the human part; reviewed by the human           |

---

## 2. What the human has to do

### 2.1 `VideoNotFoundException`

A new exception in `services/` that Spring turns into a **404** response. It receives the id of the video that was
not found (the tests create it with `new VideoNotFoundException(99L)`).

**Decision to defend:** why an exception with `@ResponseStatus(HttpStatus.NOT_FOUND)` and not returning `null` or an
empty object from the service. (It follows the same pattern as `InvalidPaginationException` from PG-20.)

### 2.2 `VideoService.getVideoById(Long id)`

Returns a `VideoDetailResponse`.

**Work to do**
- Ask the repository for the video with `findById(id)`. It returns an `Optional<Video>`.
- If it is empty, throw `VideoNotFoundException`.
- If it is there, build the response: video URL `/media/<videoFileName>`, thumbnail URL `/media/<thumbnailFileName>`,
  author from `authorName`, and the rest of the fields.

**Decisions to defend**
- How `Optional` is handled (see the coding style skill: `isEmpty()` + `get()`).
- Why the detail uses a different DTO from the list (`VideoDetailResponse` vs `VideoResponse`): the list only needs
  what the home grid shows.

### 2.3 `VideosController`: `GET /api/videos/{id}`

A new method that reads the id from the URL with `@PathVariable` and returns the details with status 200. When the
video does not exist, the exception from the service produces the 404 by itself.

### 2.4 `VideoLoaderService`: save the author

When creating each `Video` from the JSON, also set `authorName` with the `"user"` field of the JSON.

**Decision to defend:** why `"user"` (the channel) and not `meta.channel` (both have the same value in the
videoGrabber output; `"user"` is in the root, like the title).

---

## 3. How the human checks the work

1. `cd backend && ./mvnw clean verify -Pcoverage` → **24 tests**, 0 failures, `BUILD SUCCESS`.
2. With the app running on PostgreSQL and videos in the store:
   - `http://localhost:8080/api/videos/1` → JSON with title, description, author, tags, `videoUrl`, `thumbnailUrl`.
   - `http://localhost:8080/api/videos/999999` → status 404.
   - Open the `videoUrl` (`http://localhost:8080/media/<file>.mp4`): the video plays.

   The database already has videos from PG-20 **without author**. The loader only runs on an empty database, so to
   see authors empty the table first (for example `docker compose down -v` deletes the database volume).
3. Review the code against the `protube-coding-style` skill checklist.

---

## 4. What the human must be able to explain in the exam

1. How a request to `/api/videos/7` reaches `getVideoById(7)` (`@GetMapping("/{id}")` + `@PathVariable`).
2. How the exception becomes a 404 response.
3. What `Optional` is and why `findById` returns it.
4. Why the API returns DTOs and not the `Video` entity.
5. Why `authorName` is temporary and what will change in PG-25.

---

## 5. Traceability in Git

| Commit                                        | Author of the content | Trailers                                    |
|-----------------------------------------------|-----------------------|---------------------------------------------|
| `test(videos): add specification tests…`      | AI                    | `Refs: PG-22`, `AI-assisted: yes`, `Prompt: prompts/03-…` |
| `feat(videos): get one video by id`           | Team member           | `Refs: PG-22`                               |
