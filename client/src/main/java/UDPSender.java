import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class UDPSender {

    final static int UDP_PORT = 8888;

    private final static Logger logger = LoggerFactory.getLogger(UDPSender.class);

    // Send UDP package
    public static void sendData(String serverAddress, LampEntity lampEntity) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = String.format("%s,%s,%.1f,%d,%d,%b",
                    lampEntity.getLampId(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")),
                    lampEntity.getTemperature(),
                    lampEntity.getHumidity(),
                    lampEntity.getIlluminance(),
                    lampEntity.getStatus());

            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(serverAddress), UDP_PORT);
            socket.send(packet);
            logger.info("Sent UDP message: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the UDP thread to send lamp status periodically.
     * Wait for 10 min to update status to server
     */
    public static void startUDPThread(String serverAddress, LampEntity lampEntity) throws InterruptedException {
        while (true) {
            UDPSender.sendData(serverAddress, lampEntity);
            Thread.sleep(Duration.ofMinutes(10).toMillis());
        }
    }
}
