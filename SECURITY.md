# Security Policy

## Supported use

This repository is published as a training project. It is suitable for:

- Learning
- Local development
- Demo environments
- Internal testing

It is not positioned as a production-grade hosted service without additional hardening.

## Reporting a vulnerability

Do not open a public issue for a security vulnerability.

Instead:

1. Prepare a short report with:
   - affected area
   - reproduction steps
   - impact
   - suggested mitigation if known
2. Send the report privately to the repository owner
3. Wait for confirmation before publishing details

## Current security baseline

- Passwords use BCrypt for active storage
- Legacy MD5 passwords are migrated on successful login
- Production JWT secret must be explicitly configured
- Uploads are validated by extension, MIME type, size, and image parsing
- Production CORS origins must be explicitly configured

## Out of scope for the current training version

- CAPTCHA
- Rate limiting
- Object storage malware scanning
- Centralized audit logging
- Managed secret rotation
