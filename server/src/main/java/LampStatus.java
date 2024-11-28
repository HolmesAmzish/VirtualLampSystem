import java.time.LocalDateTime;

/**
 * LampStatus
 * Entity of Lamp status information
 * @version 1.1 2024-11-28
 * @author Holmes Amzish
 */
public class LampStatus {

    private String lampId;        // 路灯的设备ID，lamp_xx
    private LocalDateTime timestamp; // 状态上传的时间
    private float temperature;      // 温度，摄氏度
    private int humidity;           // 湿度，0-100%
    private int illuminance;        // 照度，0-100000
    private String status;

    // 构造函数
    public LampStatus(String lampId, LocalDateTime timestamp,
                      float temperature, int humidity, int illuminance,
                      String status) {
        this.lampId = lampId;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.illuminance = illuminance;
        this.status = status;
    }

    // Getter and Setter 方法
    public String getDeviceId() {
        return lampId;
    }

    public void setDeviceId(String deviceId) {
        this.lampId = deviceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getIlluminance() {
        return illuminance;
    }

    public void setIlluminance(int illuminance) {
        this.illuminance = illuminance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LampStatus{" +
                "deviceId='" + lampId + '\'' +
                ", timestamp=" + timestamp +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", illuminance=" + illuminance +
                ", status=" + status +
                '}';
    }
}
