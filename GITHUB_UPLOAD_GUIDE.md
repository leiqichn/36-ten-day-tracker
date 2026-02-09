#!/bin/bash

echo "🚀 GitHub上传助手"
echo "=================="

# 检查Git
if ! command -v git &> /dev/null; then
    echo "❌ 错误: 未安装Git，请先安装Git"
    echo "   Ubuntu/Debian: sudo apt install git"
    echo "   macOS: brew install git"
    echo "   Windows: 下载Git for Windows"
    exit 1
fi

# 检查是否在项目目录
if [ ! -f "build.gradle.kts" ]; then
    echo "❌ 错误: 请在项目根目录运行此脚本"
    echo "   当前目录: $(pwd)"
    exit 1
fi

echo "📊 项目信息:"
echo "   项目名称: 36个10天周期追踪器"
echo "   提交次数: $(git log --oneline | wc -l)"
echo "   文件数量: $(find . -type f -name "*.kt" -o -name "*.xml" -o -name "*.gradle*" | wc -l)"

echo ""
echo "📝 请输入GitHub信息:"
read -p "   GitHub用户名: " github_username
read -p "   仓库名称 (默认: 36-ten-day-tracker): " repo_name
repo_name=${repo_name:-"36-ten-day-tracker"}
read -p "   仓库描述 (默认: 36个10天周期追踪器Android应用): " repo_description
repo_description=${repo_description:-"36个10天周期追踪器Android应用"}
read -p "   是否私有仓库? (y/n, 默认: y): " is_private
is_private=${is_private:-"y"}

echo ""
echo "🔧 配置Git用户信息..."
read -p "   你的姓名 (用于Git提交): " user_name
read -p "   你的邮箱 (用于Git提交): " user_email

git config user.name "$user_name"
git config user.email "$user_email"

echo ""
echo "🔄 正在配置远程仓库..."

# 移除现有远程仓库
git remote remove origin 2>/dev/null

# 添加新的远程仓库
git remote add origin "https://github.com/$github_username/$repo_name.git"

echo ""
echo "📤 正在推送到GitHub..."

# 尝试推送
if git push -u origin main; then
    echo "✅ 推送成功！"
    echo ""
    echo "🌐 你的仓库地址:"
    echo "   https://github.com/$github_username/$repo_name"
    echo ""
    echo "🚀 下一步操作:"
    echo "   1. 访问上面的链接查看仓库"
    echo "   2. 设置GitHub Actions自动构建"
    echo "   3. 下载GitHub Desktop进行图形化管理"
else
    echo "❌ 推送失败，可能原因:"
    echo "   1. 仓库尚未创建"
    echo "   2. 网络连接问题"
    echo "   3. 权限不足"
    echo ""
    echo "📋 手动创建仓库步骤:"
    echo "   1. 访问 https://github.com/new"
    echo "   2. 填写仓库信息:"
    echo "      - 名称: $repo_name"
    echo "      - 描述: $repo_description"
    echo "      - 私有: $is_private"
    echo "   3. 不要初始化README"
    echo "   4. 创建后按照提示推送"
    echo ""
    echo "🔄 手动推送命令:"
    echo "   git remote add origin https://github.com/$github_username/$repo_name.git"
    echo "   git branch -M main"
    echo "   git push -u origin main"
fi

echo ""
echo "🔗 快速链接:"
echo "   📁 仓库: https://github.com/$github_username/$repo_name"
echo "   ⚙️  Actions: https://github.com/$github_username/$repo_name/actions"
echo "   📦 Releases: https://github.com/$github_username/$repo_name/releases"
echo ""
echo "🎉 完成！项目已准备好在线编译。"