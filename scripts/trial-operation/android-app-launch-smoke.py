#!/usr/bin/env python3
"""Install and launch Android QA apps without taking screenshots.

This script is intentionally lightweight: it verifies ADB connectivity, can
install the two debug APKs, launches each package, and writes a JSON report.
It does not capture screenshots or inspect UI pixels.
"""

from __future__ import annotations

import argparse
import json
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path


USER_PACKAGE = "com.xiaoyu.campus.user"
PARTTIME_PACKAGE = "com.xiaoyu.campus.parttime"


def run_command(args: list[str], timeout: int = 60) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        args,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        encoding="utf-8",
        errors="replace",
        timeout=timeout,
    )


def adb(adb_path: str, device_id: str | None, args: list[str], timeout: int = 60) -> subprocess.CompletedProcess[str]:
    command = [adb_path]
    if device_id:
        command.extend(["-s", device_id])
    command.extend(args)
    return run_command(command, timeout=timeout)


def resolve_device(adb_path: str, explicit_device: str | None) -> str | None:
    if explicit_device:
        return explicit_device

    result = run_command([adb_path, "devices"], timeout=20)
    if result.returncode != 0:
        raise RuntimeError(result.stderr.strip() or result.stdout.strip() or "adb devices failed")

    devices: list[str] = []
    for line in result.stdout.splitlines():
        parts = line.split()
        if len(parts) >= 2 and parts[1] == "device":
            devices.append(parts[0])

    if not devices:
        raise RuntimeError("No online Android device found.")
    if len(devices) > 1:
        raise RuntimeError(f"Multiple devices found: {', '.join(devices)}. Pass --device-id.")
    return devices[0]


def install_apk(adb_path: str, device_id: str | None, apk: Path) -> dict:
    result = adb(adb_path, device_id, ["install", "-r", "-t", "-g", str(apk)], timeout=180)
    return {
        "apk": str(apk),
        "ok": result.returncode == 0 and "Success" in result.stdout,
        "stdout": result.stdout.strip(),
        "stderr": result.stderr.strip(),
    }


def package_installed(adb_path: str, device_id: str | None, package_name: str) -> bool:
    result = adb(adb_path, device_id, ["shell", "pm", "path", package_name], timeout=30)
    return result.returncode == 0 and f"package:" in result.stdout


def launch_package(adb_path: str, device_id: str | None, package_name: str, wait_seconds: int) -> dict:
    result = adb(
        adb_path,
        device_id,
        ["shell", "monkey", "-p", package_name, "-c", "android.intent.category.LAUNCHER", "1"],
        timeout=60,
    )
    time.sleep(wait_seconds)
    focus = adb(adb_path, device_id, ["shell", "dumpsys", "window"], timeout=30)
    focus_lines = [
        line.strip()
        for line in focus.stdout.splitlines()
        if "mCurrentFocus" in line or "mFocusedApp" in line
    ]
    return {
        "package": package_name,
        "ok": result.returncode == 0 and "Events injected: 1" in result.stdout,
        "stdout": result.stdout.strip(),
        "stderr": result.stderr.strip(),
        "focusedWindow": focus_lines[:5],
    }


def main() -> int:
    parser = argparse.ArgumentParser(description="Android app install/launch smoke without screenshots.")
    parser.add_argument("--adb", default="adb", help="Path to adb executable.")
    parser.add_argument("--device-id", default="", help="ADB device id when multiple devices are connected.")
    parser.add_argument("--user-apk", default="", help="Optional user app APK path to install before launch.")
    parser.add_argument("--parttime-apk", default="", help="Optional parttime app APK path to install before launch.")
    parser.add_argument("--wait-seconds", type=int, default=4, help="Seconds to wait after each launch.")
    parser.add_argument("--output", required=True, help="JSON report output path.")
    parser.add_argument("--no-redact-device", action="store_true", help="Write the real ADB device id to the report.")
    args = parser.parse_args()

    try:
        device_id = resolve_device(args.adb, args.device_id or None)
        installs: list[dict] = []
        if args.user_apk:
            installs.append(install_apk(args.adb, device_id, Path(args.user_apk)))
        if args.parttime_apk:
            installs.append(install_apk(args.adb, device_id, Path(args.parttime_apk)))

        checks = []
        for package_name in (USER_PACKAGE, PARTTIME_PACKAGE):
            installed = package_installed(args.adb, device_id, package_name)
            launch = launch_package(args.adb, device_id, package_name, args.wait_seconds) if installed else None
            checks.append(
                {
                    "package": package_name,
                    "installed": installed,
                    "launch": launch,
                }
            )

        report = {
            "name": "Android app launch smoke",
            "generatedAt": datetime.now().isoformat(timespec="seconds"),
            "deviceId": device_id if args.no_redact_device else "<redacted>",
            "deviceIdRedacted": not args.no_redact_device,
            "installs": installs,
            "checks": checks,
            "summary": {
                "ok": all(item["installed"] and item["launch"] and item["launch"]["ok"] for item in checks)
                and all(item["ok"] for item in installs),
                "installedPackages": [item["package"] for item in checks if item["installed"]],
            },
            "notes": [
                "No screenshots are captured by this script.",
                "Use WebView smoke scripts only when UI route/API checks are needed.",
            ],
        }

        output = Path(args.output)
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
        print(f"Android app launch smoke completed: ok={report['summary']['ok']}")
        print(f"Report: {output}")
        return 0 if report["summary"]["ok"] else 1
    except Exception as exc:  # noqa: BLE001 - command-line smoke should report concise failures.
        print(f"Android app launch smoke failed: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
