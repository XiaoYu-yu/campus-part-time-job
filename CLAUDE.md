# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Quick commands

Backend (Spring Boot 3.2.4 / JDK 17 / Maven):
```powershell
cd backend
.\mvnw.cmd -DskipTests compile               # Compile only
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test   # Run with H2 (no MySQL needed)
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev    # Run with local MySQL
.\mvnw.cmd test -pl . -Dtest=ClassNameTest   # Run single test class
.\mvnw.cmd test                               # Run all tests (requires H2, no MySQL needed)
```

Frontend (Vue 3 / Vite / Element Plus):
```powershell
cd frontend
npm install         # First time only
npm run dev         # Dev server on :5173, proxies /api → :8080
npm run build       # Production build
npm run lint        # ESLint
npm run test        # Vitest (2 spec files)
```

## Project identity

This is a campus part-time courier relay platform (校园代送 / 校内兼职), incrementally built on top of a legacy takeaway ("苍穹外卖") codebase. The legacy takeaway modules are preserved and must not be deleted.

## Architecture summary

### Backend (`backend/`)

Two parallel domain spaces coexist:

| Layer | Legacy (unchanged) | Campus (active development) |
|-------|-------------------|---------------------------|
| Package | `com.cangqiong.takeaway` | `com.cangqiong.takeaway.campus` |
| Tables | `employee`, `user`, `category`, `dish`, `cart`, `orders`, etc. | `campus_relay_order`, `campus_courier_profile`, `campus_settlement_record`, `campus_exception_record`, `campus_after_sale_execution_record`, `campus_settlement_batch_operation_record`, `campus_settlement_reconcile_difference_record`, `campus_location_report`, `campus_pickup_point` |
| Auth model | employee / user JWT | customer / courier / employee JWT, enforced by URI prefix |

Key architectural decisions:
- **JWT interceptor** (`interceptor/JwtInterceptor.java`) applies to all paths except `/error`, `/h2-console/**`. It determines required user type from the URI prefix (`/api/campus/admin/` → employee, `/api/campus/customer/` → customer, `/api/campus/courier/` → courier) and rejects mismatches with 403.
- **Public paths** bypass auth entirely: `/api/campus/public/**`, `/api/campus/courier/auth/token`, login endpoints, and `GET /api/public/**`.
- **Bridge paths** (`/api/campus/courier/profile`, `/api/campus/courier/review-status`) accept both customer and courier tokens to support onboarding flow before a courier token is available.
- **Standard result envelope**: every controller response is wrapped in `Result<T>` with fields `code` (200=success), `msg`, `data`.
- **H2 schema** at `src/main/resources/db/schema-h2.sql` defines all tables including legacy ones; `data-h2.sql` seeds demo data. Test profile uses H2 in-memory with MySQL compatibility mode.

### Frontend (`frontend/`)

Three-role SPA controlled by router guards (`router/index.js`):
1. **Admin** (default web): routes under `/dashboard`, `/campus/*`, guarded by `admin_token` in localStorage
2. **Customer** (web + mobile shell): routes under `/user/*`, guarded by `customer_token`
3. **Courier/Parttime** (mobile shell): routes under `/parttime/*` and `/courier/*`, guarded by `courier_token`

The `VITE_APP_SHELL` env var controls which shell is active. In standard `dev` mode, all three roles are accessible via different URL paths.

**API layer** (`src/api/`): one module per domain. All use a shared axios instance (`utils/request.js`) that:
- Auto-attaches the correct token based on URL prefix
- On 401, clears the relevant token and redirects to the role-appropriate login
- Routes: customer → `/user/login`, courier → `/parttime/login`, admin → `/login`

**Stores** (Pinia): `customer.js`, `user.js` (admin), `courier.js`. Auth state is persisted to localStorage.

**Android shells**: `npm run build:android:user:*` and `npm run build:android:parttime:*` produce Capacitor-ready builds with explicit API base URLs for emulator, LAN, or public deployments. These modes reject the default `/api` proxy.

### Profiles

| Profile | Database | Use case |
|---------|----------|----------|
| `test` | H2 in-memory | Local dev without MySQL, CI, integration tests |
| `dev` | MySQL `cangqiong_takeaway` on `127.0.0.1:3306` | Local dev with real DB |
| `prod` | MySQL (server-deployed) | Docker Compose deployment |

The test profile auto-runs `schema-h2.sql` + `data-h2.sql` on startup. The dev profile never runs schema init (`spring.sql.init.mode=never`).

## Active constraints

These are hard rules; do not violate them without explicit task instructions:

1. Do not delete or refactor legacy takeaway modules (tables, packages, pages) — keep them running
2. New campus tables use `campus_` prefix; new campus Java code goes under `com.cangqiong.takeaway.campus`
3. Bridge (compatibility layer between old and new) is frozen at Phase A no-op — do not extend it
4. Payment is mock-only; do not integrate real payment, refund, or payout gateways
5. After a courier picks up an order, cancellation is forbidden — only after-sale or exception flows
6. Polish/media/bridge lines are frozen; the current active line is ops hardening + Android internal testing
7. Update `project-logs/campus-relay/` after every non-trivial campus change with: goal, completed items, changed files, remaining issues, next steps

## Demo accounts (test profile / seed data)

| Role | Phone | Password |
|------|-------|----------|
| admin | 13800138000 | 123456 |
| customer | 13900139000 | 123456 |
| courier | applied via customer onboarding | 123456 |

## Key docs

- `AGENTS.md` — full collaboration rules (superset of the constraints above)
- `docs/api-overview.md` — API endpoint inventory
- `docs/db-overview.md` — database schema overview
- `docs/deployment/internal-trial-compose.md` — Docker Compose deployment
- `project-logs/campus-relay/summary.md` — complete development history
- `project-logs/campus-relay/pending-items.md` — open work items
- `project-logs/campus-relay/global-working-memory.md` — quick context recovery
