import java.io.*;
import java.net.*;

/**
 * Client
 * @version 1.0 2024-11-28
 * @author Holmes Amzish
 */
public class Client {
    public static void main(String[] args) {
        String deviceId;
        String serverAddress = "localhost";

        if (args.length >= 1) {
            deviceId = args[0];
            serverAddress = args[1];
        } else {
            deviceId = "Unknown";
            System.out.println("Usage: java Client <deviceId> <serverAddress>");
            System.exit(1);
        }

        // Create the TCP client thread
        Thread tcpThread = new Thread(() -> {
            TCPClient tcpClient = new TCPClient();
            tcpClient.registerDevice(deviceId);
            tcpClient.sendCommand("switch ON");
        });

        // Create the UDP client thread
        Thread udpThread = new Thread(() -> {
            UDPClient.main(args);  // Call the main method of UDPClient
        });

        // Start both threads
        tcpThread.start();
        udpThread.start();
    }
}

