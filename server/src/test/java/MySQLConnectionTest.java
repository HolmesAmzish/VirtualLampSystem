import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnectionTest {
    public static void main(String[] args) {
        // 数据库连接信息
        String url = "jdbc:mysql://pur5.romules.top:3306/lamp_system"; // 替换为你的数据库URL
        String username = "cacc"; // 替换为你的MySQL用户名
        String password = "20230612"; // 替换为你的MySQL密码

        System.out.println("测试连接到 MySQL 数据库...");
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            if (connection != null) {
                System.out.println("成功连接到 MySQL 数据库！");
            } else {
                System.out.println("连接失败！");
            }
        } catch (SQLException e) {
            System.err.println("连接 MySQL 失败！");
            e.printStackTrace();
        }
    }
}

