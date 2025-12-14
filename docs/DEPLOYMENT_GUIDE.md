# ShopX 部署指南

## 📋 概述

本指南将帮助您在不同环境中部署ShopX项目，包括开发环境、测试环境和生产环境。

## 🛠️ 环境要求

### 基础环境
- **Java**: JDK 17+
- **Node.js**: 16+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Maven**: 3.6+
- **Docker**: 20.0+ (可选)

### 系统要求
- **CPU**: 2核心以上
- **内存**: 4GB以上
- **磁盘**: 20GB以上可用空间
- **网络**: 稳定的网络连接

## 🚀 开发环境部署

### 1. 环境准备

#### 安装Java 17
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# macOS
brew install openjdk@17

# 验证安装
java -version
```

#### 安装Node.js
```bash
# 使用nvm安装
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
source ~/.bashrc
nvm install 16
nvm use 16

# 验证安装
node -v
npm -v
```

#### 安装MySQL
```bash
# Ubuntu/Debian
sudo apt install mysql-server-8.0

# CentOS/RHEL
sudo yum install mysql-server

# macOS
brew install mysql

# 启动服务
sudo systemctl start mysql
sudo systemctl enable mysql
```

#### 安装Redis
```bash
# Ubuntu/Debian
sudo apt install redis-server

# CentOS/RHEL
sudo yum install redis

# macOS
brew install redis

# 启动服务
sudo systemctl start redis
sudo systemctl enable redis
```

### 2. 项目部署

#### 克隆项目
```bash
git clone https://github.com/your-username/shopx.git
cd shopx
```

#### 数据库初始化
```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE shopx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户
CREATE USER 'shopx'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON shopx.* TO 'shopx'@'localhost';
FLUSH PRIVILEGES;

# 导入数据
mysql -u shopx -p shopx < src/main/resources/db/schema.sql
mysql -u shopx -p shopx < src/main/resources/db/test_data.sql
```

#### 后端启动
```bash
# 修改配置文件
cp src/main/resources/application.yml.example src/main/resources/application.yml

# 编辑配置文件
vim src/main/resources/application.yml

# 启动后端服务
mvn spring-boot:run
```

#### 前端启动
```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 3. 验证部署
- **前端应用**: http://localhost:3000
- **后端API**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html

## 🐳 Docker部署

### 1. 创建Dockerfile

#### 后端Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/shopx-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 前端Dockerfile
```dockerfile
FROM node:16-alpine as build

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
```

### 2. Docker Compose配置
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: shopx-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: shopx
      MYSQL_USER: shopx
      MYSQL_PASSWORD: shoppassword
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./src/main/resources/db/schema.sql:/docker-entrypoint-initdb.d/schema.sql
      - ./src/main/resources/db/test_data.sql:/docker-entrypoint-initdb.d/test_data.sql

  redis:
    image: redis:6-alpine
    container_name: shopx-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  backend:
    build: .
    container_name: shopx-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/shopx
      SPRING_DATASOURCE_USERNAME: shopx
      SPRING_DATASOURCE_PASSWORD: shoppassword
      SPRING_REDIS_HOST: redis
    depends_on:
      - mysql
      - redis

  frontend:
    build: ./frontend
    container_name: shopx-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
  redis_data:
```

### 3. 部署命令
```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f backend

# 停止服务
docker-compose down
```

## 🌐 生产环境部署

### 1. 服务器准备

#### 系统配置
```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装必要工具
sudo apt install -y curl wget git vim htop

# 配置防火墙
sudo ufw allow 22
sudo ufw allow 80
sudo ufw allow 443
sudo ufw allow 8080
sudo ufw enable
```

#### 创建应用用户
```bash
# 创建用户
sudo useradd -m -s /bin/bash shopx
sudo usermod -aG sudo shopx

# 切换到应用用户
sudo su - shopx
```

### 2. 环境安装

#### 安装Java 17
```bash
# 下载OpenJDK
wget https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz

# 解压安装
sudo tar -xzf openjdk-17.0.2_linux-x64_bin.tar.gz -C /opt/
sudo ln -s /opt/jdk-17.0.2 /opt/java

# 配置环境变量
echo 'export JAVA_HOME=/opt/java' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

#### 安装Node.js
```bash
# 下载Node.js
curl -fsSL https://deb.nodesource.com/setup_16.x | sudo -E bash -
sudo apt-get install -y nodejs

# 验证安装
node -v
npm -v
```

#### 安装MySQL
```bash
# 安装MySQL
sudo apt install mysql-server-8.0

# 安全配置
sudo mysql_secure_installation

# 创建数据库和用户
sudo mysql -u root -p
```

```sql
CREATE DATABASE shopx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'shopx'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON shopx.* TO 'shopx'@'localhost';
FLUSH PRIVILEGES;
```

#### 安装Redis
```bash
# 安装Redis
sudo apt install redis-server

# 配置Redis
sudo vim /etc/redis/redis.conf
```

```conf
# 设置密码
requirepass your_redis_password

# 绑定到本地
bind 127.0.0.1

# 持久化配置
save 900 1
save 300 10
save 60 10000
```

```bash
# 重启Redis
sudo systemctl restart redis
sudo systemctl enable redis
```

#### 安装Nginx
```bash
# 安装Nginx
sudo apt install nginx

# 启动服务
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 3. 应用部署

#### 后端部署
```bash
# 克隆项目
git clone https://github.com/your-username/shopx.git
cd shopx

# 构建项目
mvn clean package -DskipTests

# 创建部署目录
sudo mkdir -p /opt/shopx
sudo chown shopx:shopx /opt/shopx

# 复制JAR文件
cp target/shopx-1.0.0.jar /opt/shopx/

# 创建配置文件
sudo mkdir -p /etc/shopx
sudo vim /etc/shopx/application.yml
```

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopx
    username: shopx
    password: your_secure_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
    database: 0

logging:
  level:
    com.shopx: INFO
    org.springframework: WARN
  file:
    name: /var/log/shopx/application.log
```

#### 前端部署
```bash
cd frontend

# 安装依赖
npm ci --only=production

# 构建项目
npm run build

# 复制构建文件
sudo cp -r dist/* /var/www/html/
```

#### 创建Systemd服务
```bash
sudo vim /etc/systemd/system/shopx.service
```

```ini
[Unit]
Description=ShopX Application
After=network.target mysql.service redis.service

[Service]
Type=simple
User=shopx
Group=shopx
WorkingDirectory=/opt/shopx
ExecStart=/opt/java/bin/java -jar -Dspring.config.location=/etc/shopx/application.yml /opt/shopx/shopx-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=shopx

[Install]
WantedBy=multi-user.target
```

```bash
# 创建日志目录
sudo mkdir -p /var/log/shopx
sudo chown shopx:shopx /var/log/shopx

# 启动服务
sudo systemctl daemon-reload
sudo systemctl enable shopx
sudo systemctl start shopx

# 检查状态
sudo systemctl status shopx
```

### 4. Nginx配置

#### 创建Nginx配置
```bash
sudo vim /etc/nginx/sites-available/shopx
```

```nginx
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;
    
    # 前端静态文件
    location / {
        root /var/www/html;
        index index.html;
        try_files $uri $uri/ /index.html;
        
        # 缓存静态资源
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
    
    # API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时设置
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }
    
    # WebSocket支持
    location /ws {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # 安全头
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
}
```

```bash
# 启用站点
sudo ln -s /etc/nginx/sites-available/shopx /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 5. SSL证书配置

#### 使用Let's Encrypt
```bash
# 安装Certbot
sudo apt install certbot python3-certbot-nginx

# 获取SSL证书
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# 自动续期
sudo crontab -e
```

```cron
0 12 * * * /usr/bin/certbot renew --quiet
```

## 📊 监控和日志

### 1. 应用监控

#### 配置Prometheus
```bash
# 下载Prometheus
wget https://github.com/prometheus/prometheus/releases/download/v2.40.0/prometheus-2.40.0.linux-amd64.tar.gz
tar xzf prometheus-2.40.0.linux-amd64.tar.gz
sudo mv prometheus-2.40.0.linux-amd64 /opt/prometheus

# 创建配置文件
sudo vim /opt/prometheus/prometheus.yml
```

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'shopx'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
```

#### 配置Grafana
```bash
# 安装Grafana
sudo apt install -y adduser libfontconfig1
wget https://dl.grafana.com/oss/release/grafana_9.3.0_amd64.deb
sudo dpkg -i grafana_9.3.0_amd64.deb

# 启动服务
sudo systemctl start grafana-server
sudo systemctl enable grafana-server
```

### 2. 日志管理

#### 配置Logrotate
```bash
sudo vim /etc/logrotate.d/shopx
```

```
/var/log/shopx/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 shopx shopx
    postrotate
        systemctl reload shopx
    endscript
}
```

#### 配置ELK Stack (可选)
```bash
# 安装Elasticsearch
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.5.0-amd64.deb
sudo dpkg -i elasticsearch-8.5.0-amd64.deb

# 安装Logstash
wget https://artifacts.elastic.co/downloads/logstash/logstash-8.5.0-amd64.deb
sudo dpkg -i logstash-8.5.0-amd64.deb

# 安装Kibana
wget https://artifacts.elastic.co/downloads/kibana/kibana-8.5.0-amd64.deb
sudo dpkg -i kibana-8.5.0-amd64.deb
```

## 🔧 性能优化

### 1. JVM优化
```bash
# 修改启动参数
sudo vim /etc/systemd/system/shopx.service
```

```ini
[Service]
ExecStart=/opt/java/bin/java -Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -jar /opt/shopx/shopx-1.0.0.jar
```

### 2. 数据库优化
```sql
-- MySQL配置优化
SET GLOBAL innodb_buffer_pool_size = 2G;
SET GLOBAL innodb_log_file_size = 256M;
SET GLOBAL max_connections = 500;
```

### 3. Redis优化
```conf
# Redis配置优化
maxmemory 1gb
maxmemory-policy allkeys-lru
tcp-keepalive 60
```

### 4. Nginx优化
```nginx
# Nginx配置优化
worker_processes auto;
worker_connections 1024;

gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
```

## 🚨 故障排除

### 1. 常见问题

#### 服务无法启动
```bash
# 检查服务状态
sudo systemctl status shopx

# 查看日志
sudo journalctl -u shopx -f

# 检查端口占用
sudo netstat -tlnp | grep :8080
```

#### 数据库连接失败
```bash
# 检查MySQL状态
sudo systemctl status mysql

# 测试连接
mysql -u shopx -p -h localhost shopx

# 检查防火墙
sudo ufw status
```

#### Redis连接失败
```bash
# 检查Redis状态
sudo systemctl status redis

# 测试连接
redis-cli ping

# 检查配置
sudo vim /etc/redis/redis.conf
```

### 2. 性能问题

#### 内存不足
```bash
# 检查内存使用
free -h
top -p $(pgrep java)

# 调整JVM参数
sudo vim /etc/systemd/system/shopx.service
```

#### 磁盘空间不足
```bash
# 检查磁盘使用
df -h
du -sh /var/log/shopx/*

# 清理日志
sudo find /var/log/shopx -name "*.log" -mtime +30 -delete
```

## 📞 技术支持

如有部署问题，请联系：
- **邮箱**：deploy-support@shopx.com
- **文档**：https://docs.shopx.com/deployment
- **GitHub**：https://github.com/shopx/deployment-guide
