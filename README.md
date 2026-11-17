# ONCLOUD 文档管理系统（后端）

基于 Spring Boot + Shiro + MyBatis-Plus + Redis 的文档管理系统，前后端分离，前端仓库见
[oncloud-vue](https://github.com/NaturePorter/oncloud-vue)。

## 技术栈

| 组件 | 版本 |
| --- | --- |
| Spring Boot | 2.7.18 |
| JDK | 17 |
| Shiro | 2.1.0 |
| MyBatis-Plus | 3.5.3.2 |
| Druid | 1.2.20 |
| Redis | Spring Data Redis |
| JSON | fastjson2 2.0.66 |
| 接口文档 | springdoc-openapi 1.6.15 |

## 快速开始

### 1. 准备环境

- JDK 17、Maven 3.6+
- MySQL 5.7 / 8.0
- Redis 5+

### 2. 初始化数据库

```bash
mysql -uroot -p < db/init.sql
```

脚本会创建 `dataoncloud` 库、全部表结构以及菜单/角色/用户数据。
默认账号：`admin / admin`（超级管理员），其余测试账号见脚本注释。

> 已有旧数据库时不要执行 `db/init.sql`（会删表），请参考脚本末尾的 `oc_role`、
> `oc_role_menu`、`oc_user_role` 三张表数据自行补齐，否则非超级管理员登录后看不到菜单。

### 3. 配置连接信息

默认连接本机 MySQL / Redis，可用环境变量覆盖，无需改代码：

| 环境变量 | 默认值 |
| --- | --- |
| `SERVER_PORT` | 8797 |
| `MYSQL_HOST` / `MYSQL_PORT` | localhost / 3306 |
| `MYSQL_DATABASE` | dataoncloud |
| `MYSQL_USERNAME` / `MYSQL_PASSWORD` | root / root |
| `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD` | localhost / 6379 / 空 |
| `ONCLOUD_UPLOAD_DIR` | ./FileLibrary |
| `ONCLOUD_JWT_SECRET` | 内置开发密钥（生产环境务必覆盖） |

### 4. 启动

```bash
mvn spring-boot:run
# 或
mvn clean package -DskipTests
java -jar target/oncloud-boot-1.0.0.jar
```

- 服务地址：<http://localhost:8797>
- 接口文档：<http://localhost:8797/swagger-ui.html>

## 接口一览

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| POST | `/api/login` | 登录，返回 token | 匿名 |
| POST | `/api/logout` | 退出登录 | 登录 |
| GET | `/api/user` | 当前用户信息 | 登录 |
| GET | `/api/menu` | 当前用户菜单 | 登录 |
| GET | `/api/doclist` | 我的文档 | 登录 |
| POST | `/api/docupload` | 上传文档 | 登录 |
| PUT | `/api/doclist/{id}/shared/{0\|1}` | 修改共享状态 | 所有者/管理员 |
| GET | `/api/download?id=` | 下载文档 | 所有者/管理员/已共享且已审核 |
| DELETE | `/api/deleteDocById/{id}` | 删除文档 | 所有者/管理员 |
| GET | `/api/halldoclist` | 文件大厅 | 登录 |
| GET | `/api/auditdoclist` | 待审核列表 | 管理员 |
| PUT | `/api/auditdoclist/{id}/audit/{0\|1}` | 审核文档 | 管理员 |
| GET | `/api/userlist` | 用户列表 | 管理员 |
| POST | `/api/adduser` | 新增用户 | 管理员 |
| PUT | `/api/userlist/{id}/state/{0\|1}` | 启用/停用 | 管理员 |
| DELETE | `/api/deleteUserByid/{id}` | 删除用户 | 管理员 |
| PUT | `/api/edituserrole/{id}?role=` | 修改角色 | 超级管理员 |

除登录接口外，请求头需携带 `token`（下载接口也支持 `?token=`，便于浏览器直接下载）。

## 本次修复与优化

### 构建与依赖

- Spring Boot 2.2.6 升级到 2.7.18，解决 JDK 17 下 `sun.misc.BASE64Encoder` 无法编译的问题。
- Shiro 1.7.1 → 1.13.0 → 2.1.0（修复 CVE-2023-22602、CVE-2026-23903，并开启 `caseInsensitive` 匹配）。
- fastjson 1.2.83 → fastjson2 2.0.66（1.x 存在反序列化 RCE 且官方不再修复）。
- 移除未使用的 `commons-io`（CVE-2024-47554）。
- MyBatis-Plus 3.3.1 → 3.5.3.2，旧版 `PaginationInterceptor` 换成 `MybatisPlusInterceptor`。
- jjwt 0.9.0 → 0.11.5，移除弱密钥与已废弃 API。
- 移除与 Spring Boot 2.2 绑定的 springfox 2.9.2，改用 springdoc-openapi。
- 补齐 `commons-pool2`、`validation` 等隐式依赖。

### 安全

- 数据库、Redis、JWT 密钥全部改为环境变量/占位符，仓库不再包含服务器地址和密码。
- 所有 Mapper 中的 `${}` 改为 `#{}`，消除 SQL 注入；修正 `queryAllMenuId` 写死 `user_id = 1` 的 Bug。
- 文件下载增加越权校验：仅所有者、管理员或“已共享且已审核”的文档可下载。
- 用户管理、审核接口增加 `@RequiresRoles`，普通用户不能再调用管理接口。
- 新增用户及历史用户密码使用 BCrypt 存储，明文密码在首次登录成功后自动升级。
- 登录时校验账号停用状态；登录态只保存脱敏后的用户信息。
- 修复 CORS 中 `allowedOrigins("*")` 与 `allowCredentials(true)` 冲突导致浏览器报错的问题。
- 操作日志不再打印登录密码等敏感参数；XSS 过滤器不再破坏密码中的特殊字符。

### 业务与健壮性

- 修复审核接口误调用 `updateUserStateById`（更新到用户表）的严重 Bug。
- 上传文件使用 UUID 重命名 + 扩展名校验 + 路径穿越校验，保存目录可配置，不再依赖 Windows 路径分隔符。
- 下载文件名使用 RFC 5987 编码，中文文件名不再乱码。
- 修复删除用户/文档、查询用户等接口在记录不存在时的空指针。
- 统一异常处理：区分 401/403/400/405/500，未登录与无权限不再返回 500。
- 新增 `db/init.sql`，补齐角色、用户角色、角色菜单数据，使“不同角色不同导航”真正可用。
- 新增基于 BCrypt/JWT 的单元测试，`mvn test` 无需数据库即可通过。

## 目录结构

```
src/main/java/cn/oncloud
├── aspect          操作日志切面
├── common          Shiro/CORS/MyBatis-Plus 配置与工具类
├── controller      用户、文档、菜单接口
├── dto             统一响应结构
├── entity          实体类
├── exception       全局异常处理
├── filter          XSS 与 OAuth2(Shiro) 过滤器
├── mapper          MyBatis-Plus Mapper
└── service         业务层
db/init.sql         数据库初始化脚本
```
