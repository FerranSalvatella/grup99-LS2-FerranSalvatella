// Checks every commit message (husky runs it in the commit-msg hook).
// Rules: Conventional Commits (type(scope): summary) + a reference to the Jira ticket, e.g. "Refs: PG-20".
export default {
  extends: ['@commitlint/config-conventional'],
  parserPreset: {
    parserOpts: {
      // Jira keys of our project start with "PG-"
      issuePrefixes: ['PG-'],
    },
  },
  rules: {
    // Every commit must mention its ticket (Refs: PG-<number>)
    'references-empty': [2, 'never'],
  },
};
