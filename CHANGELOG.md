# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-04-04

### Added

- Public repository delivery documents: README, delivery guide, changelog, contributing guide, security guide
- Production-readiness documents and migration scripts
- Public repository issue templates and pull request template

### Changed

- Unified order table to `orders`
- Unified order query contract across frontend and backend
- Added real statistics aggregation APIs for dashboard and statistics pages
- Split admin and customer authentication flows
- Replaced user-side mock data with real API integration
- Upgraded password storage to BCrypt with legacy MD5 login migration
- Hardened JWT secret validation, upload handling, and CORS configuration
- Switched local development database to MariaDB
- Refined the GitHub landing page README and docs index for public repository presentation

### Removed

- Delivery noise such as build artifacts, temporary runtime logs, private editor settings, and AI workspace metadata
