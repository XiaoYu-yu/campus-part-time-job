# Step 167 - 公开公测前安全与发布缺口收口

## 本轮目标

参考外部 AI 的发布缺口评估，用当前仓库真实文件复核并优先关闭“不依赖真实域名、证书、密钥”的发布安全缺口。

本轮重点：

1. release 签名配置入口。
2. Android release cleartext 收口。
3. 公开公测前剩余缺口清单。

## 真实复核结论

外部评估中的核心方向基本正确：

- release 签名入口此前缺失。
- Android Manifest 此前允许 `usesCleartextTraffic=true`。
- 内测部署仍是 HTTP / 裸 IP 场景，尚无 HTTPS / 域名 / 证书。
- App 内隐私说明、用户协议和反馈入口仍未真正落地。

## 本轮已完成

### 1. Release 签名入口

用户端和兼职端均新增本地签名配置读取逻辑：

- `mobile/user-app/android/app/build.gradle`
- `mobile/parttime-app/android/app/build.gradle`

行为：

- 如果 `android/key.properties` 存在，则 release buildType 绑定 `signingConfigs.release`。
- 如果不存在，则 release 构建继续可执行但输出 unsigned release，并给出明确 warning。
- Debug 构建不受影响。

新增示例文件：

- `mobile/user-app/android/key.properties.example`
- `mobile/parttime-app/android/key.properties.example`

`.gitignore` 已保护：

- `mobile/**/android/key.properties`
- `mobile/**/android/keystore/`
- `mobile/**/android/*.jks`
- `mobile/**/android/*.keystore`

### 2. Android cleartext release 收口

Manifest 改动：

- `mobile/user-app/android/app/src/main/AndroidManifest.xml`
- `mobile/parttime-app/android/app/src/main/AndroidManifest.xml`

实现：

- `android:usesCleartextTraffic="${usesCleartextTraffic}"`
- `android:networkSecurityConfig="@xml/network_security_config"`
- `tools:replace="android:usesCleartextTraffic"` 覆盖 Capacitor 插件中默认的 cleartext 设置。

新增网络安全配置：

- 用户端 debug：`mobile/user-app/android/app/src/debug/res/xml/network_security_config.xml`
- 用户端 release：`mobile/user-app/android/app/src/release/res/xml/network_security_config.xml`
- 兼职端 debug：`mobile/parttime-app/android/app/src/debug/res/xml/network_security_config.xml`
- 兼职端 release：`mobile/parttime-app/android/app/src/release/res/xml/network_security_config.xml`

结果：

- Debug：`cleartextTrafficPermitted=true`
- Release：`cleartextTrafficPermitted=false`

### 3. 发布缺口清单

新增：

- `docs/deployment/public-beta-release-gap-closure.md`

该文档记录：

- 已收口项。
- 仍未收口项。
- HTTPS / 域名 / 证书下一步。
- 隐私说明与用户协议缺口。
- App 内反馈入口缺口。
- 当前禁止项。

## 验证结果

### Android 构建

用户端：

```powershell
cd mobile\user-app\android
.\gradlew.bat :app:assembleDebug :app:assembleRelease
```

结果：通过。

兼职端：

```powershell
cd mobile\parttime-app\android
.\gradlew.bat :app:assembleDebug :app:assembleRelease
```

结果：通过。

### Manifest 核验

用户端：

- debug 合并 manifest：`usesCleartextTraffic="true"`
- release 合并 manifest：`usesCleartextTraffic="false"`

兼职端：

- debug 合并 manifest：`usesCleartextTraffic="true"`
- release 合并 manifest：`usesCleartextTraffic="false"`

## 本轮没有做什么

- 没有生成真实 release keystore。
- 没有提交任何真实签名文件或密码。
- 没有配置真实域名。
- 没有申请或提交 HTTPS 证书。
- 没有改后端接口。
- 没有改前端业务页面。
- 没有新增反馈表或反馈接口。
- 没有新增隐私协议页面。
- 没有重开 bridge。
- 没有删除旧外卖兼容模块。
- 没有提交公网 IP、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容。

## 当前仍未解决的问题

1. 公开公测前仍缺真实 release keystore 和本地 `key.properties`。
2. 公开公测前仍缺 HTTPS / 域名 / 证书。
3. 公开公测前仍缺 App 内隐私政策和用户协议入口。
4. 公开公测前仍缺 App 内反馈入口或外部表单跳转。
5. 当前 release cleartext 已默认关闭，但没有 HTTPS API base 时不能把 release 包发给公开用户。

## 当前结论

本轮已把 Android 端发布安全基线往前推进了一步：

- release 签名有了安全配置入口。
- release 网络明文默认关闭。
- debug 内测链路没有被打断。

但当前仍只能算 owner-controlled 内测准备态，不应公开公测。

## 当前 bridge 结论

bridge 继续保持 `Phase A no-op` 冻结态。本轮未触发恢复推进条件。

## 下一轮建议

Step 168 建议二选一：

1. 如果准备继续冲公开公测：先处理 HTTPS / 域名 / 证书，并在本地生成真实 release keystore。
2. 如果仍先做小范围内测：优先补 App 内反馈入口和隐私 / 用户协议静态页。
