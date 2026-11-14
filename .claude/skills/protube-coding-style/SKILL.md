---
name: protube-coding-style
description: Didactic coding style for the ProTube project (Software Lab II). Use it ALWAYS before writing, editing, refactoring or reviewing any code in this repository (Spring Boot backend, React + Redux frontend, tests, Docker/CI files). It sets the expected programming level - clear, academic, beginner-readable code with good naming, no clever tricks, no unusual libraries and no premature optimization.
---

# ProTube coding style: clear, academic, didactic

This is a university project. The goal is **not** the fastest or shortest code: the goal is code that a
beginner can open, read from top to bottom and understand **without needing comments**. Every team member must be able
to explain every line in the exam, so the code has to be explainable.

When two solutions work, choose the one that is **easier to read**, even if it is longer or a bit slower.

## 1. Golden rules

1. **Readability over cleverness.** No one-liners that pack several ideas together. One idea per line.
2. **Names explain the code, not comments.** If you need a comment to explain *what* the code does, rename things or
   split the code instead. Comments are only for *why* (a decision, a workaround, a business rule).
3. **Basic language features first.** `if`/`else`, `for` loops, small methods, plain classes and plain functions.
   Use an advanced feature only when it is the standard way of the framework (see sections 3 and 4).
4. **No premature optimization.** No caching, memoization, bit tricks, manual pooling or "faster" alternatives unless
   there is a measured problem and the team agreed on it in a task.
5. **No unusual libraries.** Use only the stack from the KickOff slides: Java 21, Spring Boot 3, JPA, PostgreSQL,
   React 19, Redux (Redux Toolkit), TypeScript, Vite, Jest, Testing Library. Adding any new dependency needs a task
   and team approval in the PR.
6. **Small units.** A method/function does one thing. If it does not fit on the screen, split it.
7. **Follow the official docs.** When in doubt about how to do something, follow the official guide of the tool
   (links at the end) and use its simplest documented approach.
8. **English only.** Code, names, comments, test names, UI texts, commit messages.

## 2. Naming

Names must be complete words that say what the thing is. No abbreviations except universally known ones (`id`, `url`,
`dto`, `api`).

| Bad                      | Good                         | Why                                   |
|--------------------------|------------------------------|---------------------------------------|
| `v`, `tmp`, `data2`      | `video`, `uploadedVideo`     | Says what it contains                 |
| `lst`, `arr`             | `videos`, `comments`         | Plural for collections                |
| `flag`, `check`          | `isLoggedIn`, `hasComments`  | Booleans start with is/has/can/should |
| `doIt()`, `process()`    | `uploadVideo()`, `addLike()` | Methods start with a verb             |
| `handle()`               | `handleSubmitClick()`        | Event handlers say which event        |

### Java (backend)

* Classes: `PascalCase`, singular nouns: `Video`, `Comment`, `UserProfile`.
* Layers end with their role: `VideoController`, `VideoService`, `VideoRepository`.
* DTOs say their direction: `CreateCommentRequest`, `VideoResponse`.
* Methods and variables: `camelCase`. Constants: `UPPER_SNAKE_CASE`.
* Keep the existing package `com.tecnocampus.LS2.protube_back` and its layer sub-packages (`controller`, `services`,
  `configuration`, ...). Do not rename existing classes unless a task asks for it.

### TypeScript / React (frontend)

* Components: `PascalCase`, one component per file, file named like the component: `VideoCard.tsx`.
* Hooks: start with `use`: `useAllVideos.ts`.
* Props types: `<ComponentName>Props`, e.g. `VideoCardProps`.
* Redux slices: `<feature>Slice.ts`, e.g. `videosSlice.ts`.
* Variables and functions: `camelCase`. Constants: `UPPER_SNAKE_CASE`.

## 3. Java / Spring Boot rules

### Prefer explicit control flow

```java
// Avoid: everything in one line, hard to read and to debug
return videos.stream().filter(v -> v.getTitle().toLowerCase().contains(q.toLowerCase())).sorted(Comparator.comparing(Video::getCreatedAt).reversed()).limit(10).collect(Collectors.toList());

// Prefer: one step per line, clear names
List<Video> matchingVideos = new ArrayList<>();
String searchText = query.toLowerCase();

for (Video video : allVideos) {
    String title = video.getTitle().toLowerCase();
    if (title.contains(searchText)) {
        matchingVideos.add(video);
    }
}
return matchingVideos;
```

* A `for` loop is the default. A **short** stream with one simple step (`.map(...)` or `.filter(...)` then `.toList()`)
  is fine. No long stream chains, no `Collectors.groupingBy`, `reduce`, `flatMap`, or custom collectors.
* Prefer writing the database query in the repository (Spring Data method names such as
  `findByTitleContainingIgnoreCase`) over filtering big lists in Java. That is the simple, documented way.
* Ternary operator only for a very simple value choice. **Never nested.** Otherwise use `if`/`else`.
* Use early returns / guard clauses instead of deep nesting.
* No `var` when the type is not obvious from the same line.

### Spring conventions

* Layering: `controller` (HTTP only) -> `services` (business logic) -> `repository` (database). A controller never
  talks to a repository directly.
* **Constructor injection**, as recommended by the Spring docs. No `@Autowired` on fields in new code.

  ```java
  @Service
  public class VideoService {

      private final VideoRepository videoRepository;

      public VideoService(VideoRepository videoRepository) {
          this.videoRepository = videoRepository;
      }
  }
  ```

* Controllers return DTOs, never JPA entities.
* Handle "not found" and invalid input explicitly and return the right HTTP status (`404`, `400`, ...).
* Avoid Lombok in new code unless the team decides otherwise: plain getters, setters and constructors are longer but
  everyone can read them. Do not use Lombok features beyond `@Getter`/`@Setter` if the team allows it.
* No reflection, no custom annotations, no AOP, no generic "base" classes for everything. Plain classes.

### Optional

```java
// Prefer: easy to read for a beginner
Optional<Video> foundVideo = videoRepository.findById(videoId);
if (foundVideo.isEmpty()) {
    throw new VideoNotFoundException(videoId);
}
Video video = foundVideo.get();
```

## 4. TypeScript / React / Redux rules

### Components

* Function components only. Declare them with `function`, with a typed props interface:

  ```tsx
  interface VideoCardProps {
    title: string;
    thumbnailUrl: string;
  }

  function VideoCard({ title, thumbnailUrl }: VideoCardProps) {
    return (
      <div className="card">
        <img src={thumbnailUrl} alt={title} />
        <h3>{title}</h3>
      </div>
    );
  }

  export default VideoCard;
  ```

* Keep JSX simple. If a condition is more than a simple `isLoading ? ... : ...` or `hasError && ...`, compute it in a
  well-named variable before the `return`, or use an early `return`:

  ```tsx
  if (status === 'loading') {
    return <p>Loading videos...</p>;
  }

  if (status === 'error') {
    return <p>Could not load the videos.</p>;
  }

  return <VideoGrid videos={videos} />;
  ```

* **Never nested ternaries** in JSX.
* No `any`. Write a type or interface for every API response.
* Use `async`/`await` with `try`/`catch`, not long `.then().then().catch()` chains.

### No premature optimization in React

* Do **not** use `useMemo`, `useCallback` or `React.memo` by default. The React docs explain that most code does not
  need them. Add them only if a task shows a measured performance problem.
* No virtualization libraries, no custom render tricks.

### State and Redux

* Local UI state (an input value, an open menu) -> `useState` in the component.
* Shared app state (logged user, videos list, ...) -> Redux, using **Redux Toolkit** as the Redux docs recommend:
  `configureStore`, `createSlice`, and `createAsyncThunk` for API calls.
* Use the typed hooks `useAppDispatch` / `useAppSelector` from the Redux TypeScript guide.
* Do not use RTK Query, sagas, observables or other advanced Redux add-ons unless the team agrees in a task.

### Files and folders

* Components in `src/components/`, tests next to them in `__tests__/` or as `*.test.tsx`.
* API access goes through one place (a service file or a thunk) using `getEnv().API_BASE_URL` from `src/utils/Env.ts`.
  Never hardcode URLs.
* Use one HTTP client consistently in the same feature (do not mix `fetch` and `axios` in one feature).

## 5. Tests

Tests are also documentation, so the same rules apply.

* Test names describe the behaviour: `shouldReturnNotFoundWhenVideoDoesNotExist` (Java),
  `it('shows an error message when the API fails')` (Jest).
* Structure every test in three visible blocks: **Arrange**, **Act**, **Assert** (a blank line between them).
* One behaviour per test. No loops or complex logic inside tests.
* Frontend: query elements the way a user finds them (`getByRole`, `getByText`, `getByLabelText`) as the Testing
  Library docs recommend.
* Coverage must stay above 50% on both apps (MVP requirement), but write meaningful tests, not tests only for numbers.

## 6. Before you finish a change

Check this list before proposing or committing code:

- [ ] Could a classmate who has never seen this file understand it without asking?
- [ ] Are all names complete, in English, and do they say what things are?
- [ ] Is there any one-liner, nested ternary, long stream/chain, or "smart trick" that can be written more simply?
- [ ] Did I add any library, optimization or pattern that nobody asked for? If yes, remove it.
- [ ] Are there tests for the new behaviour, and do lint and tests pass (`npm run lint`, `npm run test`,
      `./mvnw clean verify`)?
- [ ] Is every line something the team can explain in the exam?

## 7. Official documentation

When in doubt, follow these and use their simplest documented approach:

* React: https://react.dev/learn
* Redux Toolkit: https://redux-toolkit.js.org/ and Redux TypeScript guide: https://redux.js.org/usage/usage-with-typescript
* TypeScript: https://www.typescriptlang.org/docs/
* Testing Library: https://testing-library.com/docs/queries/about
* Jest: https://jestjs.io/docs/getting-started
* Vite: https://vite.dev/guide/
* Node.js: https://nodejs.org/docs/latest-v22.x/api/
* Spring Boot: https://docs.spring.io/spring-boot/ and guides: https://spring.io/guides
* Spring Data JPA: https://docs.spring.io/spring-data/jpa/reference/
* Java 21: https://docs.oracle.com/en/java/javase/21/
