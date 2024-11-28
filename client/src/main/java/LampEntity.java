import java.time.LocalDateTime;

/**
 * LampEntity
 * @version 1.0 2024-11-28
 * @author Holmes Amzish
 */
public class LampEntity {
    private String lampId;
    private float temperature;
    private int humidity;
    private int illuminance;
    private String status;

    LampEntity(String lampId) {
        this.lampId = lampId;
        this.temperature = 0.0f;
        this.humidity = 0;
        this.illuminance = 0;
        this.status = "Off";
    }

    public String getLampId() {
        return lampId;
    }

    public void setLampId(String lampId) {
        this.lampId = lampId;
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

    // Constructor, getters, and setters
}
