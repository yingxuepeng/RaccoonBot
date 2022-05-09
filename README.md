# RaccoonBot

## 开发环境：
- Idea Community 22.01
- Java11
- maven
- mirai的idea插件（开发core估计不需要？）
- kotlin 1.6.21（可能编译要用？）

## 项目内部用的js脚本环境
- vscode
- node

## 运行环境：
- mysql >= 5.6
- redis >= 6.x

## 大致build部署顺序
- /script/ 里面 npm run build ===> js脚本存到了resources下面
- maven build ===> jar
- /deploy/deploy.sh 带环境参数部署
