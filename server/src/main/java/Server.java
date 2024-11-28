import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server
 * Main program running on server
 * @version 1.0 2024-11-26
 * @author Holmes Amzish
 */
public class Server {
    private static final int TCP_PORT = 7777;
    private static final int UDP_PORT = 8888;

    private static final ExecutorService tcpExecutor = Executors.newCachedThreadPool();
    private static final LampStatusService lampService = new LampStatusService(MyBatisUtils.getSqlSessionFactory());

    public static void main(String[] args) {
        try {
            System.out.println("Starting Virtual Lamp System Control Server...");

            // Listen UDP package
            new Thread(new UDPReceiver()).start();

            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("TCP server listening: " + TCP_PORT);
            new Thread(() -> listenForTCPConnections(serverSocket)).start();

            // 启动命令行监听
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Control server -> ");
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
//                case "status":
//                    if (cmdParts.length < 2) throw new IllegalArgumentException("缺少设备ID");
//                    displayLampStatus(cmdParts[1]);
//                    break;
                case "switch":
                    if (cmdParts.length < 3) throw new IllegalArgumentException("缺少开关动作或设备ID");
                    switchLamp(cmdParts[1], cmdParts[2]);
                    break;
                case "history":
                    if (cmdParts.length < 2) throw new IllegalArgumentException("缺少设备ID");
                    historyLampStatus(cmdParts[1]);
                    break;
                case "disconnect":
                    if (cmdParts.length < 2) throw new IllegalArgumentException("缺少设备ID");
                    disconnectLamp(cmdParts[1]);
                    break;
                case "exit":
                    System.out.println("关闭服务器...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("未知命令!");
            }
        } catch (Exception e) {
            System.out.println("命令错误: " + e.getMessage());
        }
    }

//    private static void displayLampStatus(String lampId) {
//        System.out.println("查询 " + lampId + " 的状态...");
//        LampStatus status = lampService.getCurrentStatus(lampId);
//        if (status != null) {
//            System.out.println("设备状态: " + status);
//        } else {
//            System.out.println("未找到设备 " + lampId + " 的状态记录");
//        }
//    }

    private static void switchLamp(String action, String lampId) {
        System.out.println("向设备 " + lampId + " 发送命令: " + action);
        // TCPHandler 可通过维护的连接列表发送命令到客户端
        TCPHandler.sendCommand(lampId, "switch " + action);
    }

    private static void historyLampStatus(String lampId) {
        System.out.println("Querying " + lampId + " status...");
        var history = lampService.getHistory(lampId);
        if (history.isEmpty()) {
            System.out.println("No record for " + lampId);
        } else {
            // 表头
            System.out.printf("|%-17s|%-8s|%-12s|%-9s|%-12s|%-7s|\n", "Date time", "Lamp ID", "Temperature", "Humidity", "Illuminance", "Status");

            // 遍历历史记录并格式化输出
            for (var status : history) {
                System.out.printf("|%-17s|%-8s|%-12.1f|%-9d|%-12d|%-7s|\n",
                        status.getTimestamp().toString(),
                        status.getDeviceId(),
                        status.getTemperature(),
                        status.getHumidity(),
                        status.getIlluminance(),
                        status.getStatus());
            }
        }
    }


    private static void disconnectLamp(String lampId) {
        System.out.println("Disconnecting from " + lampId + "...");
        TCPHandler.disconnect(lampId);
    }
}