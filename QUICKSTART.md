# 快速开始指南

## 🚀 一键构建和运行

### 1. 完整构建（首次使用或完整打包）

```bash
# 一键构建所有组件并打包应用（无签名，适合开发测试）
./build.sh --no-sign

# 或完整构建（带代码签名，适合分发）
./build.sh
```

### 2. 快速测试（仅macOS，无签名）

```bash
# 确保已构建Java后端和React前端
cd java-backend && mvn clean package
cp target/java-coverage-backend-1.0.0.jar ../build/java-backend.jar
cd ../react-frontend && npm run build
cd ..

# 快速打包macOS版本
./quick-build.sh
```

### 3. 运行打包后的应用

**macOS:**
```bash
open 'build/electron-app/mac/Java Coverage Tool.app'
```

**开发模式（热重载）:**
```bash
# 终端1: 启动Java后端
cd electron-app && npm start

# 终端2: 启动React开发服务器
cd react-frontend && npm start
```

## 📁 构建产物

构建完成后，以下文件将生成：

### macOS
- `build/electron-app/mac/Java Coverage Tool.app` - macOS应用
- `build/electron-app/*.dmg` - DMG安装包
- `build/electron-app/*-mac.zip` - ZIP压缩包

### Windows
- `build/electron-app/win-unpacked/` - 解压即用版本
- `build/electron-app/*.exe` - 安装包

### Linux
- `build/electron-app/linux-unpacked/` - 解压即用版本
- `build/electron-app/*.AppImage` - AppImage格式
- `build/electron-app/*.deb` - Debian/Ubuntu包

## 🔧 构建脚本选项

### build.sh 选项

```bash
./build.sh                  # 完整构建（带签名）
./build.sh --no-sign        # 无签名构建（开发测试）
./build.sh --skip-java      # 跳过Java后端构建
./build.sh --skip-react     # 跳过React前端构建
./build.sh --skip-electron  # 跳过Electron打包
./build.sh --help           # 显示帮助信息
```

### 组合使用

```bash
# 仅重新打包Electron（Java和React已构建）
./build.sh --no-sign --skip-java --skip-react

# 仅构建Java后端
./build.sh --skip-react --skip-electron
```

## ⚠️ 常见问题

### 1. 找不到JAR文件错误

```
Java backend JAR file not found
```

**解决方案:**
```bash
cd java-backend
mvn clean package
cp target/java-coverage-backend-1.0.0.jar ../build/java-backend.jar
```

### 2. 找不到前端构建文件

```
未找到前端构建文件
```

**解决方案:**
```bash
cd react-frontend
npm run build
```

### 3. macOS签名失败

```
Command failed: codesign ...
```

**解决方案:**
使用 `--no-sign` 选项跳过签名（开发测试）:
```bash
./build.sh --no-sign
```

### 4. Electron下载慢

**解决方案:**
已配置国内镜像，如果仍然慢，请检查网络或使用代理。

## 📝 开发流程

### 修改Java代码后

```bash
cd java-backend
mvn clean package
cp target/java-coverage-backend-1.0.0.jar ../build/java-backend.jar
cd ../electron-app && npm start
```

### 修改React代码后

```bash
cd react-frontend
npm start  # 开发模式会自动热重载
```

### 修改Electron代码后

```bash
cd electron-app
npm start
```

### 完整测试

```bash
# 重新构建并打包
./build.sh --no-sign

# 运行打包后的应用测试
open 'build/electron-app/mac/Java Coverage Tool.app'
```

## 🎯 下一步

1. **查看完整文档**: 阅读 [README.md](README.md)
2. **了解功能**: 查看报告页面的多维度统计和可视化图表
3. **自定义配置**: 修改 `electron-app/package.json` 自定义应用配置

---

**提示**: 首次构建建议使用 `./build.sh --no-sign` 以快速验证整个流程。
