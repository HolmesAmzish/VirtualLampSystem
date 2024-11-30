创建数据库与数据表

```sql
CREATE TABLE lamp_status (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,    -- 自增主键
    lamp_id VARCHAR(50) NOT NULL,                  -- 路灯ID
    timestamp DATETIME NOT NULL,                   -- 时间戳
    temperature FLOAT NOT NULL,                    -- 温度
    humidity INT NOT NULL,                         -- 湿度
    illuminance INT NOT NULL,                      -- 照度
    status VARCHAR(20) NOT NULL                    -- 路灯状态
);
```