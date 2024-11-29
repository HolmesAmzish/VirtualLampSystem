import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.*;

/**
 * TCPClient
 * @version 1.0 2024-11-28
 * @author Holmes Amzish
 */
public class TCPClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private LampEntity lampEntity;
    private final Logger logger = LoggerFactory.getLogger(TCPClient.class);

    public TCPClient(String host, int port, LampEntity lampEntity) {
        try {
            this.lampEntity = lampEntity;
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            logger.info("Server connected: {}:{}", host, port);

            // Register device
            registerDevice(lampEntity.getLampId());

            // Start a thread to listen for commands
            new Thread(this::listenForCommands).start();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to connect to server");
        }
    }

    // Method to register the device (send device ID)
    private void registerDevice(String deviceId) {
        try {
            out.println(deviceId); // Send device ID to server
            logger.info("Sent device ID: " + deviceId);
            String response = in.readLine(); // Wait for server response
            logger.info("Server response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error registering device");
        }
    }

    // Method to handle control commands (turn on/off)
    private void listenForCommands() {
        try {
            String command;
            while ((command = in.readLine()) != null) {
                // Check if socket is closed before continuing
                if (socket.isClosed()) {
                    logger.error("Socket is closed. Exiting command listener.");
                    break;
                }

                if (command.equalsIgnoreCase("switch on")) {
                    lampEntity.setStatus(true);
                    logger.info("Lamp set to ON.");
                    logger.info("Lamp status: {}", lampEntity.getStatus());
                } else if (command.equalsIgnoreCase("switch off")) {
                    lampEntity.setStatus(false);
                    logger.info("Lamp set to OFF.");
                    logger.info("Lamp status: {}", lampEntity.getStatus());
                } else {
                    logger.info("Server message: " + command);
                }
            }
        } catch (IOException e) {
            logger.error("IOException occurred while listening for commands: {}", e.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();  // Ensure the socket is closed properly
                    logger.info("Socket closed.");
                }
            } catch (IOException e) {
                System.exit(1);
                logger.error("Error closing socket: {}", e.getMessage());
            }
        }
    }
}
