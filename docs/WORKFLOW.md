# Workflow

How we move forward in the project, step by step.

- Jira board: https://ferransalvatellaochoa.atlassian.net/jira/software/projects/PG/boards/1
- Final delivery: **8 Dec**, branch `main`, send the repo link by email to both teachers.

## Roles

| Role            | Who     |
|-----------------|---------|
| Product Owner   | Ferran  |
| Scrum Master    | Ferran  |
| Release Manager | Ferran  |
| Developer       | Ferran  |

## Sprints

| Sprint   | Dates           | Goal                                          |
|----------|-----------------|-----------------------------------------------|
| Sprint 0 | 26 Oct – 8 Nov  | Setup: repo rules, AI config, backlog, tools  |
| Sprint 1 | 9 – 13 Nov      | Video list + player, PostgreSQL               |
| Sprint 2 | 16 – 20 Nov     | Register, login, profile                      |
| Sprint 3 | 23 – 27 Nov     | Upload, search, comments, E2E + coverage in CI |
| Sprint 4 | 30 Nov – 7 Dec  | Edit video, replies, likes, analytics, Docker, v1.0 |

## Every week

| When        | What                                                               |
|-------------|--------------------------------------------------------------------|
| Every day   | Daily (10 min): done yesterday / do today / blockers. Update Jira. |
| Wednesday   | Refinement (1 h): next sprint tickets ready and estimated.         |
| Friday      | Sprint review, retrospective, planning of next sprint.             |

## Steps for one ticket

1. **Jira:** take a ticket of the active sprint and move it to *In Progress*.
2. **Branch** from an updated `main`:
   ```bash
   git checkout main
   git pull
   git checkout -b feature/PG-20-video-list
   ```
   Prefixes: `feature/`, `fix/`, `docs/`, `chore/`, `test/`.
3. **Code + tests.** Follow the `protube-coding-style` skill.
4. **Check locally** (the same as CI):
   ```bash
   cd frontend && npm run lint && npm run test && npm run build
   cd backend && ./mvnw clean verify -Pcoverage
   ```
5. **Commit** with conventional commits and the ticket:
   ```
   feat(videos): add GET /api/videos endpoint

   Refs: PG-20
   ```
   Types: `feat`, `fix`, `docs`, `test`, `refactor`, `style`, `chore`.
   If AI helped, add:
   ```
   AI-assisted: yes
   Prompt: prompts/NN-name.md
   ```
6. **Push:** `git push -u origin feature/PG-20-video-list`
7. **Pull Request** to `main`. Title: `PG-20 Video list endpoint`. Fill the template.
8. **Wait** for: CI green + all Copilot comments resolved + 1 approval.
9. **Merge** and delete the branch.
10. **Jira:** move the ticket to *Done* (only now, see Definition of Done).

## AI rules

- Use only the approved config (`CLAUDE.md`, `.claude/skills/`).
- Every prompt that makes code or docs goes to `prompts/` (use `prompts/TEMPLATE.md`).
  Reuse a prompt if one already exists for that kind of task.
- Mark the commit (step 5). I must be able to explain every line.

## Definition of Ready (before a ticket enters a sprint)

- [ ] Clear title and user story / task.
- [ ] Acceptance criteria written.
- [ ] Estimated, 5 points or less (bigger = split it).

## Definition of Done

- [ ] Acceptance criteria are met.
- [ ] Tests written, coverage stays > 50%.
- [ ] Lint and CI green.
- [ ] Copilot comments resolved and PR approved.
- [ ] Merged into `main`.
- [ ] AI use marked and prompt saved (if AI was used).

## End of sprint (Release Manager)

1. All Done tickets merged, CI green on `main`.
2. Tag the version: `git tag v0.N.0 && git push origin v0.N.0` (Sprint 4: `v1.0.0`).
3. Review slides with the course template. Demo from `main` + backup video.
4. Retrospective notes in `docs/sprints/sprint-N.md` (good, bad, actions).
5. Jira: close the sprint, move unfinished tickets, start the next one.

## CI plan (GitHub Actions)

| Sprint | Ticket | Change                                         |
|--------|--------|------------------------------------------------|
| 0      | PG-17  | Current pipeline green and required on `main`  |
| 0      | PG-12  | Commit messages checked (husky + commitlint)   |
| 3      | PG-40  | Fail if coverage < 50% (backend + frontend)    |
| 3      | PG-39  | First E2E test running in CI                   |
| 4      | PG-48  | E2E tests for the main user flows              |
| 4      | PG-47  | Full app with `docker compose up`              |

## Decisions

| Topic              | Decision      |
|--------------------|---------------|
| `fetch` or `axios` | Pending (PG-16) |
| Lombok             | Pending (PG-16) |
| Login type         | Pending (PG-16) |
| E2E tool           | Pending (PG-16) |
| PR approval with 1 person team | Pending: GitHub does not let you approve your own PR. Ask the teachers. |
