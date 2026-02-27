#!/bin/bash

# Java Coverage Tool 启动脚本

echo "========================================="
echo "  Java Coverage Tool 启动脚本"
echo "========================================="

# 进入项目根目录
cd "$(dirname "$0")"

# 检查Java后端是否已构建
if [ ! -f "build/java-backend.jar" ]; then
    echo "错误: Java后端未构建"
    echo "请先运行: cd java-backend && mvn clean package"
    exit 1
fi

# 检查React前端是否已构建
if [ ! -f "react-frontend/build/index.html" ]; then
    echo "错误: React前端未构建"
    echo "请先运行: cd react-frontend && npm run build"
    exit 1
fi

# 检查Electron依赖是否已安装
if [ ! -d "electron-app/node_modules" ]; then
    echo "正在安装Electron依赖..."
    cd electron-app && npm install
    cd ..
fi

echo "启动Electron应用..."
cd electron-app
npm start
