import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * LampStatusService
 * Service layer for accessing and manipulating lamp status data in the database
 * @version 1.0 2024-11-27
 * @author Holmes
 */
public class LampStatusService {

    private final SqlSessionFactory sqlSessionFactory;
    private final static Logger logger = LoggerFactory.getLogger(LampStatusService.class);

    // Constructor
    public LampStatusService(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

//    public LampStatus getCurrentStatus(String lampId) {
//
//        return currentStatus;
//    }

    /**
     * Insert new record to database
     * @param lampStatus
     */
    public void insertLampStatus(LampStatus lampStatus) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            LampStatusMapper mapper = session.getMapper(LampStatusMapper.class);
            mapper.insertLampStatus(lampStatus);
            session.commit();
        } catch (Exception e) {
            logger.warn("Failed to insert new record: " + e.getMessage());
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
            logger.warn("Failed to query history status: " + e.getMessage());
            return null;
        }
    }
}
