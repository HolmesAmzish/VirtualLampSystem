import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * LampStatusService
 * Service layer for accessing and manipulating lamp status data in the database
 * @version 1.0 2024-11-27
 * @author Holmes
 */
public class LampStatusService {

    private final SqlSessionFactory sqlSessionFactory;

    // 构造函数，接受 SqlSessionFactory 参数
    public LampStatusService(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

//    public LampStatus getCurrentStatus(String lampId) {
//
//        return currentStatus;
//    }

    /**
     * 插入新的灯状态记录
     * @param lampStatus 路灯状态对象
     */
    public void insertLampStatus(LampStatus lampStatus) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            LampStatusMapper mapper = session.getMapper(LampStatusMapper.class);
            mapper.insertLampStatus(lampStatus);
            session.commit();
        } catch (Exception e) {
            System.err.println("插入路灯状态失败: " + e.getMessage());
        }
    }

    /**
     *
     * @param lampId device ID
     * @return List of LampStatus
     */
    public List<LampStatus> getHistory(String lampId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            LampStatusMapper mapper = session.getMapper(LampStatusMapper.class);
            return mapper.getLampStatusHistory(lampId);
        } catch (Exception e) {
            System.err.println("获取路灯历史状态失败: " + e.getMessage());
            return null;
        }
    }
}
