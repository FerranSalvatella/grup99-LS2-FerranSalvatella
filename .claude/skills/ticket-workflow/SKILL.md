---
name: ticket-workflow
description: How a Jira ticket of ProTube is worked on with Claude, step by step - split between AI part and team member part, specification tests, the implementation guide (a .txt outside Git) and the AI justification (docs/AI_USAGE.md in Git). Use it whenever the team asks to prepare, start or continue a ticket (PG-xx).
---

# Ticket workflow — ProTube

How every ticket (PG-xx) is worked on with Claude. Follow `CLAUDE.md`, `docs/ARCHITECTURE.md`, `docs/WORKFLOW.md`
and the `protube-coding-style` skill. The prompt used for the AI part is
`prompts/03-tests-and-boilerplate-for-a-ticket.md`.

## Golden rules

1. **The guide is not in Git.** The implementation guide for the team member is a working document. Deliver it as a
   `.txt` file to the team member and **never commit it** (no `docs/ai-usage/...human-part.md`, no guide files).
2. **The AI justification is in Git.** Every ticket with AI work adds one row to `docs/AI_USAGE.md`: what the AI
   did, with which prompt, what the team member does, and why. Only the justification: no steps, no instructions,
   no exam questions.
3. **Never invent.** Read the ticket in Jira and the code before deciding anything. Ask the team when something is
   not decided.
4. **Every AI commit is marked:** `Refs: PG-xx`, `AI-assisted: yes`, `Prompt: prompts/03-...`.
5. **Never merge red.** A pull request is merged only with the CI green.

## Steps for one ticket

1. **Read the ticket** in Jira (summary, acceptance criteria, sprint) and the code it touches. Move it to
   *En curso*.
2. **Branch** from an updated `develop` (or from the branch it depends on, and say so): `feature/PG-xx-short-name`.
3. **Split the ticket:**
   * **Team member part:** the main logic and every decision the team member must be able to explain in the exam.
   * **AI part:** boilerplate (DTOs, types, presentational components, helpers, config) and the **specification
     tests** of the team member part (one behaviour per test, exact names and texts).
4. **Write the AI part only.**
5. **Check privately** that the tests can pass: write a throw-away implementation in a scratch copy, run the same
   commands as the CI (`./mvnw clean verify -Pcoverage`, `npm run test`, `npm run lint`, `npm run build`), then
   delete it. Never commit it and never show it.
6. **Add the row** of the ticket to `docs/AI_USAGE.md`.
7. **Commit and push the AI part** (with the `docs/AI_USAGE.md` row) as one marked commit.
8. **Write the guide** for the team member part and deliver it as `GUIA-PG-xx.txt` (outside the repository folder):
   * context of the ticket, what is already done, what is missing;
   * what each file / method must do, the exact texts and names the tests expect;
   * generic examples (not the solution) and links to the official docs;
   * how to check it (commands, number of tests) and the commit message;
   * neutral wording: it explains what to implement, not who must implement it.
9. **The team member writes and commits their part.** If they paste code to Claude, Claude applies it, runs the CI
   commands and reports problems. Claude commits it only when the team member asks explicitly; the author of that
   commit is the team member, and the message says it was committed from a Claude session. If AI was used for that
   part, the commit says so (`AI-assisted: yes` and the prompt).
10. **Pull request** to `develop` with the repository template. Wait for the CI to be green, then merge. Move the
    ticket in Jira (*En revisión*, then *Finalizada* after the merge).

## docs/AI_USAGE.md row format

| Ticket | AI part (prompt) | Team member part | Why this split |
|--------|------------------|------------------|----------------|
| PG-xx  | What the AI wrote, with its commit(s) (`prompts/03-...`) | What the team member writes, with its commit(s) | One or two sentences |
