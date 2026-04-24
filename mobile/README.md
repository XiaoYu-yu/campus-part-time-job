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

## 边界

1. Android 壳只负责容器、包名、App 名称和原生工程。
2. 不复制第二套前端源码。
3. 不把 admin 后台打进移动端默认入口。
4. 不因 Android 壳改 bridge、鉴权、token 附着逻辑或后端接口。
