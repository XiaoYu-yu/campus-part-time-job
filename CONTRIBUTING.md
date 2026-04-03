# Contributing

## Scope

This repository is a training project. Contributions should prioritize:

- Clear code structure
- Stable local startup on Windows
- Frontend/backend contract consistency
- Minimal, reviewable changes

Avoid submitting purely visual reworks or unrelated framework migrations unless they fix a concrete issue.

## Local setup

### Backend

- JDK 17
- Maven: use the bundled [apache-maven-3.9.14](D:/20278/code/show_shop1/apache-maven-3.9.14)
- MariaDB running on `127.0.0.1:3306`
- Start with `--spring.profiles.active=dev`

### Frontend

- Node.js 18+
- Run `npm install`
- Start with `npm run dev`

## Development rules

- Keep API contracts explicit and synchronized across frontend and backend
- Prefer fixing the root cause instead of patching UI symptoms
- Do not commit build artifacts such as `dist/`, `target/`, or `node_modules/`
- Keep docs updated when changing startup steps, environment variables, or data flow

## Validation checklist

Before opening a pull request, run:

```bash
cd backend
..\apache-maven-3.9.14\bin\mvn.cmd test
..\apache-maven-3.9.14\bin\mvn.cmd -DskipTests compile
```

```bash
cd frontend
npm install
npm run lint
npm run test
npm run build
```

## Pull request expectations

- Describe the problem being solved
- Describe the implementation choice
- List any config, database, or contract changes
- Include verification commands and results
