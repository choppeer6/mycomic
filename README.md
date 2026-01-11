# MyComic - 个人漫画阅读网站

MyComic 是一个基于 Spring Boot 构建的轻量级个人漫画阅读系统。它支持本地漫画导入、在线阅读、章节管理等功能，旨在为用户提供流畅、私有的漫画阅读体验。

## 📚 功能特性

*   **漫画管理**：
    *   支持漫画列表展示、搜索（按标题）。
    *   支持漫画详情查看（封面、作者、简介、状态）。
    *   支持分类管理（热血、恋爱、冒险等）。
*   **阅读体验**：
    *   沉浸式阅读器，支持单页/长条模式（取决于图片）。
    *   章节导航（上一章/下一章）。
    *   懒加载图片，优化阅读流畅度。
*   **数据导入**：
    *   **本地导入**：支持扫描本地文件夹结构批量导入漫画（`uploads/import`）。
    *   **测试数据生成**：提供接口一键生成测试数据。
*   **系统特性**：
    *   前后端分离架构（后端 API + 前端静态页面）。
    *   基于文件系统的图片存储。
    *   RESTful API 设计。

## 🛠 技术栈

*   **后端**: Spring Boot 2.7.18
*   **数据库**: MySQL 8.0
*   **ORM**: MyBatis + Spring Data JPA (混合使用)
*   **工具**: Jsoup (爬虫/HTML解析), Lombok, PageHelper
*   **前端**: 原生 HTML/CSS/JS (单页应用风格)

## 📂 目录结构

```
mycomic/
├── src/main/java/com/comic/reader/
│   ├── config/          # Web、MyBatis 等配置
│   ├── controller/      # API 控制器
│   ├── entity/          # 数据库实体 (Comic, Chapter, Page, Category)
│   ├── mapper/          # MyBatis Mapper 接口
│   ├── service/         # 业务逻辑层
│   │   ├── impl/        # 业务实现 (CrawlerServiceImpl 包含导入逻辑)
│   └── util/            # 工具类 (Result, FileUtil)
├── src/main/resources/
│   ├── mapper/          # MyBatis XML 映射文件 (如有)
│   ├── static/          # 前端静态资源
│   │   └── templates/   # HTML 页面 (index.html)
│   └── application.properties # 项目配置
├── uploads/             # (自动生成) 图片存储目录
│   └── import/          # (需手动创建) 本地漫画导入源目录
└── pom.xml              # Maven 依赖配置
```

## 🚀 快速开始

### 1. 环境准备

*   JDK 1.8+
*   MySQL 8.0+
*   Maven 3.6+

### 2. 数据库配置

1.  创建数据库：
    ```sql
    CREATE DATABASE comic_reader DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```
2.  修改 `src/main/resources/application.properties`：
    ```properties
    spring.datasource.username=你的用户名
    spring.datasource.password=你的密码
    # file.upload-dir=uploads/  <-- 图片存储路径，默认在项目根目录
    ```

### 3. 启动项目

在项目根目录下运行：
```bash
mvn spring-boot:run
```
或者在 IDE 中运行 `ComicApplication.java`。

访问地址：`http://localhost:8080`

### 4. 数据初始化

#### 方式一：生成测试数据
访问接口：`http://localhost:8080/api/test/init-data`
> 这将生成 8 部热门漫画及若干章节，图片使用随机图源。

#### 方式二：导入本地漫画
1.  在项目根目录下创建 `uploads/import` 文件夹。
2.  按以下结构放入漫画文件：
    ```
    uploads/import/
    ├── 进击的巨人/              <-- 漫画名
    │   ├── cover.jpg           <-- 封面 (可选)
    │   ├── 第1话/              <-- 章节名
    │   │   ├── 001.jpg         <-- 图片
    │   │   └── 002.jpg
    │   └── 第2话/
    │       └── ...
    ```
3.  调用导入接口（POST）：
    `http://localhost:8080/api/upload/import-local`

## 🔌 API 接口概览

| 模块 | 方法 | 路径 | 描述 |
| :--- | :--- | :--- | :--- |
| **漫画** | GET | `/api/comics/list` | 获取漫画列表（支持分页、搜索） |
| | GET | `/api/comics/{id}` | 获取漫画详情 |
| **章节** | GET | `/api/chapters/comic/{id}` | 获取章节列表 |
| | GET | `/api/chapters/{id}/read` | 获取阅读内容（图片+导航） |
| **测试** | GET | `/api/test/init-data` | 初始化测试数据 |
| **上传** | POST | `/api/upload/import-local` | 触发本地漫画导入 |

## 📝 开发计划

- [x] 基础漫画/章节/图片管理
- [x] 本地文件导入功能
- [x] 前端阅读器 UI
- [ ] 用户注册/登录系统
- [ ] 阅读历史记录
- [ ] 漫画分类筛选功能完善
- [ ] 后台管理界面

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！
