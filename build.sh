#!/bin/bash

# ============================================================
# Java Coverage Tool 构建脚本
# 用途: 一键打包整个项目生成可执行应用
# 使用: ./build.sh [选项]
# 选项:
#   --skip-java     跳过 Java 后端构建
#   --skip-react    跳过 React 前端构建
#   --skip-electron 跳过 Electron 打包
#   --no-sign       跳过 macOS 代码签名 (开发环境使用)
#   --help          显示帮助信息
# ============================================================

# 颜色输出定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 项目根目录
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
JAVA_BACKEND="$PROJECT_ROOT/java-backend"
REACT_FRONTEND="$PROJECT_ROOT/react-frontend"
ELECTRON_APP="$PROJECT_ROOT/electron-app"
BUILD_OUTPUT="$PROJECT_ROOT/build"

# 构建选项
SKIP_JAVA=false
SKIP_REACT=false
SKIP_ELECTRON=false
NO_SIGN=false

# ============================================================
# 打印带颜色的消息
# ============================================================
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# ============================================================
# 解析命令行参数
# ============================================================
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            --skip-java)
                SKIP_JAVA=true
                shift
                ;;
            --skip-react)
                SKIP_REACT=true
                shift
                ;;
            --skip-electron)
                SKIP_ELECTRON=true
                shift
                ;;
            --no-sign)
                NO_SIGN=true
                shift
                ;;
            --help|-h)
                echo "用法: $0 [选项]"
                echo ""
                echo "选项:"
                echo "  --skip-java       跳过 Java 后端构建"
                echo "  --skip-react      跳过 React 前端构建"
                echo "  --skip-electron   跳过 Electron 打包"
                echo "  --no-sign         跳过 macOS 代码签名 (开发环境)"
                echo "  --help, -h        显示此帮助信息"
                echo ""
                echo "示例:"
                echo "  $0                    # 完整构建"
                echo "  $0 --no-sign          # 不签名构建 (开发测试)"
                echo "  $0 --skip-java        # 跳过 Java 后端"
                exit 0
                ;;
            *)
                print_error "未知参数: $1"
                echo "使用 --help 查看帮助信息"
                exit 1
                ;;
        esac
    done
}

# ============================================================
# 检查必要的工具
# ============================================================
check_tools() {
    print_info "检查构建工具..."

    # 检查 Java
    if ! command -v java &> /dev/null; then
        print_error "未找到 Java，请安装 JDK 11 或更高版本"
        exit 1
    fi
    print_success "Java: $(java -version 2>&1 | head -n 1)"

    # 检查 Maven
    if ! command -v mvn &> /dev/null; then
        print_error "未找到 Maven，请安装 Maven"
        exit 1
    fi
    print_success "Maven: $(mvn -version | head -n 1)"

    # 检查 Node.js
    if ! command -v node &> /dev/null; then
        print_error "未找到 Node.js，请安装 Node.js 16 或更高版本"
        exit 1
    fi
    print_success "Node.js: $(node -v)"

    # 检查 npm
    if ! command -v npm &> /dev/null; then
        print_error "未找到 npm"
        exit 1
    fi
    print_success "npm: $(npm -v)"
}

# ============================================================
# 清理旧的构建文件
# ============================================================
clean_build() {
    print_info "清理旧的构建文件..."
    
    # 创建构建目录
    mkdir -p "$BUILD_OUTPUT"
    
    # 清理 Electron 构建输出
    if [ -d "$BUILD_OUTPUT/electron-app" ]; then
        rm -rf "$BUILD_OUTPUT/electron-app"
        print_success "已清理 electron-app 目录"
    fi
    
    # 清理 Java JAR (仅在不跳过Java构建时)
    if [ "$SKIP_JAVA" = false ] && [ -f "$BUILD_OUTPUT/java-backend.jar" ]; then
        rm "$BUILD_OUTPUT/java-backend.jar"
        print_success "已清理旧的 JAR 文件"
    fi
}

# ============================================================
# 构建 Java 后端
# ============================================================
build_java_backend() {
    if [ "$SKIP_JAVA" = true ]; then
        print_warning "跳过 Java 后端构建"
        return 0
    fi

    print_info "=========================================="
    print_info "开始构建 Java 后端..."
    print_info "=========================================="

    cd "$JAVA_BACKEND" || exit 1

    # 清理并打包
    print_info "执行 Maven clean package..."
    mvn clean package -DskipTests

    if [ $? -ne 0 ]; then
        print_error "Java 后端构建失败"
        exit 1
    fi

    # 复制 JAR 到构建目录
    cp target/java-coverage-backend-1.0.0.jar "$BUILD_OUTPUT/java-backend.jar"
    
    if [ $? -eq 0 ]; then
        print_success "Java 后端构建成功"
        print_info "JAR 文件位置: $BUILD_OUTPUT/java-backend.jar"
        
        # 显示文件大小
        SIZE=$(du -h "$BUILD_OUTPUT/java-backend.jar" | cut -f1)
        print_info "JAR 文件大小: $SIZE"
    else
        print_error "复制 JAR 文件失败"
        exit 1
    fi
}

# ============================================================
# 构建 React 前端
# ============================================================
build_react_frontend() {
    if [ "$SKIP_REACT" = true ]; then
        print_warning "跳过 React 前端构建"
        return 0
    fi

    print_info "=========================================="
    print_info "开始构建 React 前端..."
    print_info "=========================================="

    cd "$REACT_FRONTEND" || exit 1

    # 检查 node_modules
    if [ ! -d "node_modules" ]; then
        print_info "安装前端依赖..."
        npm install
        
        if [ $? -ne 0 ]; then
            print_error "前端依赖安装失败"
            exit 1
        fi
    fi

    # 构建生产版本
    print_info "执行 npm run build..."
    npm run build

    if [ $? -ne 0 ]; then
        print_error "React 前端构建失败"
        exit 1
    fi

    print_success "React 前端构建成功"
    print_info "构建文件位置: $REACT_FRONTEND/build"
    
    # 显示构建文件大小
    SIZE=$(du -sh "$REACT_FRONTEND/build" | cut -f1)
    print_info "构建目录大小: $SIZE"
}

# ============================================================
# 构建 Electron 应用
# ============================================================
build_electron_app() {
    if [ "$SKIP_ELECTRON" = true ]; then
        print_warning "跳过 Electron 打包"
        return 0
    fi

    print_info "=========================================="
    print_info "开始打包 Electron 应用..."
    print_info "=========================================="

    cd "$ELECTRON_APP" || exit 1

    # 检查 node_modules
    if [ ! -d "node_modules" ]; then
        print_info "安装 Electron 依赖..."
        ELECTRON_MIRROR=https://npmmirror.com/mirrors/electron/ npm install
        
        if [ $? -ne 0 ]; then
            print_error "Electron 依赖安装失败"
            exit 1
        fi
    fi

    # 检查前端构建文件是否存在
    if [ ! -d "$REACT_FRONTEND/build" ]; then
        print_error "未找到前端构建文件，请先构建前端"
        exit 1
    fi

    # 检查后端 JAR 是否存在
    if [ ! -f "$BUILD_OUTPUT/java-backend.jar" ]; then
        print_error "未找到后端 JAR 文件，请先构建 Java 后端"
        exit 1
    fi

    # 检查图标文件
    if [ ! -f "$ELECTRON_APP/assets/icon.png" ]; then
        print_warning "未找到图标文件，将使用默认图标"
    fi

    # 设置 Electron 镜像 (中国区加速)
    export ELECTRON_MIRROR=https://npmmirror.com/mirrors/electron/

    # 根据是否签名选择构建方式
    if [ "$NO_SIGN" = true ]; then
        print_warning "跳过代码签名 (仅用于开发测试)"
        
        # 临时修改 package.json 以禁用签名
        # 对于 macOS，设置 CSC_IDENTITY_AUTO_DISCOVERY=false
        export CSC_IDENTITY_AUTO_DISCOVERY=false
        
        print_info "执行 electron-builder (无签名)..."
        npm run build -- --mac --win --linux --config.mac.identity=null
        
        if [ $? -ne 0 ]; then
            print_error "Electron 打包失败"
            exit 1
        fi
    else
        print_info "执行 electron-builder (带签名)..."
        npm run build
        
        if [ $? -ne 0 ]; then
            print_error "Electron 打包失败"
            print_info "如果遇到签名问题，请尝试使用 --no-sign 选项"
            exit 1
        fi
    fi

    print_success "Electron 应用打包成功"
}

# ============================================================
# 显示构建结果
# ============================================================
show_build_result() {
    print_info "=========================================="
    print_info "构建完成！"
    print_info "=========================================="
    echo ""
    
    # 显示生成的文件
    if [ -d "$BUILD_OUTPUT/electron-app" ]; then
        print_success "Electron 应用已生成在: $BUILD_OUTPUT/electron-app"
        echo ""
        print_info "生成的文件:"
        
        # macOS
        if [ -d "$BUILD_OUTPUT/electron-app/mac" ]; then
            echo "  📱 macOS (x64):"
            echo "     - $BUILD_OUTPUT/electron-app/mac/Java Coverage Tool.app"
        fi
        
        if [ -d "$BUILD_OUTPUT/electron-app/mac-arm64" ]; then
            echo "  📱 macOS (arm64):"
            echo "     - $BUILD_OUTPUT/electron-app/mac-arm64/Java Coverage Tool.app"
        fi
        
        # DMG
        DMG_FILES=$(find "$BUILD_OUTPUT/electron-app" -name "*.dmg" 2>/dev/null)
        if [ -n "$DMG_FILES" ]; then
            echo "  💿 DMG 安装包:"
            echo "$DMG_FILES" | while read file; do
                echo "     - $file"
            done
        fi
        
        # ZIP
        ZIP_FILES=$(find "$BUILD_OUTPUT/electron-app" -name "*-mac.zip" 2>/dev/null)
        if [ -n "$ZIP_FILES" ]; then
            echo "  📦 ZIP 压缩包:"
            echo "$ZIP_FILES" | while read file; do
                echo "     - $file"
            done
        fi
        
        # Windows
        EXE_FILES=$(find "$BUILD_OUTPUT/electron-app" -name "*.exe" 2>/dev/null)
        if [ -n "$EXE_FILES" ]; then
            echo "  🪟 Windows 安装包:"
            echo "$EXE_FILES" | while read file; do
                echo "     - $file"
            done
        fi
        
        # Linux
        APPIMAGE_FILES=$(find "$BUILD_OUTPUT/electron-app" -name "*.AppImage" 2>/dev/null)
        if [ -n "$APPIMAGE_FILES" ]; then
            echo "  🐧 Linux AppImage:"
            echo "$APPIMAGE_FILES" | while read file; do
                echo "     - $file"
            done
        fi
        
        DEB_FILES=$(find "$BUILD_OUTPUT/electron-app" -name "*.deb" 2>/dev/null)
        if [ -n "$DEB_FILES" ]; then
            echo "  🐧 Linux DEB:"
            echo "$DEB_FILES" | while read file; do
                echo "     - $file"
            done
        fi
    fi
    
    echo ""
    print_info "Java 后端 JAR: $BUILD_OUTPUT/java-backend.jar"
    echo ""
    
    if [ "$NO_SIGN" = true ]; then
        print_warning "注意: 应用未签名，可能无法在其他 Mac 上运行"
        print_info "如需分发应用，请在不使用 --no-sign 的情况下重新构建"
    fi
}

# ============================================================
# 主构建流程
# ============================================================
main() {
    echo ""
    echo "=========================================="
    echo "   Java Coverage Tool 构建脚本"
    echo "=========================================="
    echo ""
    
    # 解析参数
    parse_args "$@"
    
    # 检查工具
    check_tools
    echo ""
    
    # 清理旧构建
    clean_build
    echo ""
    
    # 记录开始时间
    START_TIME=$(date +%s)
    
    # 构建各个模块
    build_java_backend
    echo ""
    
    build_react_frontend
    echo ""
    
    build_electron_app
    echo ""
    
    # 记录结束时间
    END_TIME=$(date +%s)
    DURATION=$((END_TIME - START_TIME))
    
    # 显示结果
    show_build_result
    
    echo ""
    print_success "总构建时间: ${DURATION} 秒"
    echo ""
}

# 执行主函数
main "$@"
