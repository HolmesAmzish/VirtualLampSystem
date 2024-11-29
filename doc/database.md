创建数据库与数据表

```sql
CREATE DATABASE lamp_system;

USE lamp_system;

CREATE TABLE lamp_status (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lamp_id VARCHAR(50) NOT NULL,        -- 设备ID (lamp_xx)
    timestamp DATETIME NOT NULL,         -- 时间戳
    temperature FLOAT NOT NULL,          -- 温度 (摄氏度)
    humidity INT NOT NULL,               -- 湿度 (0-100)
    illuminance INT NOT NULL,            -- 照度 (0-100000)
    state BOOLEAN NOT NULL               -- 灯的状态 (开/关)
);
```