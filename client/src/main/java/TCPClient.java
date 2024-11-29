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
        }
    }

    // Method to handle control commands (turn on/off)
    private void listenForCommands() {
        try {
            String command;
            while ((command = in.readLine()) != null) {
                if (command.equalsIgnoreCase("switch on")) {
                    lampEntity.setStatus("On");
                    logger.info("Lamp set to ON.");
                } else if (command.equalsIgnoreCase("switch off")) {
                    lampEntity.setStatus("Off");
                    logger.info("Lamp set to OFF.");
                } else {
                    System.out.println("Unknown command: " + command);
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server.");
        }
    }
}
