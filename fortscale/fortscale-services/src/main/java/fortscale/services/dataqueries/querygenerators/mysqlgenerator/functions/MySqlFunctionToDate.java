package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * TO_DATE function generator for fields
 */
@Component
public class MySqlFunctionToDate extends MySqlFieldFunction {
    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{

        String sqlFunctionName = field.getFunc().getName().toString().toUpperCase();


        StringBuilder sb = new StringBuilder();
        String entityId = field.getEntity();

        if (field.getId() == null)
            throw new InvalidQueryException("The " + sqlFunctionName + " field function requires a field ID.");

        String timeZone ="0";
        Map<String, String> params = field.getFunc().getParams();
        if(params != null && params.containsKey("timezone")){
            timeZone =  params.get("timezone");
        }

        sb.append(sqlFunctionName);
        sb.append("(");
        int tz =Integer.parseInt(timeZone);
        if(tz > 0) {
            sb.append("hours_sub");
        }
        else{ sb.append("hours_add");
        }
        sb.append("(");
        sb.append(getFieldName(field, dataQueryDTO)); // the date
        sb.append(",");
        sb.append(timeZone);
        sb.append(")");
        sb.append(")");

        return sb.toString();
    }
}
