# Step 02 统一订单表名

## 做了什么

- 将数据库订单主表统一为 `orders`
- 修复初始化脚本、外键引用和 Java 侧注释表述

## 为什么这么改

- 原先数据库脚本和 Mapper 对订单表名使用不一致，运行时容易出现 SQL 查询失败或联调偏差

## 验证结果

- 初始化脚本主表名已为 `orders`
- 订单统计和订单查询 SQL 已不再引用旧表名

## 修改文件

- [backend/db/init.sql](D:/20278/code/show_shop1/backend/db/init.sql)
- [backend/src/main/java/com/cangqiong/takeaway/entity/Order.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/entity/Order.java)
