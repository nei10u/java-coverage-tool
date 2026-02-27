# Java单元测试覆盖检测工具

一个基于Electron + React + Java的GUI工具，用于分析Java项目的单元测试覆盖率，并生成详细的多维度报告。

## ✨ 功能特性

- 🎯 **自动项目扫描** - 自动识别Maven/Gradle项目结构
- 📊 **测试覆盖分析** - 精确到方法级别的测试覆盖检测
- 📈 **Git历史关联** - 关联Git提交信息，评估个体贡献
- 📋 **多维度报告** - 支持开发者、提交、文件、方法四个维度查看
- 🎨 **现代化UI** - 基于Electron和React的友好界面
- 📊 **数据可视化** - 使用ECharts展示提交统计和覆盖率趋势
- 📄 **HTML报告导出** - 生成易于分享的HTML格式报告
- 🚀 **一键打包** - 提供自动化构建脚本，快速生成安装包

## 🏗️ 系统架构

```
┌─────────────────────────────────────┐
│      Electron + React GUI           │
│   (用户界面和交互)                    │
└──────────────┬──────────────────────┘
               │ HTTP API
               ▼
┌─────────────────────────────────────┐
│       Java Backend Service          │
│   (代码分析和报告生成)                │
└──────────────┬──────────────────────┘
               │
       ┌───────┴───────┐
       ▼               ▼
┌─────────────┐ ┌──────────────┐
│  JavaParser │ │    JGit      │
│  (代码解析)  │ │  (Git分析)   │
└─────────────┘ └──────────────┘
```

## 📦 项目结构

```
java-coverage-tool/
├── electron-app/           # Electron主进程
│   ├── main.js            # 主进程入口
│   ├── preload.js         # 预加载脚本
│   └── package.json
│
├── react-frontend/         # React前端
│   ├── src/
│   │   ├── pages/         # 页面组件
│   │   ├── App.jsx        # 主组件
│   │   └── index.jsx      # 入口文件
│   └── package.json
│
├── java-backend/           # Java后端
│   ├── src/main/java/
│   │   └── com/coveragetool/
│   │       ├── model/     # 数据模型
│   │       ├── scanner/   # 项目扫描
│   │       ├── analyzer/  # 代码分析
│   │       ├── git/       # Git分析
│   │       ├── coverage/  # 覆盖度分析
│   │       ├── report/    # 报告生成
│   │       └── api/       # REST API
│   └── pom.xml
│
└── package.json           # 根配置
```

## 🚀 快速开始

### 前置要求

- **Java 11+** - 运行Java后端
- **Node.js 16+** - 构建前端和运行Electron
- **Maven 3.6+** - 构建Java项目

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/your-org/java-coverage-tool.git
cd java-coverage-tool
```

2. **安装前端依赖**
```bash
# 安装Electron依赖
cd electron-app
npm install

# 安装React依赖
cd ../react-frontend
npm install

# 返回根目录
cd ..
```

3. **构建Java后端**
```bash
cd java-backend
mvn clean package
```

这将在`build/`目录生成`java-backend.jar`文件。

4. **构建React前端**
```bash
cd ../react-frontend
npm run build
```

### 运行应用

#### 开发模式

1. **启动Java后端**
```bash
cd java-backend
mvn exec:java
```

后端将在`http://localhost:4567`启动。

2. **启动React开发服务器**
```bash
cd ../react-frontend
npm start
```

React开发服务器将在`http://localhost:3000`启动。

3. **启动Electron（连接到React开发服务器）**
```bash
cd ../electron-app
NODE_ENV=development npm start
```

#### 生产模式 - 运行打包后的应用

**macOS:**
```bash
# 方法1: 使用终端打开
open 'build/electron-app/mac/Java Coverage Tool.app'

# 方法2: 双击应用图标
# 在 Finder 中找到: build/electron-app/mac/Java Coverage Tool.app
```

**Windows:**
```bash
# 运行解压版本
'build/electron-app/win-unpacked/Java Coverage Tool.exe'

# 或运行安装包
'build/electron-app/Java Coverage Tool Setup 1.0.0.exe'
```

**Linux:**
```bash
# AppImage (无需安装)
chmod +x 'build/electron-app/Java Coverage Tool-1.0.0.AppImage'
./'build/electron-app/Java Coverage Tool-1.0.0.AppImage'

# 或 DEB 包
sudo dpkg -i 'build/electron-app/java-coverage-electron_1.0.0_amd64.deb'
```

## 📖 使用指南

### 1. 选择项目

启动应用后，点击"选择项目目录"按钮，选择你的Java项目根目录。

### 2. 扫描项目

应用会自动扫描项目结构，识别：
- 项目类型（Maven/Gradle）
- 源码目录（如`src/main/java`）
- 测试目录（如`src/test/java`）

### 3. 配置分析

勾选需要分析的源码目录和测试目录。你可以：
- 选择多个模块的源码目录
- 选择对应的测试目录
- 配置Git分析选项

### 4. 开始分析

点击"开始分析"按钮，应用将：
1. 扫描所有业务类（Service、Controller、Repository等）
2. 分析测试类和测试方法
3. 获取Git提交历史
4. 计算测试覆盖率
5. 生成详细报告

### 5. 查看报告

分析完成后，你可以在多个维度查看结果：

- **概览** - 总体覆盖率统计和未覆盖方法列表
- **开发者统计** - 每个开发者的测试覆盖贡献
- **提交统计** - 每次提交的代码覆盖情况（包含图形化可视化）
  - 代码变更趋势图（新增/删除/净增行数）
  - 提交覆盖率柱状图
  - 开发者贡献饼图
- **文件统计** - 每个文件的测试覆盖详情
- **方法统计** - 每个方法的测试覆盖详情

### 6. 导出报告

点击"导出HTML报告"按钮，可以下载完整的HTML格式报告，方便分享和存档。

## 🔧 核心技术

### 前端技术栈

- **Electron 23** - 跨平台桌面应用框架
- **React 18** - UI框架
- **Ant Design 5** - UI组件库
- **React Router 6** - 路由管理
- **Axios** - HTTP客户端
- **ECharts** - 数据可视化图表库

### 后端技术栈

- **Java 11** - 后端语言
- **Spark Java** - 轻量级Web框架
- **JavaParser** - Java源码解析
- **JGit** - Git仓库操作
- **Gson** - JSON序列化

## 📊 覆盖率计算规则

### 方法级覆盖判定

一个业务方法被认为"已覆盖"，需要满足以下条件之一：
1. 存在测试方法名包含业务方法名
2. 测试方法的`testedBusinessMethod`属性匹配

### 测试粒度评估

测试粒度根据以下维度评分（满分100分）：
- **命名规范**（20分）- 测试方法名包含业务方法名
- **断言数量**（30分）- 至少3个断言
- **边界值测试**（25分）- 测试边界条件
- **异常测试**（15分）- 测试异常情况
- **Mock使用**（10分）- 合理使用Mock对象

评级标准：
- **优秀（Excellent）** - ≥80分
- **良好（Good）** - ≥60分
- **可接受（Acceptable）** - ≥40分
- **较差（Poor）** - <40分

## 📦 打包成独立软件

### 准备工作

1. **创建应用图标**
   
   在 `electron-app/assets/` 目录下放置图标文件：
   - `icon.png` (512x512 PNG格式，推荐)
   - `icon.svg` (SVG格式，可选)
   
   图标会自动转换各平台所需格式（.icns for macOS, .ico for Windows）

2. **构建所有组件**
   ```bash
   # 构建Java后端
   cd java-backend
   mvn clean package
   cp target/java-coverage-backend-1.0.0.jar ../build/java-backend.jar
   cd ..
   
   # 构建React前端
   cd react-frontend
   npm run build
   cd ..
   
   # 安装Electron依赖
   cd electron-app
   npm install
   cd ..
   ```

### 打包命令

#### macOS

```bash
cd electron-app
npm run build
```

输出位置：`build/electron-app/mac/Java Coverage Tool.app`

#### Windows

```bash
cd electron-app
npm run build -- --win
```

输出位置：`build/electron-app/win-unpacked/` 或 `.exe` 安装包

#### Linux

```bash
cd electron-app
npm run build -- --linux
```

输出位置：`build/electron-app/linux-unpacked/` 或 `.AppImage` 文件

### 一键打包

我们提供了自动化构建脚本 `build.sh`，可以一键打包整个项目。

#### 基本用法

```bash
# 完整构建（包含签名）
./build.sh

# 开发环境构建（跳过签名，速度更快）
./build.sh --no-sign

# 跳过某些步骤
./build.sh --skip-java      # 跳过Java后端构建
./build.sh --skip-react     # 跳过React前端构建
./build.sh --skip-electron  # 跳过Electron打包

# 组合使用
./build.sh --no-sign --skip-java

# 查看帮助
./build.sh --help
```

#### 构建输出

构建完成后，应用将生成在 `build/electron-app/` 目录：

**macOS:**
- `mac/Java Coverage Tool.app` - x64架构应用
- `mac-arm64/Java Coverage Tool.app` - arm64架构应用
- `*.dmg` - DMG安装包
- `*-mac.zip` - ZIP压缩包

**Windows:**
- `win-unpacked/` - 解压即用版本
- `*.exe` - NSIS安装包

**Linux:**
- `linux-unpacked/` - 解压即用版本
- `*.AppImage` - AppImage格式
- `*.deb` - Debian/Ubuntu安装包

#### 手动打包

如果你想手动打包各个组件，可以按以下步骤操作：

```bash
# 1. 构建Java后端
cd java-backend
mvn clean package -DskipTests
cp target/java-coverage-backend-1.0.0.jar ../build/java-backend.jar
cd ..

# 2. 构建React前端
cd react-frontend
npm run build
cd ..

# 3. 打包Electron应用
cd electron-app
npm install
npm run build        # 完整构建（带签名）
# 或者
npm run build:dev    # 开发构建（无签名）
cd ..
```

#### 平台特定构建

```bash
# 仅构建macOS版本
cd electron-app && npm run build:mac

# 仅构建Windows版本
cd electron-app && npm run build:win

# 仅构建Linux版本
cd electron-app && npm run build:linux
```

### 分发

打包完成后，你可以：

1. **macOS**: 压缩 `.app` 文件分发，或创建 DMG
2. **Windows**: 使用生成的 `.exe` 安装包
3. **Linux**: 分发 `.AppImage` 文件（无需安装）

### 自动更新配置（可选）

在 `electron-app/package.json` 中添加：

```json
{
  "build": {
    "publish": {
      "provider": "github",
      "owner": "your-github-username",
      "repo": "java-coverage-tool"
    }
  }
}
```

## 🤝 贡献指南

欢迎贡献代码、报告Bug或提出新功能建议！

1. Fork本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 📝 开发路线

- [x] 项目扫描和目录识别
- [x] 业务类和测试类分析
- [x] 方法级覆盖检测
- [x] Git历史分析
- [x] 基础报告生成
- [x] 多维度报告（开发者、提交、文件、方法）
- [x] 代码复杂度分析
- [x] 提交统计可视化（ECharts图表）
- [x] 一键打包脚本
- [ ] 代码复杂度可视化图表
- [ ] 历史趋势图表
- [ ] 自定义规则配置
- [ ] 增量分析支持
- [ ] 测试建议生成

## 📄 许可证

本项目采用MIT许可证 - 详见 [LICENSE](LICENSE) 文件

## 👥 作者

Coverage Tool Team

## 🙏 致谢

- [JavaParser](https://github.com/javaparser/javaparser) - 优秀的Java源码解析库
- [JGit](https://www.eclipse.org/jgit/) - 强大的Git操作库
- [Ant Design](https://ant.design/) - 美观的React UI组件库
- [Electron](https://www.electronjs.org/) - 跨平台桌面应用框架

---

**注意**：这是一个开发和演示版本，生产使用前请进行充分测试。
