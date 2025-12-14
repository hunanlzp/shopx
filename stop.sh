#!/bin/bash

# ShopX 项目停止脚本

echo "🛑 停止 ShopX 服务..."

# 停止后端服务
echo "停止后端服务..."
pkill -f "spring-boot:run"
pkill -f "shopx-backend"

# 停止前端服务
echo "停止前端服务..."
pkill -f "vite"
pkill -f "npm run dev"

# 等待进程完全停止
sleep 2

echo "✅ 所有服务已停止"
