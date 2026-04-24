# Android 壳工程

当前移动端策略是：一份 `frontend/` 业务源码，两个 Capacitor Android 壳。

## 壳工程

1. `mobile/user-app`
   - App 名称：校内兼职
   - 包名：`com.xiaoyu.campus.user`
   - 前端构建产物：`frontend/dist-android-user`
   - 默认入口：`/user/login`
2. `mobile/parttime-app`
   - App 名称：校内兼职端
   - 包名：`com.xiaoyu.campus.parttime`
   - 前端构建产物：`frontend/dist-android-parttime`
   - 默认入口：`/parttime/login`

## 常用命令

### 前端产物与 Capacitor 同步

先在 `frontend/` 生成对应移动端构建产物：

```bash
npm run build:android:user
npm run build:android:parttime
```

用户端壳：

```bash
cd mobile/user-app
npm install
npm run cap:sync
```

兼职端壳：

```bash
cd mobile/parttime-app
npm install
npm run cap:sync
```

### Debug APK 构建

Android 本地构建需要：

1. JDK 21。
2. Android SDK `platform-tools`、`platforms;android-36`、`build-tools;36.0.0`。
3. 每个壳工程自己的 `android/local.properties` 指向本机 SDK，例如：

```properties
sdk.dir=C:/Users/20278/AppData/Local/Android/Sdk
```

`local.properties`、`android/app/build/`、`android/app/src/main/assets/` 和 `android/app/src/main/res/xml/config.xml` 都是本地生成内容，不提交到仓库。

用户端 APK：

```bash
cd mobile/user-app
npm run cap:sync
cd android
./gradlew assembleDebug --no-daemon
```

兼职端 APK：

```bash
cd mobile/parttime-app
npm run cap:sync
cd android
./gradlew assembleDebug --no-daemon
```

Debug APK 输出位置：

1. `mobile/user-app/android/app/build/outputs/apk/debug/app-debug.apk`
2. `mobile/parttime-app/android/app/build/outputs/apk/debug/app-debug.apk`

当前 Android 构建已优先使用国内镜像：

1. Gradle wrapper 分发包走腾讯 Gradle 镜像。
2. Android Gradle 依赖优先走阿里云 Maven 镜像，官方 `google()` / `mavenCentral()` 保留为兜底。

## 边界

1. Android 壳只负责容器、包名、App 名称和原生工程。
2. 不复制第二套前端源码。
3. 不把 admin 后台打进移动端默认入口。
4. 不因 Android 壳改 bridge、鉴权、token 附着逻辑或后端接口。
