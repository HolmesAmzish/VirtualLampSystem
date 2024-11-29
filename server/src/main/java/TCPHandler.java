import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCPHandler
 * Handles individual TCP connections and provides utilities for managing client connections
 * @version 1.0 2024-11-27
 * @author Holmes
 */
public class TCPHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TCPHandler.class);
    private static final Map<String, Socket> connections = new ConcurrentHashMap<>(); // 存储设备ID和Socket映射
    private final Socket clientSocket;

    public TCPHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            // Register device
            String deviceId = registerDevice(reader, writer);
            if (deviceId == null) {
                return;
            }

            System.out.println("Device connected: " + deviceId);

            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Message from \"" + deviceId + "\" : " + message);
            }

        } catch (IOException e) {
            logger.warn("Error on connecting device: " + e.getMessage());
        } finally {
            disconnectDevice();
        }
    }

    /**
     * Register device with socket
     */
    private String registerDevice(BufferedReader reader, BufferedWriter writer) throws IOException {
        writer.write("Receive device id:\n");
        writer.flush();
        String deviceId = reader.readLine();

        if (deviceId == null || deviceId.isEmpty()) {
            System.out.println("Register failed, missing ID");
            writer.write("Register failed, device ID required\n");
            writer.flush();
            return null;
        }

        // Check existed device
        if (connections.containsKey(deviceId)) {
            System.out.println("Device ID " + deviceId + " already existed, connection refused");
            writer.write("Register failed, device id already existed\n");
            writer.flush();
            return null;
        }

        // Register device
        connections.put(deviceId, clientSocket);
        writer.write("Register success: device " + deviceId + "\n");
        writer.flush();
        return deviceId;
    }

    /**
     * Disconnect from device
     */
    private void disconnectDevice() {
        try {
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
            System.out.println("Error occurred in disconnection: " + e.getMessage());
        }
    }

    /**
     * Send command to local device to perform
     * @param deviceId target device id
     * @param command
     */
    public static void sendCommand(String deviceId, String command) {
        Socket socket = connections.get(deviceId);
        if (socket == null) {
            System.out.println("Lamp " + deviceId + " is not connected.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            writer.write(command + "\n");
            writer.flush();
            System.out.println("Command has been sent " + deviceId + ": " + command);
        } catch (IOException e) {
            logger.warn("Error occurred when sending command to " + deviceId + " " + e.getMessage());
        }
    }

    /**
     * 断开指定设备的连接
     * @param deviceId 目标设备ID
     */
    public static void disconnect(String deviceId) {
        Socket socket = connections.remove(deviceId);
        if (socket == null) {
            System.out.println("Device " + deviceId + " is not connected.");
            return;
        }

        try {
            socket.close();
            logger.info("Disconnected from " + deviceId);
        } catch (IOException e) {
            System.out.println("Error occurred disconnecting from " + deviceId + " " + e.getMessage());
        }
    }
}
