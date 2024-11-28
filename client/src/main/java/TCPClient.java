import java.io.*;
import java.net.*;

/**
 * TCPClient
 * @version 1.0 2024-11-28
 * @author Holmes Amzish
 */
public class TCPClient {
    private static final String SERVER_HOST = "localhost"; // Server address
    private static final int SERVER_PORT = 7777; // Server port

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public TCPClient() {
        try {
            // Connect to the server
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to register the device (send device ID)
    public void registerDevice(String deviceId) {
        try {
            // Send device ID to the server
            out.println(deviceId);
            System.out.println("Sent device ID: " + deviceId);

            // Wait for acknowledgment from server
            String response = in.readLine();
            System.out.println("Server response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle control commands (turn on/off)
    public void sendCommand(String command) {
        try {
            out.println(command);
            String response = in.readLine();
            System.out.println("Server response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TCPClient client = new TCPClient();

        // Register the device with the server
        client.registerDevice("lamp_01");

        // Simulate sending a control command (e.g., turning the lamp ON)
        client.sendCommand("switch ON");

        // Simulate sending a control command (e.g., turning the lamp OFF)
        client.sendCommand("switch OFF");
    }
}
