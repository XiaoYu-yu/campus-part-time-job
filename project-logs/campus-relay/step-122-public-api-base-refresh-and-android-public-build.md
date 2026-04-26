# Step 122 - 公网 API base 切换与 Android public APK 复核

## 本轮目标

1. 基于 owner 提供的新公网地址，更新本地 ignored public env。
2. 重新执行 public API base 只读 smoke。
3. 重新同步并构建用户端 / 兼职端 Android public APK。
4. 继续不提交真实公网 IP，不改 bridge、不改后端接口、不改订单状态机。

## 实际完成

### 1. public API base smoke 已通过

执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-public-api-smoke.ps1 -ApiBase <redacted> -AllowFailures
```

结果：

1. `GET /api/campus/public/pickup-points` 通过。
2. `GET /api/campus/public/delivery-rules` 通过。
3. `passed=2 failed=0`。
4. 报告已写入：
   - `project-logs/campus-relay/runtime/step-121-public-api-base/public-api-smoke.json`

说明：

1. 真实公网 IP 只写在本地 ignored env 文件。
2. 仓库内报告继续使用 `http://<redacted>/api`。
3. 这证明当前公网 API base 已具备 Android 用户端代送入口的只读依赖。

### 2. 本地 ignored public env 已切换

本地文件：

1. `frontend/.env.android-user-public`
2. `frontend/.env.android-parttime-public`

当前已写入新的 public API base。

说明：

1. 这两个文件被 `.gitignore` 忽略。
2. 不提交真实 IP。
3. 不影响默认模拟器构建。

### 3. Android public 壳重新同步

执行：

1. `mobile/user-app npm run cap:sync:public`
2. `mobile/parttime-app npm run cap:sync:public`

结果：

1. 用户端 public 壳同步通过。
2. 兼职端 public 壳同步通过。
3. 结构化脱敏证据：
   - `project-logs/campus-relay/runtime/step-122-public-api-base-refresh/android-public-refresh-evidence.json`

证据结论：

1. 用户端 public 壳中未发现 `10.0.2.2`。
2. 兼职端 public 壳中未发现 `10.0.2.2`。
3. 两个壳均已嵌入新的 public API base。

### 4. Android Debug APK 重新构建

执行：

1. `mobile/user-app/android .\gradlew.bat assembleDebug --no-daemon`
2. `mobile/parttime-app/android .\gradlew.bat assembleDebug --no-daemon`

结果：

1. 用户端 Debug APK 构建通过。
2. 兼职端 Debug APK 构建通过。

### 5. Android clean launch smoke

执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -StartEmulator -ClearData -LaunchWaitSeconds 10
```

结果：

1. 用户端安装、清数据、启动成功。
2. 兼职端安装、清数据、启动成功。
3. 用户端截图：
   - `project-logs/campus-relay/runtime/android-smoke/20260426-143330-user-app-launch.png`
4. 兼职端截图：
   - `project-logs/campus-relay/runtime/android-smoke/20260426-143341-parttime-app-launch.png`

结论：

1. 用户端仍进入“用户端登录”。
2. 兼职端仍进入“兼职端入口”。
3. public mode 路由修正仍有效。

## 验证结果

1. `scripts/trial-operation/android-public-api-smoke.ps1`
   - 通过，`passed=2 failed=0`。
2. `scripts/trial-operation/android-api-base-check.ps1`
   - hard failure 为 `0`。
   - warning：LAN env 未配置、公网仍为 HTTP。
3. `mobile/user-app npm run cap:sync:public`
   - 通过。
4. `mobile/parttime-app npm run cap:sync:public`
   - 通过。
5. 用户端 `assembleDebug`
   - 通过。
6. 兼职端 `assembleDebug`
   - 通过。
7. Android clean launch smoke
   - 通过。

## 明确未改

1. 未改 bridge。
2. 未改 `frontend/src/utils/request.js`。
3. 未改 token 附着逻辑。
4. 未改后端接口、鉴权或订单状态机。
5. 未改旧外卖模块。
6. 未新增 Android 原生页面。
7. 未接真实支付、真实退款或真实打款。
8. 未提交真实公网 IP。

## 当前结论

1. 新公网 API base 已通过 public 只读依赖 smoke。
2. 两个 Android public APK 已按新 API base 重新同步并构建成功。
3. 清数据启动截图确认两个 APK 仍进入正确移动入口。
4. 当前已从“公网 API 404 阻塞”推进到“public API base 可用，下一步可做 Android WebView 登录 / 代送真实接口 smoke”。
5. bridge 继续保持 `Phase A no-op` 冻结态。

## 遗留问题

1. 当前 public API 仍使用 HTTP，正式对外内测应切 HTTPS / 域名 / 反向代理证书。
2. 本轮只验证 public 只读依赖和 APK 启动，尚未在 WebView 内完成登录、代送列表、创建订单和 mock-pay。
3. LAN 真机 smoke 仍未执行。

## 下一步建议

1. Step 123 优先做 Android public WebView 真实接口 smoke：
   - 用户端登录。
   - 进入 `/user/campus/orders`。
   - 读取取餐点 / 配送规则 / 我的代送单。
   - 创建代送单。
   - mock-pay。
2. 若用户端 public WebView smoke 通过，再验证兼职端 public 登录、workbench 加载和可接单列表。
3. 暂不继续扩 Android UI、bridge、admin 页面或真实支付能力。
