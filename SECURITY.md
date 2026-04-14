# Security Policy

## Supported use

This repository is suitable for:

- local development
- course or project demonstration
- trial-operation validation
- internal testing

It is not a full production-grade hosted service until external payment, mapping, monitoring, rate limiting, secret management, and operational security have been completed.

## Current security baseline

1. JWT-based authentication is used for admin, customer, and courier paths.
2. Password storage uses BCrypt for active storage.
3. Production JWT secret must be explicitly configured.
4. Production CORS origins must be explicitly configured.
5. Uploads are validated by extension, MIME type, size, and image parsing.
6. Bridge endpoints remain intentionally retained in `Phase A no-op` freeze status.

## Current out-of-scope items

1. CAPTCHA.
2. Rate limiting.
3. SMS verification.
4. Payment provider signing.
5. Map provider key management.
6. Centralized audit logging.
7. Managed secret rotation.
8. WAF / bot defense.

## Reporting a vulnerability

Do not open a public issue for a security vulnerability.

Prepare a short report with:

1. affected area
2. reproduction steps
3. impact
4. suggested mitigation if known

Send it privately to the repository owner and wait for confirmation before publishing details.
