# 网址导航系统

一个基于 Spring Boot + SQLite + Bootstrap 的现代化网址导航系统，具备完整的后台管理功能。

## 📋 项目简介

这是一个全栈网址导航系统，采用前后端架构，提供美观的卡片式导航界面和功能强大的后台管理系统。

## ✨ 主要特性

### 前端功能
- 🎨 **美观的卡片式导航**：采用渐变背景和现代化UI设计
- 🔍 **聚合搜索**：支持 Google、百度、Bing、GitHub、站内搜索
- 📱 **响应式设计**：完美适配桌面端和移动端
- 🔗 **友情链接展示**：支持友情链接的展示和管理
- 📢 **悬浮广告位**：支持左右两侧悬浮广告位展示，保留关闭广告按钮

### 后台管理功能
- 👤 **用户管理**：管理员登录、用户信息管理、密码修改
- 📋 **栏目管理**：主菜单和子菜单的增删改查，支持树形结构
- 🃏 **卡片管理**：导航卡片的增删改查，支持通过网址自动解析网站名称、logo、描述
- 📢 **广告管理**：广告位的增删改查，支持启用/禁用状态
- 🔗 **友链管理**：友情链接的增删改查
- 📊 **数据统计**：登录时间、IP等统计信息展示
- 💾 **备份恢复**：一键备份数据为ZIP压缩包，支持上传ZIP恢复数据，自动保留最多5个备份文件

### 技术特性
- 🔐 **JWT认证**：安全的用户认证机制
- 🗄️ **SQLite数据库**：轻量级数据库，无需额外配置
- 📤 **文件上传**：支持图片上传功能
- 🔍 **搜索功能**：支持站内搜索和外部搜索引擎
- 📱 **移动端适配**：完美的移动端体验

## 🛠 技术栈

- **后端**：Spring Boot 2.6.13 + JDK 1.8
- **数据库**：SQLite
- **ORM**：Spring Data JPA + Hibernate
- **认证**：JWT (JSON Web Token)
- **前端**：HTML5 + Bootstrap 4 + jQuery
- **构建工具**：Maven
- **其他**：Lombok、Jsoup (网站信息解析)

## 📦 环境要求

- JDK 1.8 或更高版本
- Maven 3.x
- 无需安装数据库（使用 SQLite）

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd yidianav
```

### 2. 配置环境变量（可选）

可以通过环境变量自定义配置：

```bash
export PORT=8888                    # 服务器端口号（默认: 8888）
export ADMIN_USERNAME=admin         # 管理员用户名（默认: admin）
export ADMIN_PASSWORD=123456        # 管理员密码（默认: 123456）
```

### 3. 构建项目

```bash
mvn clean package
```

### 4. 运行应用

```bash
java -jar target/yidianav-0.0.1-SNAPSHOT.jar
```

或者使用 Maven 直接运行：

```bash
mvn spring-boot:run
```

### 5. 访问应用

应用启动后，访问以下地址：

- **前端首页**：http://localhost:8888
- **管理后台**：http://localhost:8888/admin
- **登录页面**：http://localhost:8888/admin-login.html

**默认管理员账号**：
- 用户名：admin
- 密码：123456

## 📖 使用说明

### 前台使用

1. **浏览导航**：首页展示所有分类和导航卡片，点击卡片即可访问对应网站
2. **搜索功能**：
   - 在搜索框输入关键词
   - 选择搜索引擎（Google、百度、Bing、GitHub、站内）
   - 点击搜索按钮或按回车键
3. **友情链接**：页面底部展示所有友情链接
4. **广告位**：左右两侧显示悬浮广告，可点击关闭按钮隐藏

### 后台管理

#### 1. 登录后台

访问 http://localhost:8888/admin，如未登录会自动跳转到登录页面。

#### 2. 仪表盘

登录后进入仪表盘，显示：
- 栏目数量统计
- 导航卡片数量统计
- 友情链接数量统计
- 最近登录记录（最多显示10条）

#### 3. 栏目管理

- **新增栏目**：点击"新增栏目"按钮，填写栏目名称、图标（FontAwesome类名）、排序
- **编辑栏目**：点击编辑按钮修改栏目信息
- **删除栏目**：点击删除按钮删除栏目（注意：删除栏目前请先删除该栏目下的所有卡片）

**图标说明**：使用 FontAwesome 5.15.4 图标类名，例如：
- `fas fa-star` - 星星图标
- `fas fa-code` - 代码图标
- `fas fa-flask` - 烧杯图标

#### 4. 卡片管理

- **新增卡片**：
  1. 点击"新增卡片"按钮
  2. 输入网站URL
  3. 点击"解析"按钮自动获取网站名称、描述和Logo（可选）
  4. 选择所属栏目
  5. 设置排序号
  6. 点击保存
- **编辑卡片**：点击编辑按钮修改卡片信息
- **删除卡片**：点击删除按钮删除卡片

**自动解析功能**：系统会自动访问目标网站，获取网站标题、描述和图标信息。

#### 5. 广告管理

- **新增广告**：
  1. 填写广告标题
  2. 输入图片URL
  3. 输入点击跳转链接
  4. 选择位置（左侧/右侧）
  5. 设置启用状态
  6. 设置排序号
- **编辑广告**：修改广告信息
- **删除广告**：删除广告
- **启用/禁用**：控制广告是否在前台显示

#### 6. 友链管理

- **新增友链**：填写名称、链接、Logo、描述、排序
- **编辑友链**：修改友链信息
- **删除友链**：删除友链

#### 7. 备份恢复

- **创建备份**：
  1. 点击"立即备份"按钮
  2. 系统自动将数据库和上传文件打包为ZIP
  3. 备份文件保存在 `backups` 目录
  4. 系统自动保留最近5个备份文件
  
- **恢复备份**：
  1. 选择备份ZIP文件
  2. 点击"恢复数据"按钮
  3. 确认后系统将解压并恢复数据
  
- **下载备份**：点击备份历史中的"下载"按钮下载备份文件

#### 8. 个人设置

- **修改密码**：
  1. 输入新密码（至少6位）
  2. 确认新密码
  3. 点击"修改密码"按钮

## 📁 项目结构

```
yidianav/
├── src/
│   ├── main/
│   │   ├── java/jbc/com/cn/yidianav/
│   │   │   ├── config/              # 配置类
│   │   │   │   ├── DataInitializer.java      # 数据初始化
│   │   │   │   ├── SQLiteDialect.java        # SQLite方言
│   │   │   │   └── WebConfig.java            # Web配置
│   │   │   ├── controller/          # 控制器
│   │   │   │   ├── AdminAdvertisementController.java
│   │   │   │   ├── AdminBackupController.java
│   │   │   │   ├── AdminCardController.java
│   │   │   │   ├── AdminCategoryController.java
│   │   │   │   ├── AdminFriendLinkController.java
│   │   │   │   ├── AdminUserController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── FileUploadController.java
│   │   │   │   ├── PublicApiController.java
│   │   │   │   └── ViewController.java
│   │   │   ├── dto/                 # 数据传输对象
│   │   │   │   ├── ApiResponse.java
│   │   │   │   ├── CategoryDTO.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── LoginResponse.java
│   │   │   ├── entity/              # 实体类
│   │   │   │   ├── Advertisement.java
│   │   │   │   ├── Card.java
│   │   │   │   ├── Category.java
│   │   │   │   ├── FriendLink.java
│   │   │   │   ├── LoginHistory.java
│   │   │   │   └── User.java
│   │   │   ├── filter/              # 过滤器
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   ├── repository/          # 数据访问层
│   │   │   │   ├── AdvertisementRepository.java
│   │   │   │   ├── CardRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── FriendLinkRepository.java
│   │   │   │   ├── LoginHistoryRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/             # 业务逻辑层
│   │   │   │   ├── AdvertisementService.java
│   │   │   │   ├── BackupService.java
│   │   │   │   ├── CardService.java
│   │   │   │   ├── CategoryService.java
│   │   │   │   ├── FileUploadService.java
│   │   │   │   ├── FriendLinkService.java
│   │   │   │   └── UserService.java
│   │   │   ├── util/                # 工具类
│   │   │   │   └── JwtUtil.java
│   │   │   └── YidianavApplication.java  # 启动类
│   │   └── resources/
│   │       ├── static/              # 静态资源
│   │       │   ├── admin.html       # 管理后台页面
│   │       │   ├── admin.js         # 管理后台JS
│   │       │   ├── admin-login.html # 登录页面
│   │       │   ├── index.html       # 前台首页
│   │       │   └── web_tool-master/ # 原始静态导航（保留）
│   │       └── application.properties  # 配置文件
│   └── test/                        # 测试代码
├── backups/                         # 备份文件目录（自动创建）
├── uploads/                         # 上传文件目录（自动创建）
├── navigation.db                    # SQLite数据库文件（自动创建）
├── pom.xml                          # Maven配置
└── README.md                        # 项目说明
```

## 🔌 API 接口

### 公开接口

- `GET /api/public/categories` - 获取栏目树形结构
- `GET /api/public/cards` - 获取所有卡片
- `GET /api/public/cards/category/{id}` - 获取指定栏目的卡片
- `GET /api/public/cards/search?keyword=xxx` - 搜索卡片
- `GET /api/public/ads` - 获取所有启用的广告
- `GET /api/public/ads/{position}` - 获取指定位置的广告
- `GET /api/public/friendlinks` - 获取所有友链

### 认证接口

- `POST /api/auth/login` - 管理员登录

### 管理接口（需要JWT认证）

#### 用户管理
- `GET /api/admin/users/me` - 获取当前用户信息
- `PUT /api/admin/users/password` - 修改密码
- `GET /api/admin/users/login-history` - 获取登录历史
- `GET /api/admin/users/stats` - 获取统计信息

#### 栏目管理
- `GET /api/admin/categories` - 获取所有栏目
- `GET /api/admin/categories/tree` - 获取栏目树
- `GET /api/admin/categories/{id}` - 获取指定栏目
- `POST /api/admin/categories` - 创建栏目
- `PUT /api/admin/categories/{id}` - 更新栏目
- `DELETE /api/admin/categories/{id}` - 删除栏目

#### 卡片管理
- `GET /api/admin/cards` - 获取所有卡片
- `GET /api/admin/cards/{id}` - 获取指定卡片
- `GET /api/admin/cards/category/{id}` - 获取指定栏目的卡片
- `POST /api/admin/cards` - 创建卡片
- `PUT /api/admin/cards/{id}` - 更新卡片
- `DELETE /api/admin/cards/{id}` - 删除卡片
- `POST /api/admin/cards/parse` - 解析网站信息

#### 广告管理
- `GET /api/admin/ads` - 获取所有广告
- `GET /api/admin/ads/{id}` - 获取指定广告
- `POST /api/admin/ads` - 创建广告
- `PUT /api/admin/ads/{id}` - 更新广告
- `DELETE /api/admin/ads/{id}` - 删除广告

#### 友链管理
- `GET /api/admin/friendlinks` - 获取所有友链
- `GET /api/admin/friendlinks/{id}` - 获取指定友链
- `POST /api/admin/friendlinks` - 创建友链
- `PUT /api/admin/friendlinks/{id}` - 更新友链
- `DELETE /api/admin/friendlinks/{id}` - 删除友链

#### 备份管理
- `POST /api/admin/backup/create` - 创建备份
- `POST /api/admin/backup/restore` - 恢复备份
- `GET /api/admin/backup/list` - 获取备份列表
- `GET /api/admin/backup/download/{filename}` - 下载备份

#### 文件上传
- `POST /api/admin/upload` - 上传文件
- `DELETE /api/admin/upload?url=xxx` - 删除文件

## 🔒 安全说明

1. **首次部署后请立即修改管理员密码**
2. JWT Token 有效期为 24 小时
3. 建议在生产环境中修改 `application.properties` 中的 `jwt.secret`
4. 数据库文件 `navigation.db` 请定期备份
5. 上传的文件存储在 `uploads` 目录，建议定期清理

## 🐛 常见问题

### 1. 启动失败，提示端口被占用

修改 `application.properties` 中的端口号，或通过环境变量设置：

```bash
export PORT=9090
```

### 2. 登录后台提示未授权

检查浏览器控制台，确认 Token 是否正确保存在 localStorage 中。清除浏览器缓存后重新登录。

### 3. 卡片解析功能失败

确保服务器能够访问目标网站。某些网站可能有防爬措施，导致解析失败。

### 4. 备份文件无法恢复

确保上传的备份文件是由本系统创建的 ZIP 文件，且文件未损坏。

### 5. 图片无法显示

- 检查图片URL是否正确
- 确保图片链接可公开访问
- 对于本地上传的图片，确保 `uploads` 目录有读权限

## 📝 开发说明

### 添加新功能

1. 创建实体类（entity包）
2. 创建数据访问接口（repository包）
3. 创建业务逻辑类（service包）
4. 创建控制器（controller包）
5. 更新前端页面

### 数据库表结构

系统使用 JPA 自动创建表结构，主要表包括：

- `users` - 用户表
- `categories` - 栏目表
- `cards` - 卡片表
- `advertisements` - 广告表
- `friend_links` - 友链表
- `login_history` - 登录历史表

## 📄 许可证

本项目采用 MIT 许可证。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📮 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件至：admin@example.com

---

**祝您使用愉快！** 🎉
