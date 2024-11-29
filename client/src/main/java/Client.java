import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client
 * The virtual lamp device
 * @version 1.1 2024-11-29
 * @author Holmes Amzish
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private static final int TCP_PORT = 7777;
    private static final ExecutorService commandExecutor = Executors.newSingleThreadExecutor();

    // Initialize connection info
    static String serverAddress = "localhost";
    static String deviceId = "unknown_device";
    private static LampEntity lampEntity;

    public static void main(String[] args) {
        if (args.length == 2) {
            serverAddress = args[0];
            deviceId = args[1];
        } else {
            logger.error("Usage: java Client <server address> <device id>");
            System.exit(1);
        }
        lampEntity = new LampEntity(deviceId);

        TCPClient tcpClient = new TCPClient(serverAddress, TCP_PORT, lampEntity);

        new Thread(() -> {
            try {
                UDPSender.startUDPThread(serverAddress, lampEntity);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        // Command Listener
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            handleCommand(command);
        }
    }



    /**
     * Handle incoming commands from the user.
     * @param command the user input command
     */
    private static void handleCommand(String command) {
        switch (command) {
            case "help":
                System.out.print(String.format("""
                        switch on/off       # Turn on/off the lamp
                        status              # Show status of lamp
                        update              # Update status to server actively
                        """));
                break;
            case "switch on":
                lampEntity.setStatus("ON");
                logger.info("Lamp status set \"On\"");
                break;
            case "switch off":
                lampEntity.setStatus("OFF");
                logger.info("Lamp status set \"Off\"");
                break;
            case "status":
                lampEntity.printStatus();
                break;
            case "update":
                UDPSender.sendData(serverAddress, lampEntity);
                break;
            default: System.out.println("Unknown command: " + command);
        }
    }
}
