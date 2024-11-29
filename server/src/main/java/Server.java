import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server
 * Main program running on server
 * @version 1.1 2024-11-28
 * @author Holmes Amzish
 */
public class Server {
    private static final int TCP_PORT = 7777;
    private static final int UDP_PORT = 8888;

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private static final ExecutorService tcpExecutor = Executors.newCachedThreadPool();
    private static final LampStatusService lampService = new LampStatusService(MyBatisUtils.getSqlSessionFactory());

    public static void main(String[] args) {
        try {
            System.out.println("Starting Virtual Lamp System Control Server...");

            // Listen UDP package
            new Thread(new UDPReceiver()).start();

            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            logger.info("TCP server listening: {}", TCP_PORT);
            new Thread(() -> listenForTCPConnections(serverSocket)).start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                //System.out.print("Control server -> ");
                String command = scanner.nextLine();
                handleCommand(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void listenForTCPConnections(ServerSocket serverSocket) {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                tcpExecutor.execute(new TCPHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleCommand(String command) {
        String[] cmdParts = command.split(" ");
        try {
            switch (cmdParts[0]) {
                case "help":
                    System.out.print(String.format("""
                            help                    # Show all command
                            switch on/off <device>  # Turn on/off target device
                            history <device>        # Show history of device
                            disconnect <device>     # Disconnect from target device
                            exit                    # Shutdown the server
                            """));
                    break;
//                case "status":
//                    if (cmdParts.length < 2) throw new IllegalArgumentException("");
//                    displayLampStatus(cmdParts[1]);
//                    break;
                case "switch":
                    if (cmdParts.length < 3) throw new IllegalArgumentException("Missing argument(s)");
                    switchLamp(cmdParts[1], cmdParts[2]);
                    break;
                case "history":
                    if (cmdParts.length < 2) throw new IllegalArgumentException("Device ID required");
                    historyLampStatus(cmdParts[1]);
                    break;
                case "disconnect":
                    if (cmdParts.length < 2) throw new IllegalArgumentException("Device ID required");
                    disconnectLamp(cmdParts[1]);
                    break;
                case "exit":
                    System.out.println("Shutting down...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Unknow command: " + cmdParts[0]);
            }
        } catch (Exception e) {
            System.out.println("Invalid command: " + e.getMessage());
        }
    }

//    private static void displayLampStatus(String lampId) {
//        System.out.println("Querying " + lampId + "...");
//        LampStatus status = lampService.getCurrentStatus(lampId);
//        if (status != null) {
//            System.out.println("Status: " + status);
//        } else {
//            System.out.println("No record found " + lampId);
//        }
//    }

    private static void switchLamp(String action, String lampId) {
        System.out.println("Sending command to " + lampId + " " + action);
        // TCPHandler
        TCPHandler.sendCommand(lampId, "switch " + action);
    }

    private static void historyLampStatus(String lampId) {
        System.out.println("Querying " + lampId + " status...");
        var history = lampService.getHistory(lampId);
        if (history.isEmpty()) {
            System.out.println("No record for " + lampId);
        } else {
            System.out.printf("|%-20s|%-8s|%-12s|%-9s|%-12s|%-7s|\n", "Date time", "Lamp ID", "Temperature", "Humidity", "Illuminance", "Status");
            for (var status : history) {
                System.out.printf(status.toLine());
            }
        }
    }

    private static void disconnectLamp(String lampId) {
        System.out.println("Disconnecting from " + lampId + "...");
        TCPHandler.disconnect(lampId);
    }
}