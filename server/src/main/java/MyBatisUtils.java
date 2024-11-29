import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * MyBatisUtils
 * Load config of MyBatis framework
 * @version 1.0 2024-11-26
 * @author Holmes Amzish
 */
public class MyBatisUtils {

    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            // 加载 MyBatis 配置文件
            InputStream inputStream = MyBatisUtils.class.getResourceAsStream("/mybatis-config.xml");
            // 使用 SqlSessionFactoryBuilder 创建 SqlSessionFactory
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize MyBatis SqlSessionFactory", e);
        }
    }

    // 获取 SqlSessionFactory 实例
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
