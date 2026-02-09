#!/bin/bash

echo "🚀 开始构建36个10天周期追踪器Android应用..."

# 检查必要的工具
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java，请安装JDK 11或更高版本"
    exit 1
fi

if ! command -v gradle &> /dev/null; then
    echo "⚠️ 警告: 未找到gradle命令，尝试下载gradle wrapper..."
    # 这里可以添加下载gradle wrapper的逻辑
    echo "请手动下载gradle或使用Android Studio打开项目"
    exit 1
fi

echo "📦 检查项目结构..."
if [ ! -f "app/build.gradle.kts" ]; then
    echo "❌ 错误: 未找到app/build.gradle.kts"
    exit 1
fi

if [ ! -f "settings.gradle.kts" ]; then
    echo "❌ 错误: 未找到settings.gradle.kts"
    exit 1
fi

echo "🔧 同步Gradle依赖..."
gradle wrapper

echo "🔄 开始构建..."
./gradlew build

if [ $? -eq 0 ]; then
    echo "✅ 构建成功！"
    echo ""
    echo "📱 生成的APK文件位置:"
    echo "   - app/build/outputs/apk/debug/app-debug.apk"
    echo "   - app/build/outputs/apk/release/app-release.apk"
    echo ""
    echo "🚀 如何安装:"
    echo "   1. 将APK文件传输到Android设备"
    echo "   2. 在设备上启用'未知来源'安装"
    echo "   3. 点击APK文件进行安装"
    echo ""
    echo "💡 提示: 建议使用Android Studio进行开发和调试"
else
    echo "❌ 构建失败，请检查错误信息"
    exit 1
fi