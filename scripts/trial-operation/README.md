# Trial Operation Scripts

This directory contains local helper scripts for trial-operation checks. The scripts are intentionally conservative:

1. They do not change business data.
2. They do not reset H2 automatically.
3. They do not start long-running backend or frontend processes.
4. They do not print local Tencent map keys.
5. They do not modify bridge, auth, route, API, or frontend page behavior.

## Preflight

Run the lightweight local check:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1
```

Check local backend/frontend ports as warnings:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -CheckPorts
```

Run full compile/build validation:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunBackendCompile -RunFrontendBuild
```

Run seed sample validation:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\validate-samples.ps1
```

Run preflight with sample validation:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunSampleValidation
```

Treat closed ports as hard failures:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -CheckPorts -StrictPorts
```

Use `docs/trial-operation-preflight.md` for the full manual runbook. This script is only a small executable entrypoint for repeatable local checks.

## Sample Validation

`validate-samples.ps1` reads only `backend/src/main/resources/db/schema-h2.sql` and `backend/src/main/resources/db/data-h2.sql`.

It checks the fixed local trial-operation anchors:

1. Admin, customer, and courier seed accounts.
2. The available order `CR202604070002`.
3. The completed order `CR202604060001`.
4. Settlement and location sample anchors.
5. Exception, after-sale history, and reconcile difference table schemas.

Optional runtime samples are warnings by default because several records are intentionally created during validation flows. Use `-StrictOptional` only when those optional records must be pre-seeded.

Exit code summary:

1. `0`: all required and optional checks passed.
2. `1`: at least one hard failure exists.
3. `2`: required checks passed, optional sample warnings exist.
