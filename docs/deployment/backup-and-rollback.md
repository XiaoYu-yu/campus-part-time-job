# 备份与回滚说明

## 数据库备份

上线或高风险变更前必须先做全量备份。

```powershell
mysqldump -h %DB_HOST% -P %DB_PORT% -u %DB_USERNAME% -p %DB_NAME% > backup_full.sql
```

建议记录：

1. 备份时间。
2. 对应 Git commit。
3. 当前执行到的 migration 版本。
4. 是否包含演示样本数据。

## 应用回滚

### 后端

1. 保留上一个稳定版本 jar。
2. 如果新版本异常，停止当前进程。
3. 启动上一个稳定 jar。
4. 验证登录、customer 下单、courier workbench、admin 只读运营页。

### 前端

1. 保留上一个稳定版 `dist/`。
2. 如果新版本异常，回滚静态资源。
3. 清理浏览器缓存或 CDN 缓存。

## 数据库回滚

1. 优先执行明确的回滚 SQL。
2. 没有安全增量回滚脚本时，恢复上线前全量备份。
3. 对删除字段、修改字段类型等不可逆变更，必须先走兼容字段或双写策略。

## 当前特殊注意

1. bridge 当前处于 `Phase A no-op` 冻结态，回滚时不得删除 `/api/campus/courier/profile` 或 `/api/campus/courier/review-status`。
2. settlement 当前仍是模拟打款/运营记录，不应按真实财务系统做自动撤回。
3. 真实支付、地图、消息推送等外部服务尚未接入，不存在对应生产凭据回滚流程。
