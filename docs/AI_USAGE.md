# AI usage

Where AI (Claude) was used in ProTube, with which prompt, and where the work was done by a team member.
This is the record asked for in the course ("justify where you used AI and where you didn't") and in the
"Usage of AI" section of the README. AI commits are also marked in Git with `AI-assisted: yes`.

How tickets are split between AI and team member: `ticket-workflow` skill and
[`prompts/03-tests-and-boilerplate-for-a-ticket.md`](../prompts/03-tests-and-boilerplate-for-a-ticket.md).

## Setup and process

| Ticket | AI part (prompt) | Team member part | Why |
|--------|------------------|------------------|-----|
| PG-13 | `protube-coding-style` skill (`941c99f`, `prompts/01-…`) | Review and approval in PR #2 | AI configuration agreed before coding (NFR-06) |
| PG-14 | `docs/WORKFLOW.md` (`05f35dc`, `prompts/02-…`) | Review and approval in PR #2 | Written from the course rules and the Jira plan |
| PG-11 | Pull request template (`81e364d`, `prompts/02-…`) | Review and approval in PR #2 | Boilerplate |
| PG-16 | `docs/ARCHITECTURE.md` (`8e12d27`) | Product Owner decisions (login type, author field, Git flow…) and approval in PR #4 | AI wrote the document from the decisions taken by the Product Owner |
| PG-17 | CI also runs on `develop` (`8d26c7b`) | Review in PR #3 | Three-line config change |
| — | Sprint review skill (`14db639`) | Review in PR #2 | Built from the course template |
| PG-12 | Whole ticket: husky + commitlint setup, `commitlint.config.js`, docs (no prompt file: direct request in the session) | Try a bad and a good commit on their own computer; review in the PR | Configuration only, no logic to split |
| PG-24 | Sprint 1 review slides filled from the course template with the real Jira and GitHub data (no prompt file: direct request in the session; the `sprint-review` skill) | Release v0.1 merged to `main` and tagged; demo and backup video | The slides follow a fixed template; the release and the demo are done by the Release Manager |

## Features

| Ticket | AI part (prompt) | Team member part | Why this split |
|--------|------------------|------------------|----------------|
| PG-18 | Whole ticket: PostgreSQL config, `Video` entity, `VideoRepository`, tests (`561f4b1`) | Review in PR #5 | Done before the split between AI and team member was agreed |
| PG-20 | `VideoResponse` DTO, test data, specification tests (`05f7769`, `d57e064`, `prompts/03-…`) | Video loader, service, controller, startup runner (`c785dc2`) | The logic of the ticket stays with the team member; the AI writes only boilerplate and tests |
| PG-22 | `authorName` field, `VideoDetailResponse` DTO, specification tests (`f25f94a`, `prompts/03-…`) | `VideoNotFoundException`, `getVideoById`, `GET /api/videos/{id}`, author in the loader (`2929020`) | Same split as PG-20 |
| PG-19 | Redux store and hooks, empty pages, Jest config, specification tests (`c052fd6`, `prompts/03-…`); Prettier formatting (`baf9ea3`) | `NavBar`, `VideoPage`, routes in `App.tsx` (`417abe5`, `9a68bb3`, `4691d21`, `c587951`) | Routing and the menu are the structure of the app the team member must explain |
| PG-21 | `VideoCard`, `formatDuration`, `buildMediaUrl`, `setupStore`, specification tests (`148b8b5`, `prompts/03-…`) | `fetchVideos` thunk and `HomePage` (`52d6ba6`) | Redux async flow and the home screen are core knowledge |
| PG-23 | `VideoPlayer`, `VideoDetails` type, specification tests (`854323a`, `prompts/03-…`) | `VideoPage` (`05ce5d1`) | Loading data, 404 handling and the page states are core knowledge |
| PG-25 | Spring Security dependencies, `User` entity, `UserRepository`, specification tests of the security rules, CORS and BCrypt (`9c9f290`, `prompts/03-…`) | `SecurityConfig`: public and private URLs, 401 without login, CORS with Spring Security, BCrypt `PasswordEncoder` (`9eb389a`) | Which URLs are public and how passwords are protected are security decisions the team member must explain |
| PG-26 | `RegisterRequest` and `UserResponse` DTOs, specification tests of the register rules and of `POST /api/auth/register`, decisions written in `docs/ARCHITECTURE.md` (`ef5f2fd`, `prompts/03-…`) | `UserService.register` (validation, username from the email, BCrypt, save), `AuthController`, the two exceptions, CSRF disabled in `SecurityConfig` (`9403d6a`) | The register rules and the security choice are the core of the story; the decisions were taken by the Product Owner |
| PG-27 | `LoginRequest` DTO, specification tests of the login, the session and the logout, test setup for `UserDetailsService`, decision written in `docs/ARCHITECTURE.md` (`prompts/03-…`) | `AppUserDetailsService`, `AuthenticationManager` in `SecurityConfig`, login / logout / me in `AuthController`, `UserService.getUserByEmail` | How the login and the session work is the core of the story and must be explained by the team member |
