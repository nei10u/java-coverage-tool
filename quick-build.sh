#!/bin/bash

# ============================================================
# 快速测试构建脚本 - 仅打包macOS版本（无签名）
# ============================================================

echo "=========================================="
echo "  快速测试构建 - macOS (无签名)"
echo "=========================================="
echo ""

# 设置项目根目录
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_ROOT"

# 检查必要的文件
echo "检查必要文件..."
if [ ! -f "build/java-backend.jar" ]; then
    echo "❌ 错误: 未找到 build/java-backend.jar"
    echo "请先运行: cd java-backend && mvn clean package"
    exit 1
fi

if [ ! -d "react-frontend/build" ]; then
    echo "❌ 错误: 未找到 react-frontend/build"
    echo "请先运行: cd react-frontend && npm run build"
    exit 1
fi

echo "✅ 所有必要文件已就绪"
echo ""

# 设置环境变量
export CSC_IDENTITY_AUTO_DISCOVERY=false
export ELECTRON_MIRROR=https://npmmirror.com/mirrors/electron/

# 清理旧的构建
if [ -d "build/electron-app/mac" ]; then
    echo "清理旧的构建文件..."
    rm -rf build/electron-app/mac
fi

# 打包macOS版本
echo "开始打包 macOS 版本 (无签名)..."
cd electron-app

if [ ! -d "node_modules" ]; then
    echo "安装依赖..."
    npm install
fi

echo "执行 electron-builder..."
npm run build -- --mac --config.mac.identity=null

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "✅ 构建成功！"
    echo "=========================================="
    echo ""
    echo "应用位置:"
    echo "  📱 build/electron-app/mac/Java Coverage Tool.app"
    echo ""
    echo "运行应用:"
    echo "  open 'build/electron-app/mac/Java Coverage Tool.app'"
    echo ""
else
    echo ""
    echo "❌ 构建失败"
    exit 1
fi
