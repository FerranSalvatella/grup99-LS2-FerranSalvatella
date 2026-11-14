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
   which existing classes to use, the Spring / Java APIs needed with a link to their official docs, and how to
   check it (which tests must pass). The guide must be understandable by any team member without more context.
4. Before handing over, check privately that the tests and the guide are consistent (a throw-away implementation
   that is not committed and not shown).
5. Commit the AI part alone, marked with AI-assisted: yes and this prompt file.
```

## Notes

* The human part is committed separately by the team member, without the AI trailers.
* First used in PG-20 (load the videoGrabber videos and list them).
