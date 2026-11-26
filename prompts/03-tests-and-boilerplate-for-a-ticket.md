# Tests and boilerplate for a ticket (human writes the main logic)

**Approved on:** pending (review in the Pull Request that adds this file)
**Approved by:** pending
**Used for:** splitting a ticket into a human part and an AI part. The AI writes only boilerplate (DTOs, test data)
and the unit tests that describe the expected behaviour. A team member writes the main logic and makes the tests pass.

## Prompt

```
Ticket: <JIRA_KEY> <title>
Acceptance criteria: <paste them from Jira>

Follow CLAUDE.md, docs/ARCHITECTURE.md and the protube-coding-style skill.

1. Split the ticket into:
   - Human part: the main logic and the decisions a team member must be able to explain in the exam
     (business rules, mapping of data, controllers/services that are the core of the ticket).
   - AI part: boilerplate only (DTO classes, test data files) and the unit tests.
2. Write the AI part only. The tests are the specification of the human part: name every class and method
   the human must write, and test each acceptance criterion with one behaviour per test (Arrange / Act / Assert).
3. Do NOT write the human part. Write a step-by-step guide for it instead: what each class and method must do,
   which existing classes to use, the APIs needed with a link to their official docs, and how to check it
   (which tests must pass). The guide must be understandable by any team member without more context.
   Deliver the guide as a .txt file to the team member. It is a working document: it is NOT committed to Git.
4. Before handing over, check privately that the tests and the guide are consistent (a throw-away implementation
   that is not committed and not shown).
5. Add one row for the ticket to docs/AI_USAGE.md: what the AI did (with this prompt), what the team member does,
   and why the ticket was split that way. Only the justification, no instructions.
6. Commit the AI part and the docs/AI_USAGE.md row alone, marked with AI-assisted: yes and this prompt file.
```

## Notes

* The human part is committed separately by the team member. If the team member also uses AI for it, that commit is
  marked too (AI-assisted: yes and its prompt).
* First used in PG-20 (load the videoGrabber videos and list them).
* Changed after PG-23: the guide is a .txt outside Git (it used to be committed as docs/ai-usage/PG-xx-human-part.md);
  only the justification goes to Git, in docs/AI_USAGE.md. The full workflow is in the ticket-workflow skill.
