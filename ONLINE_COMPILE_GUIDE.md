# 🌐 在线编译服务完整指南

## 📋 目录
1. [GitHub上传步骤](#github上传步骤)
2. [GitHub Actions自动编译](#github-actions自动编译)
3. [其他在线编译平台](#其他在线编译平台)
4. [APK下载和安装](#apk下载和安装)
5. [常见问题解答](#常见问题解答)

## GitHub上传步骤

### 方法1: 使用GitHub Desktop (最简单)
1. **下载安装** [GitHub Desktop](https://desktop.github.com)
2. **登录账号** 使用GitHub账号登录
3. **添加仓库** File → Add Local Repository
4. **选择项目** 浏览到 `projects/36-ten-day-tracker`
5. **提交更改** 填写提交信息，点击Commit
6. **发布仓库** Publish repository → 选择私有/公开
7. **完成** 等待上传完成

### 方法2: 使用命令行
```bash
# 1. 进入项目目录
cd projects/36-ten-day-tracker

# 2. 初始化Git (如果还没初始化)
git init

# 3. 添加所有文件
git add .

# 4. 提交更改
git commit -m "初始提交: 36个10天周期追踪器"

# 5. 在GitHub创建新仓库
# 访问: https://github.com/new
# 填写仓库信息，不要初始化README

# 6. 添加远程仓库
git remote add origin https://github.com/你的用户名/仓库名.git

# 7. 推送代码
git push -u origin main
```

### 方法3: 使用网页上传
1. 在GitHub创建空仓库
2. 点击 "uploading an existing file"
3. 拖拽整个项目文件夹
4. 填写提交信息
5. 点击Commit changes

## GitHub Actions自动编译

### 配置自动编译
1. 上传代码到GitHub后
2. 进入仓库页面
3. 点击 "Actions" 标签
4. 系统会自动检测到Android项目
5. 点击 "Set up this workflow"
6. 使用我们预配置的 `android-build.yml`

### 手动触发编译
1. 进入仓库 → Actions
2. 选择 "Android CI" workflow
3. 点击 "Run workflow"
4. 选择分支 (main)
5. 点击绿色运行按钮

### 查看编译结果
1. 编译完成后，点击对应的运行
2. 查看 "Artifacts" 部分
3. 下载生成的APK文件
4. 查看编译日志排查问题

## 其他在线编译平台

### 1. Bitrise (推荐)
- **网址**: https://bitrise.io
- **特点**: 专门为移动应用设计
- **免费额度**: 每月200分钟
- **步骤**:
  1. 连接GitHub账号
  2. 选择仓库
  3. 选择Android模板
  4. 开始构建

### 2. Codemagic
- **网址**: https://codemagic.io
- **特点**: Flutter和Android专用
- **免费额度**: 每月500分钟
- **步骤**:
  1. 登录并连接GitHub
  2. 选择项目
  3. 配置工作流
  4. 开始构建

### 3. GitHub Codespaces
- **网址**: GitHub内置功能
- **特点**: 在线IDE + 编译
- **免费额度**: 每月60小时
- **步骤**:
  1. 在仓库页面按 `.` 键
  2. 打开在线VS Code
  3. 打开终端运行 `./gradlew build`
  4. 下载生成的APK

### 4. GitLab CI/CD
- **网址**: https://gitlab.com
- **特点**: 类似GitHub Actions
- **免费额度**: 每月400分钟
- **步骤**:
  1. 导入GitHub仓库到GitLab
  2. 配置 `.gitlab-ci.yml`
  3. 自动构建和部署

## APK下载和安装

### 从GitHub Actions下载
1. 完成编译后，进入Actions页面
2. 点击最新的工作流运行
3. 滚动到 "Artifacts" 部分
4. 点击 "apk-files" 下载
5. 解压后找到APK文件

### 安装APK到手机
1. **Android 8.0+**:
   - 下载APK到手机
   - 点击文件管理器中的APK
   - 允许"未知来源"安装
   - 点击安装

2. **使用ADB安装**:
   ```bash
   # 连接手机并启用USB调试
   adb devices
   adb install app-debug.apk
   ```

3. **通过二维码安装**:
   - 上传APK到文件分享服务
   - 生成下载二维码
   - 手机扫码下载安装

## 常见问题解答

### ❓ 编译失败怎么办？
1. **检查日志**: 查看详细的错误信息
2. **依赖问题**: 确保网络可以访问Maven仓库
3. **版本兼容**: 检查Gradle和Android插件版本
4. **内存不足**: 增加Gradle堆内存 `-Xmx2048m`

### ❓ APK安装失败？
1. **签名问题**: 调试版APK可以直接安装
2. **Android版本**: 确保手机Android 8.0+
3. **存储权限**: 允许安装未知来源应用
4. **冲突应用**: 卸载旧版本再安装

### ❓ 如何更新应用？
1. 推送新代码到GitHub
2. GitHub Actions自动编译
3. 下载新的APK
4. 安装覆盖旧版本

### ❓ 需要哪些权限？
应用需要以下权限：
- 网络权限 (检查更新)
- 存储权限 (备份数据)
- 通知权限 (提醒功能)
- 这些权限在安装时会请求

## 📱 快速开始模板

### 一键上传脚本
```bash
#!/bin/bash
# 保存为 upload.sh，运行: bash upload.sh

echo "🚀 开始上传到GitHub..."

# 配置信息
GITHUB_USER="你的用户名"
REPO_NAME="36-ten-day-tracker"
REPO_DESC="36个10天周期追踪器Android应用"

# 初始化Git
git init
git add .
git commit -m "初始提交: $REPO_DESC"

# 创建GitHub仓库 (需要gh命令行工具)
gh repo create $REPO_NAME --description "$REPO_DESC" --private --source=. --remote=origin --push

echo "✅ 上传完成！"
echo "🌐 访问: https://github.com/$GITHUB_USER/$REPO_NAME"
```

### 预配置的工作流
项目已包含 `.github/workflows/android-build.yml`，上传后自动生效。

## 🎯 最佳实践

### 1. 版本管理
- 使用语义化版本号 (1.0.0, 1.1.0等)
- 每次更新都打标签 `git tag v1.0.0`
- 发布时生成Release notes

### 2. 安全考虑
- 使用私有仓库保护源代码
- 不要在代码中硬编码敏感信息
- 使用环境变量存储密钥

### 3. 性能优化
- 启用Gradle缓存加速编译
- 使用并行构建 `--parallel`
- 配置构建缓存

## 📞 技术支持

遇到问题可以：
1. 查看GitHub Actions日志
2. 搜索Stack Overflow相关问题
3. 查看Android官方文档
4. 在GitHub Issues提问

## 🎉 开始行动！

选择最适合你的方法：
- **新手**: 使用GitHub Desktop
- **开发者**: 使用命令行 + GitHub Actions
- **团队**: 使用Bitrise或Codemagic

**立即开始**: 打开终端，进入项目目录，按照上面的步骤操作！

---
*提示: 建议先从私有仓库开始，熟悉后再转为公开。*