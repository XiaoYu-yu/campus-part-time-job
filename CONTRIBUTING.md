# Contributing

## Scope

This repository is now centered on the campus part-time delivery trial system. The legacy takeaway modules remain in place, but new work should prioritize the `campus` domain unless a task explicitly targets legacy behavior.

## Local setup

### Backend

```powershell
cd D:\20278\code\Campus part-time job\backend
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test
```

### Frontend

```powershell
cd D:\20278\code\Campus part-time job\frontend
npm install
npm run dev
```

## Development rules

1. Do not delete legacy takeaway modules unless the task explicitly says so.
2. New campus tables must use the `campus_` prefix.
3. New campus Java code should stay under `com.cangqiong.takeaway.campus`.
4. Keep API contracts explicit and synchronized across frontend and backend.
5. Keep bridge behavior frozen unless a task explicitly reopens that line.
6. Do not commit build artifacts such as `dist/`, `target/`, or `node_modules/`.
7. Update `project-logs/campus-relay/` when changing campus behavior or project status.

## Validation checklist

```powershell
cd backend
.\mvnw.cmd -DskipTests compile
```

```powershell
cd frontend
npm run lint
npm run test
npm run build
```

## Pull request expectations

1. Describe the problem being solved.
2. Describe the implementation choice.
3. List config, database, API, or route changes.
4. Include verification commands and results.
5. State whether bridge, auth, token attachment, legacy modules, or old order/cart/address semantics were touched.
