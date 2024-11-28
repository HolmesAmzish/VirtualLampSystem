import java.net.*;
import java.nio.ByteBuffer;
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

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // 解析 UDP 数据包
                LampStatus status = parsePacket(packet.getData());
                if (status != null) {
                    // 将数据保存到数据库
                    lampService.insertLampStatus(status);

                    // 打印状态
                    System.out.println("Lamp status recevied: " + status);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LampStatus parsePacket(byte[] data) {
        try {
            // 假设数据包使用逗号分隔
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
            System.out.println("解析UDP数据包时出错：" + e.getMessage());
            return null;
        }
    }
}
