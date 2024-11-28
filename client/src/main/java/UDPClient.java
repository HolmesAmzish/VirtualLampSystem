import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UDPClient
 * UDP Client to send lamp status updates to the server.
 * @version 1.0 2024-11-28
 */
public class UDPClient {
    private static final String SERVER_HOST = "localhost"; // Server address
    private static final int SERVER_PORT = 8888; // Server port for UDP
    private static final String DEVICE_ID = "lamp_02"; // Device ID

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            while (true) {
                // Simulate periodic data (temperature, humidity, etc.)
                LampEntity lampEntity = new LampEntity(DEVICE_ID);

                // Create the message string
                String message = String.format("%s,%s,%.1f,%d,%d,%s",
                        lampEntity.getLampId(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")),
                        lampEntity.getTemperature(),
                        lampEntity.getHumidity(),
                        lampEntity.getIlluminance(),
                        lampEntity.getStatus());

                // Send UDP message
                byte[] data = message.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(SERVER_HOST), SERVER_PORT);
                socket.send(packet);
                System.out.println("Sent UDP message: " + message);

                // Sleep for 5 seconds before sending the next message
                Thread.sleep(Duration.ofMinutes(10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
