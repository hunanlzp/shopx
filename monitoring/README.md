# ShopX 监控和日志系统配置

## 📊 Prometheus 监控配置

### prometheus.yml
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "shopx_rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

scrape_configs:
  # ShopX 应用监控
  - job_name: 'shopx-backend'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s

  # MySQL 监控
  - job_name: 'mysql'
    static_configs:
      - targets: ['mysql-exporter:9104']

  # Redis 监控
  - job_name: 'redis'
    static_configs:
      - targets: ['redis-exporter:9121']

  # Node Exporter 系统监控
  - job_name: 'node-exporter'
    static_configs:
      - targets: ['node-exporter:9100']

  # Nginx 监控
  - job_name: 'nginx'
    static_configs:
      - targets: ['nginx-exporter:9113']
```

### shopx_rules.yml
```yaml
groups:
  - name: shopx.rules
    rules:
      # API 响应时间告警
      - alert: HighAPILatency
        expr: histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m])) > 0.5
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "API响应时间过高"
          description: "95%的API请求响应时间超过500ms，当前值: {{ $value }}s"

      # 错误率告警
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) / rate(http_server_requests_seconds_count[5m]) > 0.05
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "API错误率过高"
          description: "5xx错误率超过5%，当前值: {{ $value }}"

      # 数据库连接数告警
      - alert: HighDatabaseConnections
        expr: mysql_global_status_threads_connected / mysql_global_variables_max_connections > 0.8
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "数据库连接数过高"
          description: "数据库连接数超过80%，当前值: {{ $value }}"

      # Redis 内存使用率告警
      - alert: HighRedisMemoryUsage
        expr: redis_memory_used_bytes / redis_memory_max_bytes > 0.8
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "Redis内存使用率过高"
          description: "Redis内存使用率超过80%，当前值: {{ $value }}"

      # 系统CPU使用率告警
      - alert: HighCPUUsage
        expr: 100 - (avg by(instance) (irate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "CPU使用率过高"
          description: "CPU使用率超过80%，当前值: {{ $value }}%"

      # 系统内存使用率告警
      - alert: HighMemoryUsage
        expr: (1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100 > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "内存使用率过高"
          description: "内存使用率超过80%，当前值: {{ $value }}%"

      # 磁盘空间告警
      - alert: LowDiskSpace
        expr: (1 - (node_filesystem_avail_bytes / node_filesystem_size_bytes)) * 100 > 85
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "磁盘空间不足"
          description: "磁盘使用率超过85%，当前值: {{ $value }}%"
```

## 📈 Grafana 仪表板配置

### dashboard.json
```json
{
  "dashboard": {
    "id": null,
    "title": "ShopX 系统监控",
    "tags": ["shopx", "monitoring"],
    "style": "dark",
    "timezone": "browser",
    "panels": [
      {
        "id": 1,
        "title": "API 请求量",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_server_requests_seconds_count[5m])",
            "legendFormat": "{{method}} {{uri}}"
          }
        ],
        "yAxes": [
          {
            "label": "请求/秒",
            "min": 0
          }
        ]
      },
      {
        "id": 2,
        "title": "API 响应时间",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))",
            "legendFormat": "95th percentile"
          },
          {
            "expr": "histogram_quantile(0.50, rate(http_server_requests_seconds_bucket[5m]))",
            "legendFormat": "50th percentile"
          }
        ],
        "yAxes": [
          {
            "label": "响应时间(秒)",
            "min": 0
          }
        ]
      },
      {
        "id": 3,
        "title": "错误率",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_server_requests_seconds_count{status=~\"5..\"}[5m]) / rate(http_server_requests_seconds_count[5m]) * 100",
            "legendFormat": "5xx错误率"
          },
          {
            "expr": "rate(http_server_requests_seconds_count{status=~\"4..\"}[5m]) / rate(http_server_requests_seconds_count[5m]) * 100",
            "legendFormat": "4xx错误率"
          }
        ],
        "yAxes": [
          {
            "label": "错误率(%)",
            "min": 0,
            "max": 100
          }
        ]
      },
      {
        "id": 4,
        "title": "数据库连接数",
        "type": "graph",
        "targets": [
          {
            "expr": "mysql_global_status_threads_connected",
            "legendFormat": "当前连接数"
          },
          {
            "expr": "mysql_global_variables_max_connections",
            "legendFormat": "最大连接数"
          }
        ],
        "yAxes": [
          {
            "label": "连接数",
            "min": 0
          }
        ]
      },
      {
        "id": 5,
        "title": "Redis 内存使用",
        "type": "graph",
        "targets": [
          {
            "expr": "redis_memory_used_bytes",
            "legendFormat": "已使用内存"
          },
          {
            "expr": "redis_memory_max_bytes",
            "legendFormat": "最大内存"
          }
        ],
        "yAxes": [
          {
            "label": "内存(bytes)",
            "min": 0
          }
        ]
      },
      {
        "id": 6,
        "title": "系统资源使用",
        "type": "graph",
        "targets": [
          {
            "expr": "100 - (avg by(instance) (irate(node_cpu_seconds_total{mode=\"idle\"}[5m])) * 100)",
            "legendFormat": "CPU使用率"
          },
          {
            "expr": "(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100",
            "legendFormat": "内存使用率"
          }
        ],
        "yAxes": [
          {
            "label": "使用率(%)",
            "min": 0,
            "max": 100
          }
        ]
      }
    ],
    "time": {
      "from": "now-1h",
      "to": "now"
    },
    "refresh": "30s"
  }
}
```

## 📝 ELK Stack 日志配置

### logstash.conf
```ruby
input {
  beats {
    port => 5044
  }
}

filter {
  if [fields][service] == "shopx-backend" {
    grok {
      match => { "message" => "%{TIMESTAMP_ISO8601:timestamp} \[%{DATA:thread}\] %{LOGLEVEL:level} %{DATA:logger} - %{GREEDYDATA:message}" }
    }
    
    date {
      match => [ "timestamp", "yyyy-MM-dd HH:mm:ss" ]
    }
    
    if [message] =~ /ERROR/ {
      mutate {
        add_tag => [ "error" ]
      }
    }
    
    if [message] =~ /WARN/ {
      mutate {
        add_tag => [ "warning" ]
      }
    }
  }
  
  if [fields][service] == "nginx" {
    grok {
      match => { "message" => "%{COMBINEDAPACHELOG}" }
    }
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "shopx-logs-%{+YYYY.MM.dd}"
  }
}
```

### filebeat.yml
```yaml
filebeat.inputs:
  - type: log
    enabled: true
    paths:
      - /var/log/shopx/*.log
    fields:
      service: shopx-backend
    fields_under_root: true
    multiline.pattern: '^\d{4}-\d{2}-\d{2}'
    multiline.negate: true
    multiline.match: after

  - type: log
    enabled: true
    paths:
      - /var/log/nginx/*.log
    fields:
      service: nginx
    fields_under_root: true

output.logstash:
  hosts: ["logstash:5044"]

processors:
  - add_host_metadata:
      when.not.contains.tags: forwarded
```

## 🔔 AlertManager 配置

### alertmanager.yml
```yaml
global:
  smtp_smarthost: 'localhost:587'
  smtp_from: 'alerts@shopx.com'

route:
  group_by: ['alertname']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 1h
  receiver: 'web.hook'

receivers:
  - name: 'web.hook'
    webhook_configs:
      - url: 'http://localhost:5001/'

  - name: 'email'
    email_configs:
      - to: 'admin@shopx.com'
        subject: 'ShopX 告警: {{ .GroupLabels.alertname }}'
        body: |
          {{ range .Alerts }}
          告警: {{ .Annotations.summary }}
          描述: {{ .Annotations.description }}
          时间: {{ .StartsAt }}
          {{ end }}

  - name: 'slack'
    slack_configs:
      - api_url: 'https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK'
        channel: '#alerts'
        title: 'ShopX 告警'
        text: |
          {{ range .Alerts }}
          *告警*: {{ .Annotations.summary }}
          *描述*: {{ .Annotations.description }}
          *时间*: {{ .StartsAt }}
          {{ end }}
```

## 🐳 Docker Compose 监控服务

### docker-compose.monitoring.yml
```yaml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - ./monitoring/shopx_rules.yml:/etc/prometheus/shopx_rules.yml
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--web.enable-lifecycle'

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3001:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana-storage:/var/lib/grafana
      - ./monitoring/dashboard.json:/var/lib/grafana/dashboards/shopx.json

  alertmanager:
    image: prom/alertmanager:latest
    container_name: alertmanager
    ports:
      - "9093:9093"
    volumes:
      - ./monitoring/alertmanager.yml:/etc/alertmanager/alertmanager.yml

  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.15.0
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
    volumes:
      - elasticsearch-storage:/usr/share/elasticsearch/data

  kibana:
    image: docker.elastic.co/kibana/kibana:7.15.0
    container_name: kibana
    ports:
      - "5601:5601"
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200

  logstash:
    image: docker.elastic.co/logstash/logstash:7.15.0
    container_name: logstash
    ports:
      - "5044:5044"
    volumes:
      - ./monitoring/logstash.conf:/usr/share/logstash/pipeline/logstash.conf

  filebeat:
    image: docker.elastic.co/beats/filebeat:7.15.0
    container_name: filebeat
    user: root
    volumes:
      - ./monitoring/filebeat.yml:/usr/share/filebeat/filebeat.yml
      - /var/log:/var/log:ro
      - /var/lib/docker/containers:/var/lib/docker/containers:ro

  mysql-exporter:
    image: prom/mysqld-exporter:latest
    container_name: mysql-exporter
    ports:
      - "9104:9104"
    environment:
      - DATA_SOURCE_NAME=root:password@(mysql:3306)/shopx

  redis-exporter:
    image: oliver006/redis_exporter:latest
    container_name: redis-exporter
    ports:
      - "9121:9121"
    environment:
      - REDIS_ADDR=redis://redis:6379

  node-exporter:
    image: prom/node-exporter:latest
    container_name: node-exporter
    ports:
      - "9100:9100"
    volumes:
      - /proc:/host/proc:ro
      - /sys:/host/sys:ro
      - /:/rootfs:ro
    command:
      - '--path.procfs=/host/proc'
      - '--path.rootfs=/rootfs'
      - '--path.sysfs=/host/sys'
      - '--collector.filesystem.mount-points-exclude=^/(sys|proc|dev|host|etc)($$|/)'

volumes:
  grafana-storage:
  elasticsearch-storage:
```

## 📊 监控指标说明

### 应用指标
- **http_server_requests_seconds**: HTTP请求响应时间
- **http_server_requests_seconds_count**: HTTP请求总数
- **jvm_memory_used_bytes**: JVM内存使用量
- **jvm_gc_pause_seconds**: GC暂停时间
- **hikaricp_connections_active**: 数据库连接池活跃连接数

### 系统指标
- **node_cpu_seconds_total**: CPU使用时间
- **node_memory_MemTotal_bytes**: 总内存
- **node_memory_MemAvailable_bytes**: 可用内存
- **node_filesystem_size_bytes**: 文件系统大小
- **node_filesystem_avail_bytes**: 文件系统可用空间

### 数据库指标
- **mysql_global_status_threads_connected**: MySQL连接数
- **mysql_global_variables_max_connections**: MySQL最大连接数
- **mysql_global_status_queries**: MySQL查询数
- **mysql_global_status_slow_queries**: MySQL慢查询数

### Redis指标
- **redis_memory_used_bytes**: Redis内存使用量
- **redis_memory_max_bytes**: Redis最大内存
- **redis_connected_clients**: Redis连接客户端数
- **redis_commands_processed_total**: Redis处理命令总数

## 🚀 部署说明

### 1. 启动监控服务
```bash
docker-compose -f docker-compose.monitoring.yml up -d
```

### 2. 访问监控界面
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3001 (admin/admin)
- **Kibana**: http://localhost:5601
- **AlertManager**: http://localhost:9093

### 3. 配置告警
1. 在Grafana中导入仪表板
2. 配置AlertManager告警规则
3. 设置邮件和Slack通知

### 4. 日志收集
1. 配置Filebeat收集应用日志
2. 在Kibana中创建索引模式
3. 设置日志分析和可视化

## 📈 性能优化建议

### 1. 监控优化
- 设置合适的采集间隔
- 配置数据保留策略
- 优化告警规则避免告警风暴

### 2. 日志优化
- 使用结构化日志格式
- 设置日志级别过滤
- 定期清理历史日志

### 3. 告警优化
- 设置合理的告警阈值
- 配置告警抑制规则
- 建立告警升级机制

---

**ShopX 监控和日志系统配置完成！**

现在ShopX项目具备了完整的监控和日志系统，包括Prometheus监控、Grafana可视化、ELK日志分析、AlertManager告警等功能，能够全面监控系统运行状态并及时发现和处理问题。
