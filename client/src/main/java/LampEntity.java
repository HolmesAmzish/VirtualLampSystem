/**
 * LampEntity
 * @version 1.0 2024-11-28
 * @author Holmes Amzish
 */
public class LampEntity {
    private String lampId;
    private double temperature;
    private int humidity;
    private int illuminance;
    private String status; // ON or OFF

    public LampEntity(String lampId) {
        this.lampId = lampId;
        this.temperature = Math.random() * 40; // Example temperature
        this.humidity = (int) (Math.random() * 100);
        this.illuminance = (int) (Math.random() * 1000);
        this.status = "OFF";
    }

    // Getters and setters
    public String getLampId() {
        return lampId;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getIlluminance() {
        return illuminance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    public String toString() {
//        return String.format("|%-8s|%-12.1f|%-9d|%-12d|%-7s|",
//                this.getLampId(),
//                this.getTemperature(),
//                this.getHumidity(),
//                this.getIlluminance(),
//                this.getStatus());
//    }

    public void printStatus() {
        System.out.print(String.format("Lamp ID: %s Status: %s\n", this.getLampId(), this.getStatus())
        + String.format("Temperature: %.2f, Humidity: %d, Illuminance: %d\n", this.getTemperature(), this.getHumidity(), this.getIlluminance()));
    }
}
