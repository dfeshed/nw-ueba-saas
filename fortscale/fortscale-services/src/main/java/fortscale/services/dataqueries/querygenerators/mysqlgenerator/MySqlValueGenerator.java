package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.QueryValueType;
import fortscale.utils.TimestampUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yossi on 03/11/2014.
 * Service for validating and generating a value in an SQL query according to a value from a DTO
 */
@Component
public class MySqlValueGenerator {
    final static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String generateSql(String value, QueryValueType type){
        if (value == null)
            return "null";

        if (type == null)
            return value;

        switch (type){
            case STRING:
                return getStringValue(value);
            case SELECT:
            return getStringValue(value);
            case DATE_TIME:
                long timestamp = new Long(value);
                Date date = new Date(TimestampUtils.convertToMilliSeconds(timestamp));
                String formattedDate = DATE_TIME_FORMAT.format(date);
                return getStringValue(formattedDate);
            default:
                return value;
        }
    }

    private String getStringValue(String str){
        return new StringBuilder("\"").append(str).append("\"").toString();
    }
}
