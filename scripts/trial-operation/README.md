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

Run local browser smoke after backend `test` profile and frontend dev server are already running:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\browser-smoke.ps1
```

This script logs in with the fixed H2 sample accounts, writes only redacted browser-smoke reports, captures screenshots for the key admin / customer / parttime pages under `project-logs/campus-relay/runtime/step-132-browser-smoke/`, and does not mutate bridge, auth, route, API, or page behavior. It requires `127.0.0.1:8080` and `127.0.0.1:5173` to be running before execution.

Run remote/internal-trial API and SPA shell smoke after the server stack is deployed:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase http://your-host:8080/api -FrontendBase http://your-host/
```

The remote smoke script uses the same fixed trial-operation sample accounts as the local smoke baseline. It checks admin / customer / parttime login, key protected API reads, and optional frontend SPA shell reachability. Reports are written under `project-logs/campus-relay/runtime/step-134-remote-smoke/` and redact host names by default. Do not commit reports that intentionally use `-NoRedact` or include real server addresses.

Run the post-deploy wrapper when you want remote smoke plus optional key-based SSH deployment checks in one read-only step:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\server-post-deploy-check.ps1 -ApiBase http://your-host/api -FrontendBase http://your-host/ -SshHost your-host -SshIdentity "$env:USERPROFILE\.ssh\campus_trial_ed25519"
```

The wrapper calls `remote-smoke.ps1`, optionally checks the server commit and Docker `LogConfig` through SSH, writes redacted reports, and does not modify server state. Do not commit reports generated with real host values unless they are redacted.

If a deployed frontend requires screenshot-level validation, reuse the local browser smoke script with explicit bases and an untracked runtime directory:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\browser-smoke.ps1 -ApiBase http://your-host:8080/api -FrontendBase http://your-host/ -RuntimeDir .\tmp\remote-browser-smoke
```

This browser smoke writes current URLs into its report, so store remote reports outside committed project logs unless the host is safe to disclose.

Run Android API base layering checks:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-api-base-check.ps1
```

Include Android API base checks in preflight:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunAndroidApiBaseCheck
```

Check a real public/internal-trial API base before building a public Android APK:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-public-api-smoke.ps1 -ApiBase https://your-domain.example.com/api
```

This smoke only checks the read-only public dependencies used by the Android user campus order entry. It writes a redacted JSON report under `project-logs/campus-relay/runtime/step-121-public-api-base/` and does not mutate orders, tokens, bridge behavior, or server data. If the API base fails here, the Android public build can still be compiled, but WebView API smoke remains blocked until the server route/proxy is fixed.

Run Android public WebView user smoke after public APK sync/build:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-webview-user-public-smoke.ps1 -StartEmulator -ClearData
```

Run Android public WebView part-time smoke after public APK sync/build:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-webview-parttime-public-smoke.ps1 -StartEmulator -ClearData
```

Run both Android public WebView smokes and write a combined readiness report:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-webview-public-smoke.ps1 -StartEmulator -ClearData
```

These two WebView smoke scripts read the API base from ignored local files by default:

1. `frontend/.env.android-user-public`
2. `frontend/.env.android-parttime-public`

They drive the real Android WebView through DevTools/CDP, log in through the visible login pages, call the public API base from inside the WebView context, and write redacted reports under `project-logs/campus-relay/runtime/step-123-android-public-webview/` or `project-logs/campus-relay/runtime/step-124-android-public-webview-readiness/`. They do not commit the real public host, token, server credentials, or secrets.

Use the combined readiness script before owner-controlled Android public smoke or internal-trial handoff. If real external users will install the APK, do not treat HTTP cleartext as production-ready: move to a real domain, HTTPS certificate, reverse proxy, and then tighten Android cleartext settings in a separate release step.

Treat closed ports as hard failures:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -CheckPorts -StrictPorts
```

Use `docs/trial-operation-preflight.md` for the full manual runbook. This script is only a small executable entrypoint for repeatable local checks.

## Command Index

Print the local trial-operation command index:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\commands.ps1
```

Print browser entrypoints too:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\commands.ps1 -Full
```

The command index does not start backend/frontend processes and does not reset H2 automatically. It only prints the approved local commands and reset boundary.

## Android Smoke

List online Android devices or emulators:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -ListDevices
```

Install and launch both Debug APKs, then save launch screenshots:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1
```

Start the prepared `campus_api35` emulator first, then run the same install / launch flow:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -StartEmulator
```

Use a specific device when more than one device is online:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -DeviceId <adb-serial>
```

Wait longer before each launch screenshot:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -DeviceId emulator-5554 -LaunchWaitSeconds 8
```

Clear app data before launching when validating clean first-entry routing:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -StartEmulator -ClearData
```

The Android smoke script only installs, launches, and captures screenshots from the existing APKs. It does not modify backend/frontend business code, bridge behavior, auth, routes, or API calls. If no device is online, the script fails explicitly instead of pretending the APK smoke passed.

Current local emulator baseline:

1. AVD name: `campus_api35`
2. System image: `system-images;android-35;google_apis;x86_64`
3. Required emulator acceleration: Android Emulator Hypervisor Driver or an equivalent Windows hypervisor setup.

If `adb devices` stays empty after starting the emulator and `emulator -accel-check` reports that the Android Emulator hypervisor driver is missing, run the installer as Administrator:

```powershell
C:\Users\20278\AppData\Local\Android\Sdk\extras\google\Android_Emulator_Hypervisor_Driver\silent_install.bat
```

Current local Android WebView API baseline:

1. Default emulator build-time API base lives in `frontend/.env.android-user` and `frontend/.env.android-parttime`.
2. Explicit emulator modes live in `frontend/.env.android-user-emulator` and `frontend/.env.android-parttime-emulator`.
3. LAN phone builds should copy `frontend/.env.android-user-lan.example` / `frontend/.env.android-parttime-lan.example` to ignored local env files and replace the example IP.
4. Public/internal-trial builds should copy `frontend/.env.android-user-public.example` / `frontend/.env.android-parttime-public.example` to ignored local env files and prefer an HTTPS API base.
5. Backend `dev` / `test` profiles allow `http://localhost`, `https://localhost`, and `capacitor://localhost` for local WebView CORS.
6. Both Capacitor shells allow local cleartext HTTP for smoke only; real server / public trial builds should switch to HTTPS and a real API base.

Step 116 verified on `emulator-5554`:

1. User app package `com.xiaoyu.campus.user` launched to `/user/login`.
2. Part-time app package `com.xiaoyu.campus.parttime` launched to `/parttime/login`.
3. Part-time WebView token login, profile, review status, and available-orders calls returned 200.
4. User WebView login returned 200 and reached `/user`.

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
