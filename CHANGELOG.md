# Changelog

## [2.0.0-campus-trial] - 2026-04-14

### Added

- Campus relay domain under `com.cangqiong.takeaway.campus`.
- Customer onboarding, courier token handoff, courier workbench, and completed result readback.
- Admin campus operation pages for settlement, after-sale execution, courier ops, and exception handling.
- Exception history with admin resolve.
- After-sale execution history with frontend read-only drawer.
- Delivery packaging, demo script, media checklist, and project handoff logs.

### Changed

- Project documentation now reflects the campus part-time delivery trial system instead of the original takeaway-only training project.
- Legacy takeaway documents are archived under `docs/legacy-takeaway/` and `project-logs/legacy-takeaway/`.
- Root README, docs index, frontend README, deployment notes, and contribution/security docs now use current campus terminology.

### Frozen

- Bridge line remains in `Phase A no-op` freeze.
- Display polish line remains in maintenance/freeze mode.
- Media collection line is closed unless new delivery evidence is needed.

## [1.0.0-legacy-takeaway] - 2026-04-04

### Added

- Public repository delivery documents.
- Production-readiness documents and migration scripts.
- GitHub PR template.

### Changed

- Unified old takeaway order table and query contracts.
- Split admin and customer authentication flows.
- Replaced user-side mock data with real API integration.
- Hardened JWT secret validation, upload handling, and CORS configuration.

### Removed

- Delivery noise such as build artifacts, temporary runtime logs, private editor settings, and AI workspace metadata.
