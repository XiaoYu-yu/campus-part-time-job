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

Treat closed ports as hard failures:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -CheckPorts -StrictPorts
```

Use `docs/trial-operation-preflight.md` for the full manual runbook. This script is only a small executable entrypoint for repeatable local checks.
