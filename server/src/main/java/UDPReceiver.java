import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UDPReceiver
 * @version 1.1 2024-11-28
 * @author Holmes Amzish
 */
public class UDPReceiver implements Runnable {
    private static final int UDP_PORT = 8888;
    private static final LampStatusService lampService = new LampStatusService(MyBatisUtils.getSqlSessionFactory());

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                LampStatus status = parsePacket(packet.getData());
                if (status != null) {
                    // Insert received status record
                    lampService.insertLampStatus(status);

                    // Record received UDP package
                    logger.info("Lamp status received: {}", status.getDeviceId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LampStatus parsePacket(byte[] data) {
        try {
            // Split with comma
            String packetData = new String(data, 0, data.length).trim();
            String[] fields = packetData.split(",");

            if (fields.length != 6) {
                System.out.println("Invalid UDP package: " + packetData);
                return null;
            }

            String deviceId = fields[0];
            String timestampStr = fields[1];
            float temperature = Float.parseFloat(fields[2]);
            int humidity = Integer.parseInt(fields[3]);
            int illuminance = Integer.parseInt(fields[4]);
            String status = fields[5];

            // 将时间字符串转换为 LocalDateTime 对象
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);

            // 创建并返回 LampStatus 对象
            return new LampStatus(deviceId, timestamp, temperature, humidity, illuminance, status);
        } catch (Exception e) {
            System.out.println("Error while parse UDP package" + e.getMessage());
            return null;
        }
    }
}
