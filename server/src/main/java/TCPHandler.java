import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TCPHandler
 * Handles individual TCP connections and provides utilities for managing client connections
 * @version 1.0 2024-11-27
 * @author Holmes
 */
public class TCPHandler implements Runnable {
    private static final Map<String, Socket> connections = new ConcurrentHashMap<>(); // 存储设备ID和Socket映射
    private final Socket clientSocket;

    public TCPHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            // 注册设备
            String deviceId = registerDevice(reader, writer);
            if (deviceId == null) {
                return; // 设备注册失败，关闭连接
            }

            System.out.println("设备已连接: " + deviceId);

            // 持续监听设备消息
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("来自设备 " + deviceId + " 的消息: " + message);
                // 在这里可以处理设备发送的指令或状态更新
            }

        } catch (IOException e) {
            System.out.println("连接发生错误: " + e.getMessage());
        } finally {
            disconnectDevice();
        }
    }

    /**
     * 注册设备，将设备ID和Socket关联
     */
    private String registerDevice(BufferedReader reader, BufferedWriter writer) throws IOException {
        // 接收客户端发送的设备ID
        writer.write("请输入设备ID (如: lamp_01):\n");
        writer.flush();
        String deviceId = reader.readLine();

        if (deviceId == null || deviceId.isEmpty()) {
            System.out.println("设备注册失败，未提供设备ID");
            writer.write("注册失败: 未提供设备ID\n");
            writer.flush();
            return null;
        }

        // 检查是否已有同名设备连接
        if (connections.containsKey(deviceId)) {
            System.out.println("设备ID " + deviceId + " 已存在，拒绝连接");
            writer.write("注册失败: 设备ID已存在\n");
            writer.flush();
            return null;
        }

        // 注册设备
        connections.put(deviceId, clientSocket);
        writer.write("Register success: device " + deviceId + "\n");
        writer.flush();
        return deviceId;
    }

    /**
     * 断开设备连接，移除设备注册
     */
    private void disconnectDevice() {
        try {
            // 查找并移除断开的设备
            String deviceId = connections.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(clientSocket))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (deviceId != null) {
                connections.remove(deviceId);
                System.out.println("Disconnect from: " + deviceId);
            }
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("关闭连接时发生错误: " + e.getMessage());
        }
    }

    /**
     * 发送命令到设备
     * @param deviceId 目标设备ID
     * @param command  要发送的命令
     */
    public static void sendCommand(String deviceId, String command) {
        Socket socket = connections.get(deviceId);
        if (socket == null) {
            System.out.println("设备 " + deviceId + " 未连接");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            writer.write(command + "\n");
            writer.flush();
            System.out.println("已发送命令到设备 " + deviceId + ": " + command);
        } catch (IOException e) {
            System.out.println("发送命令到设备 " + deviceId + " 时发生错误: " + e.getMessage());
        }
    }

    /**
     * 断开指定设备的连接
     * @param deviceId 目标设备ID
     */
    public static void disconnect(String deviceId) {
        Socket socket = connections.remove(deviceId);
        if (socket == null) {
            System.out.println("设备 " + deviceId + " 未连接");
            return;
        }

        try {
            socket.close();
            System.out.println("已断开设备 " + deviceId);
        } catch (IOException e) {
            System.out.println("断开设备 " + deviceId + " 时发生错误: " + e.getMessage());
        }
    }
}
