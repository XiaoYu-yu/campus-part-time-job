# Step 01 修复后端构建

## 做了什么

- 检查并修改 [backend/pom.xml](D:/20278/code/show_shop1/backend/pom.xml)
- 将 Lombok 从不可用的 `edge-SNAPSHOT` 改为 Maven Central 可用正式版 `1.18.44`
- 为 `maven-compiler-plugin` 增加 Lombok 注解处理器配置

## 为什么这么改

- `edge-SNAPSHOT` 无法稳定从 Maven Central 拉取，直接导致 `mvn test` / `compile` 失败
- 显式配置 annotation processor 可以避免不同 JDK 环境下的编译器行为不一致

## 验证结果

- `..\apache-maven-3.9.14\bin\mvn.cmd test` 通过
- `..\apache-maven-3.9.14\bin\mvn.cmd -DskipTests compile` 通过

## 修改文件

- [backend/pom.xml](D:/20278/code/show_shop1/backend/pom.xml)
