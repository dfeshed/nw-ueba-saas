package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.QueryValueType;
import org.springframework.stereotype.Component;

/**
 * Created by Yossi on 03/11/2014.
 * Service for validating and generating a value in an SQL query according to a value from a DTO
 */
@Component
public class MySqlValueGenerator {
    public String generateSql(String value, QueryValueType type){
        if (value == null)
            return "null";

        switch (type){
            case STRING:
                StringBuilder sb = new StringBuilder("\"").append(value).append("\"");
                return sb.toString();
            default:
                return value;
        }
    }
}
