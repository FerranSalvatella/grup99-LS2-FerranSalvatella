# Didactic coding style skill

**Approved on:** pending team review
**Approved by:** pending (to be approved in the PR that adds `.claude/skills/protube-coding-style/`)
**Used for:** defining the shared AI coding style (programming level and conventions) as a project skill

## Prompt

> Original prompt was written in Spanish; this is a faithful English translation.

Let's create a skill, a project skill, that defines how we are going to program and the programming level. It has to
be academic: we focus on naming and on the basics, and we do not over-optimize. Not everything needs to be optimized;
it has to be a didactic project where a beginner can read the code and understand it clearly. Not through comments,
but by not using strange methods, strange libraries or ultra-optimized techniques. If you need an `if` and it does not
have to be on a single line, write it on several lines so it is clear. Do not use a method just because it is faster
if almost nobody knows it and it is unusual. Follow the course slides and keep things clear. If in doubt, check the
official documentation of the stack we use (React, Node, etc.).

## Notes

* Output: `.claude/skills/protube-coding-style/SKILL.md`, referenced from `CLAUDE.md`.
* Open team decisions written as defaults in the skill (change them in review if the team disagrees): avoid Lombok in
  new code, constructor injection instead of field `@Autowired`, Redux Toolkit as the Redux flavour, one HTTP client
  per feature (`fetch` vs `axios`).
