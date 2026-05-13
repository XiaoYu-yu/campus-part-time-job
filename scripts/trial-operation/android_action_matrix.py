#!/usr/bin/env python3
"""
Android dual-end action matrix for owner-controlled internal trial.

The script intentionally keeps business code untouched. It uses ADB only for
device/app launch evidence and screenshots, then drives the campus order state
machine through the public API with test accounts.
"""

from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import subprocess
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from datetime import datetime
from pathlib import Path
from typing import Any, Dict, List, Optional, Tuple


REPO_ROOT = Path(__file__).resolve().parents[2]
USER_PACKAGE = "com.xiaoyu.campus.user"
PARTTIME_PACKAGE = "com.xiaoyu.campus.parttime"
CUSTOMER_PHONE = "13900139000"
COURIER_PHONE = "13900139001"
TEST_PASSWORD = "123456"
CONTROLLED_PROOF_URL = "/api/files/android-action-matrix-proof.png"


class MatrixError(Exception):
    pass


class ApiError(MatrixError):
    def __init__(self, name: str, status: Optional[int], code: Any, msg: str):
        super().__init__(f"{name} failed: http={status}, code={code}, msg={msg}")
        self.name = name
        self.status = status
        self.code = code
        self.msg = msg


def now_iso() -> str:
    return datetime.now().isoformat(timespec="seconds")


def read_api_base(value: str, env_file: Path) -> str:
    candidate = value.strip() if value else ""
    if not candidate and env_file.exists():
        for line in env_file.read_text(encoding="utf-8").splitlines():
            if line.strip().startswith("VITE_API_BASE_URL="):
                candidate = line.split("=", 1)[1].strip()
                break
    if not candidate:
        raise MatrixError(
            "Api base is required. Pass --api-base or create frontend/.env.android-user-public "
            "with VITE_API_BASE_URL."
        )
    candidate = candidate.rstrip("/")
    if not re.match(r"^https?://", candidate):
        raise MatrixError("Api base must start with http:// or https://")
    if not candidate.endswith("/api"):
        raise MatrixError("Api base must end with /api")
    return candidate


def redact_url(url: str) -> str:
    if not url:
        return ""
    parsed = urllib.parse.urlparse(url)
    port = f":{parsed.port}" if parsed.port else ""
    path = parsed.path.rstrip("/")
    return f"{parsed.scheme}://<redacted>{port}{path}"


def resolve_adb(explicit: str) -> str:
    if explicit:
        path = Path(explicit)
        if path.exists():
            return str(path)
        raise MatrixError(f"adb path does not exist: {explicit}")

    candidates: List[Path] = []
    for env_key in ("ANDROID_HOME", "ANDROID_SDK_ROOT"):
        value = os.environ.get(env_key)
        if value:
            candidates.append(Path(value) / "platform-tools" / "adb.exe")
    local_app_data = os.environ.get("LOCALAPPDATA")
    if local_app_data:
        candidates.append(Path(local_app_data) / "Android" / "Sdk" / "platform-tools" / "adb.exe")

    which = shutil.which("adb")
    if which:
        candidates.append(Path(which))

    for candidate in candidates:
        if candidate.exists():
            return str(candidate)
    raise MatrixError("adb.exe not found. Install Android platform-tools or pass --adb-path.")


def run_command(args: List[str], timeout: int = 30) -> Tuple[int, str]:
    proc = subprocess.run(
        args,
        cwd=str(REPO_ROOT),
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        timeout=timeout,
    )
    return proc.returncode, (proc.stdout or "") + (proc.stderr or "")


def adb_command(adb: str, device_id: str, command: List[str], timeout: int = 30) -> str:
    args = [adb]
    if device_id:
        args += ["-s", device_id]
    args += command
    code, output = run_command(args, timeout=timeout)
    if code != 0:
        raise MatrixError(f"adb {' '.join(command)} failed: {output.strip()}")
    return output


def list_devices(adb: str) -> List[str]:
    code, output = run_command([adb, "devices", "-l"])
    if code != 0:
        raise MatrixError(f"adb devices failed: {output.strip()}")
    devices: List[str] = []
    for line in output.splitlines():
        match = re.match(r"^(\S+)\s+device\s", line.strip())
        if match:
            devices.append(match.group(1))
    return devices


def resolve_device(adb: str, explicit: str) -> str:
    devices = list_devices(adb)
    if explicit:
        if explicit not in devices:
            raise MatrixError(f"Device '{explicit}' is not online. Current online devices: {devices}")
        return explicit
    if not devices:
        raise MatrixError("No online Android device found.")
    if len(devices) > 1:
        raise MatrixError(f"Multiple Android devices online: {devices}. Pass --device-id.")
    return devices[0]


def launch_app(adb: str, device_id: str, package_name: str, clear_data: bool, wait_seconds: int) -> None:
    if clear_data:
        adb_command(adb, device_id, ["shell", "pm", "clear", package_name], timeout=20)
    adb_command(
        adb,
        device_id,
        ["shell", "monkey", "-p", package_name, "-c", "android.intent.category.LAUNCHER", "1"],
        timeout=20,
    )
    time.sleep(wait_seconds)


def save_screenshot(adb: str, device_id: str, local_path: Path, name: str) -> Dict[str, str]:
    local_path.parent.mkdir(parents=True, exist_ok=True)
    remote_path = f"/sdcard/{name}"
    adb_command(adb, device_id, ["shell", "screencap", "-p", remote_path], timeout=20)
    adb_command(adb, device_id, ["pull", remote_path, str(local_path)], timeout=30)
    adb_command(adb, device_id, ["shell", "rm", remote_path], timeout=20)
    return {"path": str(local_path.relative_to(REPO_ROOT)).replace("\\", "/"), "description": name}


class ApiClient:
    def __init__(self, base_url: str, timeout: int = 20):
        self.base_url = base_url.rstrip("/")
        self.timeout = timeout

    def request(
        self,
        name: str,
        method: str,
        path: str,
        token: Optional[str] = None,
        body: Optional[Dict[str, Any]] = None,
        expected_code: int = 200,
    ) -> Dict[str, Any]:
        url = f"{self.base_url}{path}"
        headers = {"Content-Type": "application/json"}
        if token:
            headers["Authorization"] = f"Bearer {token}"
        data = None
        if body is not None:
            data = json.dumps(body, ensure_ascii=False).encode("utf-8")
        request = urllib.request.Request(url, data=data, headers=headers, method=method.upper())
        status: Optional[int] = None
        try:
            with urllib.request.urlopen(request, timeout=self.timeout) as response:
                status = response.status
                raw = response.read().decode("utf-8", errors="replace")
        except urllib.error.HTTPError as exc:
            status = exc.code
            raw = exc.read().decode("utf-8", errors="replace")
        except urllib.error.URLError as exc:
            raise ApiError(name, None, None, str(exc)) from exc

        try:
            payload = json.loads(raw) if raw else {}
        except json.JSONDecodeError as exc:
            raise ApiError(name, status, None, f"non-json response: {raw[:160]}") from exc

        code = payload.get("code")
        msg = payload.get("msg") or payload.get("message") or ""
        if status < 200 or status >= 300 or code != expected_code:
            raise ApiError(name, status, code, msg)
        return payload.get("data")


def append_event(events: List[Dict[str, Any]], name: str, status: str, message: str, data: Optional[Dict[str, Any]] = None) -> None:
    event: Dict[str, Any] = {
        "name": name,
        "status": status,
        "message": message,
        "time": now_iso(),
    }
    if data:
        event["data"] = data
    events.append(event)
    print(f"[{status}] {name} - {message}")


def require_status(name: str, detail: Dict[str, Any], expected: str) -> None:
    actual = detail.get("status")
    if actual != expected:
        raise MatrixError(f"{name}: expected status {expected}, got {actual}")


def main() -> int:
    parser = argparse.ArgumentParser(description="Android dual-end campus action matrix")
    parser.add_argument("--api-base", default="", help="API base ending with /api")
    parser.add_argument("--api-base-env-file", default=str(REPO_ROOT / "frontend" / ".env.android-user-public"))
    parser.add_argument("--adb-path", default="")
    parser.add_argument("--device-id", default="")
    parser.add_argument("--output-dir", default=str(REPO_ROOT / "project-logs" / "campus-relay" / "runtime" / "step-165-android-action-matrix"))
    parser.add_argument("--clear-app-data", action="store_true", help="Clear user/parttime app data before launch evidence screenshots")
    parser.add_argument("--launch-wait-seconds", type=int, default=5)
    parser.add_argument("--timeout-seconds", type=int, default=20)
    args = parser.parse_args()

    output_dir = Path(args.output_dir)
    if not output_dir.is_absolute():
        output_dir = REPO_ROOT / output_dir
    output_dir.mkdir(parents=True, exist_ok=True)
    report_path = output_dir / "android-action-matrix-report.json"

    events: List[Dict[str, Any]] = []
    screenshots: List[Dict[str, str]] = []
    summary: Dict[str, Any] = {"passed": False}

    try:
        api_base = read_api_base(args.api_base, Path(args.api_base_env_file))
        client = ApiClient(api_base, timeout=args.timeout_seconds)
        adb = resolve_adb(args.adb_path)
        device_id = resolve_device(adb, args.device_id)
        append_event(events, "adb device", "PASS", f"online device {device_id}")

        launch_app(adb, device_id, USER_PACKAGE, args.clear_app_data, args.launch_wait_seconds)
        screenshots.append(save_screenshot(adb, device_id, output_dir / "user-launch.png", "campus-user-action-matrix.png"))
        append_event(events, "user app launch", "PASS", "user app launched and screenshot captured")

        customer_login = client.request(
            "customer login",
            "POST",
            "/users/login",
            body={"phone": CUSTOMER_PHONE, "password": TEST_PASSWORD},
        )
        customer_token = customer_login.get("token") if isinstance(customer_login, dict) else None
        if not customer_token:
            raise MatrixError("customer login did not return token")
        append_event(events, "customer login", "PASS", "customer token acquired")

        pickup_points = client.request("pickup points", "GET", "/campus/public/pickup-points", token=customer_token)
        if not isinstance(pickup_points, list) or not pickup_points:
            raise MatrixError("pickup points list is empty")
        pickup_point = pickup_points[0]
        append_event(events, "pickup points", "PASS", "pickup points loaded", {"count": len(pickup_points), "firstPointId": pickup_point.get("id")})

        rules = client.request("delivery rules", "GET", "/campus/public/delivery-rules", token=customer_token)
        append_event(events, "delivery rules", "PASS", "delivery rules loaded", {"platformName": rules.get("platformName") if isinstance(rules, dict) else None})

        suffix = str(int(time.time() * 1000))[-10:]
        create_payload = {
            "pickupPointId": pickup_point.get("id"),
            "targetType": "LIBRARY",
            "deliveryBuilding": "图书馆",
            "deliveryDetail": "图书馆一楼门口",
            "contactName": "动作矩阵",
            "contactPhone": CUSTOMER_PHONE,
            "foodDescription": "Python动作链测试订单",
            "externalPlatformName": "ActionMatrix",
            "externalOrderRef": f"PYM-{suffix}",
            "pickupCode": "P165",
            "remark": "Step165 Python动作链自动化",
            "tipFee": 1,
            "urgentFlag": 0,
        }
        order_id = client.request("create order", "POST", "/campus/customer/orders", token=customer_token, body=create_payload)
        if not order_id:
            raise MatrixError("create order did not return order id")
        append_event(events, "create order", "PASS", f"created {order_id}")

        client.request("mock pay", "POST", f"/campus/customer/orders/{order_id}/mock-pay", token=customer_token)
        customer_detail = client.request("customer detail after pay", "GET", f"/campus/customer/orders/{order_id}", token=customer_token)
        require_status("customer detail after pay", customer_detail, "WAITING_ACCEPT")
        append_event(events, "mock pay", "PASS", "order paid and waiting for accept", {"orderId": order_id, "status": customer_detail.get("status"), "paymentStatus": customer_detail.get("paymentStatus")})

        launch_app(adb, device_id, PARTTIME_PACKAGE, args.clear_app_data, args.launch_wait_seconds)
        screenshots.append(save_screenshot(adb, device_id, output_dir / "parttime-launch.png", "campus-parttime-action-matrix.png"))
        append_event(events, "parttime app launch", "PASS", "parttime app launched and screenshot captured")

        courier_login = client.request(
            "parttime token",
            "POST",
            "/campus/courier/auth/token",
            body={"phone": COURIER_PHONE, "password": TEST_PASSWORD},
        )
        courier_token = courier_login.get("token") if isinstance(courier_login, dict) else None
        if not courier_token:
            raise MatrixError("parttime token login did not return token")
        append_event(events, "parttime login", "PASS", "courier token acquired")

        profile = client.request("parttime profile", "GET", "/campus/courier/profile", token=courier_token)
        review = client.request("parttime review", "GET", "/campus/courier/review-status", token=courier_token)
        append_event(events, "parttime profile", "PASS", "profile and review loaded", {
            "profileId": profile.get("id") if isinstance(profile, dict) else None,
            "reviewStatus": review.get("reviewStatus") if isinstance(review, dict) else None,
        })

        available = client.request("available orders", "GET", "/campus/courier/orders/available?page=1&pageSize=20", token=courier_token)
        records = available.get("records") if isinstance(available, dict) else []
        found = any(item.get("id") == order_id for item in records or [])
        append_event(events, "available orders", "PASS", "available orders loaded", {
            "total": available.get("total") if isinstance(available, dict) else None,
            "records": len(records or []),
            "createdOrderInFirstPage": found,
        })

        client.request("accept order", "POST", f"/campus/courier/orders/{order_id}/accept", token=courier_token)
        courier_detail = client.request("courier detail after accept", "GET", f"/campus/courier/orders/{order_id}", token=courier_token)
        require_status("courier detail after accept", courier_detail, "ACCEPTED")
        append_event(events, "accept order", "PASS", "order accepted", {"status": courier_detail.get("status")})

        client.request(
            "pickup order",
            "POST",
            f"/campus/courier/orders/{order_id}/pickup",
            token=courier_token,
            body={"pickupProofImageUrl": CONTROLLED_PROOF_URL, "courierRemark": "Python动作链确认取餐"},
        )
        courier_detail = client.request("courier detail after pickup", "GET", f"/campus/courier/orders/{order_id}", token=courier_token)
        require_status("courier detail after pickup", courier_detail, "PICKED_UP")
        append_event(events, "pickup order", "PASS", "order picked up", {"status": courier_detail.get("status"), "proof": CONTROLLED_PROOF_URL})

        client.request(
            "start delivering",
            "POST",
            f"/campus/courier/orders/{order_id}/deliver",
            token=courier_token,
            body={"courierRemark": "Python动作链开始配送"},
        )
        courier_detail = client.request("courier detail after start delivering", "GET", f"/campus/courier/orders/{order_id}", token=courier_token)
        require_status("courier detail after start delivering", courier_detail, "DELIVERING")
        append_event(events, "start delivering", "PASS", "order delivering", {"status": courier_detail.get("status")})

        client.request(
            "report exception",
            "POST",
            f"/campus/courier/orders/{order_id}/exception-report",
            token=courier_token,
            body={"exceptionType": "联系不上", "exceptionRemark": "Python动作链测试异常留痕"},
        )
        courier_detail = client.request("courier detail after exception", "GET", f"/campus/courier/orders/{order_id}", token=courier_token)
        if not courier_detail.get("exceptionType"):
            raise MatrixError("exception summary was not populated after report")
        append_event(events, "report exception", "PASS", "exception summary populated", {
            "exceptionType": courier_detail.get("exceptionType"),
            "exceptionRemark": courier_detail.get("exceptionRemark"),
        })

        client.request(
            "mark delivered",
            "POST",
            f"/campus/courier/orders/{order_id}/deliver",
            token=courier_token,
            body={"courierRemark": "Python动作链确认送达"},
        )
        courier_detail = client.request("courier detail after delivered", "GET", f"/campus/courier/orders/{order_id}", token=courier_token)
        require_status("courier detail after delivered", courier_detail, "AWAITING_CONFIRMATION")
        append_event(events, "mark delivered", "PASS", "order awaiting customer confirmation", {"status": courier_detail.get("status")})

        customer_detail = client.request("customer detail awaiting confirmation", "GET", f"/campus/customer/orders/{order_id}", token=customer_token)
        require_status("customer detail awaiting confirmation", customer_detail, "AWAITING_CONFIRMATION")
        append_event(events, "customer awaiting confirmation", "PASS", "customer can read awaiting confirmation", {"status": customer_detail.get("status")})

        client.request("customer confirm", "POST", f"/campus/customer/orders/{order_id}/confirm", token=customer_token)
        customer_detail = client.request("customer detail completed", "GET", f"/campus/customer/orders/{order_id}", token=customer_token)
        require_status("customer detail completed", customer_detail, "COMPLETED")
        courier_detail = client.request("courier detail completed", "GET", f"/campus/courier/orders/{order_id}", token=courier_token)
        require_status("courier detail completed", courier_detail, "COMPLETED")
        append_event(events, "customer confirm", "PASS", "order completed and readable by both sides", {
            "customerStatus": customer_detail.get("status"),
            "courierStatus": courier_detail.get("status"),
        })

        launch_app(adb, device_id, USER_PACKAGE, False, args.launch_wait_seconds)
        screenshots.append(save_screenshot(adb, device_id, output_dir / "user-after-action-chain.png", "campus-user-after-action-chain.png"))
        launch_app(adb, device_id, PARTTIME_PACKAGE, False, args.launch_wait_seconds)
        screenshots.append(save_screenshot(adb, device_id, output_dir / "parttime-after-action-chain.png", "campus-parttime-after-action-chain.png"))
        append_event(events, "post-chain screenshots", "PASS", "captured user and parttime app screenshots after action chain")

        summary = {
            "passed": True,
            "orderId": order_id,
            "finalCustomerStatus": customer_detail.get("status"),
            "finalCourierStatus": courier_detail.get("status"),
            "paymentStatus": customer_detail.get("paymentStatus"),
            "exceptionType": customer_detail.get("exceptionType"),
        }
    except Exception as exc:  # noqa: BLE001 - report all matrix failures.
        append_event(events, "matrix failure", "FAIL", str(exc))
        summary = {"passed": False, "error": str(exc)}
    finally:
        report = {
            "name": "Android dual-end action matrix",
            "date": now_iso(),
            "apiBase": redact_url(locals().get("api_base", "")),
            "deviceId": locals().get("device_id", args.device_id or ""),
            "packages": {"user": USER_PACKAGE, "parttime": PARTTIME_PACKAGE},
            "summary": summary,
            "events": events,
            "screenshots": screenshots,
            "notes": [
                "The report redacts the real API host and never stores tokens.",
                "ADB is used for real-device launch/screenshot evidence.",
                "Business state transitions are driven through public APIs with test accounts.",
                "The script does not modify bridge behavior, token attachment logic, routes, backend state machine, or old takeaway modules.",
            ],
        }
        report_path.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
        print(f"Report: {report_path}")

    return 0 if summary.get("passed") else 1


if __name__ == "__main__":
    sys.exit(main())
