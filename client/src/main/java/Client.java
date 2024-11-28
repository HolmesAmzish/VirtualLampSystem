import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client
 * @version 1.0 2024-11-28
 * @author Holmes Amzish
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client
 * @version 1.1 2024-11-28
 * @author Holmes Amzish
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private static final String DEFAULT_SERVER = "localhost";
    private static final int UDP_PORT = 8888;
    private static final ExecutorService commandExecutor = Executors.newSingleThreadExecutor();

    private static TCPClient tcpClient;
    private static LampEntity lampEntity;

    public static void main(String[] args) {
        String serverAddress = DEFAULT_SERVER;
        String deviceId = "unknown_device";

        if (args.length == 2) {
            serverAddress = args[0];
            deviceId = args[1];
        } else {
            serverAddress = DEFAULT_SERVER;
        }

        lampEntity = new LampEntity(deviceId);
        tcpClient = new TCPClient();  // Establish a single TCP connection

        // Start the UDP thread to periodically send status
        String finalServerAddress = serverAddress;
        new Thread(() -> startUDPThread(finalServerAddress)).start();

        // Command Listener
        Scanner scanner = new Scanner(System.in);
        while (true) {
            //System.out.print("Control server -> ");
            String command = scanner.nextLine();
            handleCommand(command);
        }
    }

    /**
     * Start the UDP thread to send lamp status periodically.
     */
    private static void startUDPThread(String serverAddress) {
        try (DatagramSocket socket = new DatagramSocket()) {
            while (true) {
                String message = String.format("%s,%s,%.1f,%d,%d,%s",
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

                // Wait 10 minutes before sending the next update
                Thread.sleep(Duration.ofMinutes(10).toMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the command listener to handle user input.
     */
    private static void startCommandListener() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                //System.out.printf("Client %s: ", lampEntity.getLampId());
                String command = scanner.nextLine().trim();
                handleCommand(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle incoming commands from the user.
     * @param command the user input command
     */
    private static void handleCommand(String command) {
        if (command.equalsIgnoreCase("switch on")) {
            lampEntity.setStatus("ON");
            logger.info("Lamp status set \"On\"");
        } else if (command.equalsIgnoreCase("switch off")) {
            lampEntity.setStatus("OFF");
            logger.info("Lamp status set \"Off\"");
        } else if (command.equalsIgnoreCase("status")) {
            lampEntity.printStatus();
        } else {
            System.out.println("Invalid command.");
        }
    }
}
