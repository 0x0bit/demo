使用 springboot + mybatis-plus 实现员工简单的增删查改
项目目录结构：
如果有复杂的业务场景，可以参照如下项目结构进行整理编写代码
```
|—java
| |————com.xxxx.xxxx
| |————aop---------------
| |----common------------
| |----config------------
| |----consumer----------

| |----database----------数据持久层
| |----|----dao--------------- 对应resource 中的 sqlMapper文件中定义的 SQL 语句
| |----|----entity------------ 实体类

| |----service-----------
| |----|----form------------- 表单入参参数限定
| |----|----DTO------------ 需求字段组装
| |----|----VO------------- 定义返回值参数类
| |----|----xxxService.java------------- 定义 service 接口
| |----|----impl——---------------------- 对 service 定义的接口进行具体的业务逻辑实现
| |----|----info------------------------ 一些返回信息的定义

| |----web.v1 ------------  controller，接收请求和返回

| |----Application.java--- SpringBoot 启动文件
```

建表语句
```sql
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(30) DEFAULT NULL COMMENT '姓名',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

