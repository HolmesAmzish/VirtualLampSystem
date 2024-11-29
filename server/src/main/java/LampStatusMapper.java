import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LampStatusMapper
 * @version 1.0 2024-11-28
 * @author Holmes Amzish
 */
public interface LampStatusMapper {

    @Insert("INSERT INTO lamp_status (lamp_id, timestamp, temperature, humidity, illuminance, status) " +
            "VALUES (#{lampId}, #{timestamp}, #{temperature}, #{humidity}, #{illuminance}, #{status})")
    void insertLampStatus(LampStatus lampStatus);

    @Select("SELECT lamp_id, timestamp, temperature, humidity, illuminance, status FROM lamp_status WHERE lamp_id = #{lampId} ORDER BY timestamp DESC LIMIT 10")
    @Results({
            @Result(property = "lampId", column = "lamp_id"),
            @Result(property = "timestamp", column = "timestamp", javaType = LocalDateTime.class),
            @Result(property = "temperature", column = "temperature"),
            @Result(property = "humidity", column = "humidity"),
            @Result(property = "illuminance", column = "illuminance"),
            @Result(property = "status", column = "status")
    })
    List<LampStatus> getLampStatusHistory(String lampId);

}
