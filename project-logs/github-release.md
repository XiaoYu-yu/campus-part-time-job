# GitHub 公开仓库整理日志

## 目标

将项目整理为可以直接发布到 GitHub 的公开仓库版本，并补齐标准项目材料。

## 本轮补齐内容

- 新增 `MIT` 许可证
- 新增 `CHANGELOG.md`
- 新增 `CONTRIBUTING.md`
- 新增 `SECURITY.md`
- 新增 GitHub Issue 模板
- 新增 GitHub Pull Request 模板
- 更新根 `README.md`，加入公开仓库入口信息
- 更新 `docs/README.md`，纳入开源仓库材料索引
- 生成后端 `Maven Wrapper`
- 将 `apache-maven-3.9.14` 调整为仅本机辅助目录，不再纳入 GitHub 公开仓库

## 关键文件

- [LICENSE](D:/20278/code/show_shop1/LICENSE)
- [CHANGELOG.md](D:/20278/code/show_shop1/CHANGELOG.md)
- [CONTRIBUTING.md](D:/20278/code/show_shop1/CONTRIBUTING.md)
- [SECURITY.md](D:/20278/code/show_shop1/SECURITY.md)
- [.github/pull_request_template.md](D:/20278/code/show_shop1/.github/pull_request_template.md)
- [.github/ISSUE_TEMPLATE/bug_report.yml](D:/20278/code/show_shop1/.github/ISSUE_TEMPLATE/bug_report.yml)
- [.github/ISSUE_TEMPLATE/feature_request.yml](D:/20278/code/show_shop1/.github/ISSUE_TEMPLATE/feature_request.yml)

## 采用的默认决定

- 许可证采用 `MIT`
- 仓库按公开训练项目维护，不引入企业化流程材料
- 保留现有中文主文档，不强制改成全英文仓库

## 当前结论

仓库材料已经具备标准公开项目的基础形态。

## 发布状态

- 本地 Git 仓库已初始化
- 当前分支：`main`
- 本地提交已完成：
  - `8ce2858 feat: publish show_shop1 training project`
- 远程仓库已绑定：
  - `https://github.com/XiaoYu-yu/show_shop1.git`
- 当前环境未完成 `push`，原因是到 `github.com:443` 的 TCP 连接失败，不是仓库内容问题
