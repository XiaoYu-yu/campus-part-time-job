# Database Seed Data

This directory contains optional data seeds for validation environments.

- `internal-trial-data.sql` is for isolated internal-trial / LAN smoke deployments.
- Production schema changes must go through `backend/db/migrations`.
- Do not put required production business data or secrets in seed files.

The standalone Podman deployment applies `internal-trial-data.sql` only when
`APP_SEED_INTERNAL_TRIAL=1` after Flyway has created or migrated the schema.
