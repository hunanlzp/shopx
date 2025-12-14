#!/bin/bash

# ShopX 项目启动脚本
# 用于快速启动前后端服务

echo "🚀 启动 ShopX 创新电商平台..."

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java环境，请安装JDK 17+"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven环境，请安装Maven 3.6+"
    exit 1
fi

# 检查Node.js环境
if ! command -v node &> /dev/null; then
    echo "❌ 错误: 未找到Node.js环境，请安装Node.js 18+"
    exit 1
fi

# 检查MySQL服务
if ! pgrep -x "mysqld" > /dev/null; then
    echo "⚠️  警告: MySQL服务未运行，请先启动MySQL服务"
fi

# 检查Redis服务
if ! pgrep -x "redis-server" > /dev/null; then
    echo "⚠️  警告: Redis服务未运行，请先启动Redis服务"
fi

echo "📦 安装后端依赖..."
cd backend
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 后端依赖安装失败"
    exit 1
fi

echo "📦 安装前端依赖..."
cd ../frontend
npm install

if [ $? -ne 0 ]; then
    echo "❌ 前端依赖安装失败"
    exit 1
fi

echo "🏗️  构建前端项目..."
npm run build

if [ $? -ne 0 ]; then
    echo "❌ 前端构建失败"
    exit 1
fi

echo "🎯 启动后端服务..."
cd ../backend
mvn spring-boot:run &

BACKEND_PID=$!

# 等待后端服务启动
echo "⏳ 等待后端服务启动..."
sleep 10

# 检查后端服务是否启动成功
if ! curl -s http://localhost:8080/api/products > /dev/null; then
    echo "❌ 后端服务启动失败"
    kill $BACKEND_PID
    exit 1
fi

echo "✅ 后端服务启动成功 (PID: $BACKEND_PID)"
echo "🌐 后端API地址: http://localhost:8080/api"
echo "📚 API文档地址: http://localhost:8080/api/swagger-ui.html"

echo "🎯 启动前端服务..."
cd ../frontend
npm run dev &

FRONTEND_PID=$!

echo "✅ 前端服务启动成功 (PID: $FRONTEND_PID)"
echo "🌐 前端应用地址: http://localhost:3000"

echo ""
echo "🎉 ShopX 创新电商平台启动完成！"
echo ""
echo "📋 服务信息:"
echo "   后端API: http://localhost:8080/api"
echo "   API文档: http://localhost:8080/api/swagger-ui.html"
echo "   前端应用: http://localhost:3000"
echo ""
echo "🛑 停止服务: 按 Ctrl+C 或运行 ./stop.sh"

# 等待用户中断
trap "echo '🛑 正在停止服务...'; kill $BACKEND_PID $FRONTEND_PID; exit 0" INT
wait
