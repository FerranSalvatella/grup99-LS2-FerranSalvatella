---
name: sprint-review
description: Prepare the Sprint Review presentation for ProTube (Software Lab II) using the team template in this folder. Use it when the team asks to prepare, fill in or review the sprint review slides of a sprint.
---

# Sprint review — ProTube

The sprint review is built from the template in this folder:
`.claude/skills/sprint-review/sprint-review-template.pptx`.

Every slide of the template has speaker notes that start with `TEMPLATE.` and explain what goes
on that slide. Read them and follow them. This file summarises them.

## 1. Golden rules

1. **Never invent data.** Ticket numbers, story points, statuses, names, roles, problems,
   coverage numbers and links come from the team, the board, the pull requests or the CI.
   If a value is missing, ask the developer. Do not fill it with an example.
2. **Replace all the example content.** The template contains example data (Team 3, Anna, Marc,
   Laia, Pol, tickets #21–#28, 72% coverage...). None of it belongs to our team.
3. **Keep the order and the sections** of the template. Only adjust the times to the slot.
4. **English only**, as every other project artefact.
5. **The review is about the product, not the slides.** Most of the time goes to the live demo.

## 2. Data to collect before filling the slides

Ask the team (or read the board / GitHub) for:

- Sprint number, team number, project name, names of all members.
- Each member's Scrum role this sprint and what they worked on.
- The sprint backlog as committed in sprint planning: ticket, task / user story, points.
- The real status of each committed item at the end of the sprint.
- The features to show in the demo and the demo steps.
- What went well, the problems found, how they were solved and their impact.
- The proposal for the next sprint: ticket, task / user story, points.
- Links: repository, board, deployed app, CI.

## 3. Slide by slide

| # | Slide | What goes in it |
|---|---|---|
| 1 | Cover | Sprint number, team number, project name, all member names. |
| 2 | Agenda | Keep the sections and order. Adjust the times to the slot (template: 20 minutes). |
| 3 | The team | One card per member: name, Scrum role, what they worked on. With 3 members, delete the whole fourth card. |
| 4 | Section 01 — The sprint | Section divider. |
| 5 | What we committed to do | Every committed item exactly as it was on the board, with ticket and points. Do not edit it after the fact. More than 6 items: add a second slide like this one. |
| 6 | What we finally did | Same list with the real status. Only three values: **Done**, **Partially done** (say which part) or **Not done**. Not merged = not done. |
| 7 | Sprint result | One sentence with the result in numbers (done points of committed points, done tasks of committed tasks), then the key facts. Only Done items count. This is the team velocity. |
| 8 | Section 02 — Demo | Section divider. |
| 9 | New features | One card per feature (up to 4), what the user can do now, in the user's words. Demo on the app built from `main`, with its version. |
| 10 | Demo script | Five steps that tell a story from the user's point of view. Rehearse it and have a backup video. |
| 11 | Section 03 — Looking back | Section divider. |
| 12 | What went well | Three main points (left) and other positive points (right). Be specific. |
| 13 | Problems found | Problem, how we solved it, impact on the sprint. Technical and organisational. Short cell texts. |
| 14 | Why we did not finish everything | Left: every committed item that is not Done. Right: the honest reasons. Last line: what the team will change. |
| 15 | Section 04 — Next steps | Section divider. |
| 16 | Next sprint | Unfinished work first, then new work from the backlog, with points. Total close to this sprint's velocity. It is a proposal: the commitment is made in sprint planning. |
| 17 | Questions | Links to repository, board, deployed app and CI; team and member names. |

## 4. Definition of Done used in the review

An item is **Done** only if it is merged, tested and the CI is green.

## 5. Output

- Work on a copy of the template, never on the template itself.
- Name the copy with the sprint number, e.g. `sprint-02-review.pptx`.
- Keep the template design: only change the text, and delete whole cards or rows when there
  are fewer items.
- Before finishing, check that no example data from the template is left.
