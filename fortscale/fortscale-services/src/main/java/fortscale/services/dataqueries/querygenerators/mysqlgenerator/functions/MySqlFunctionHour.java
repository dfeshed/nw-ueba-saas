package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.stereotype.Component;

/**
 * HOUR function generator for fields
 */
@Component
public class MySqlFunctionHour extends MySqlFieldFunction {
    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        String sqlFunctionName = field.getFunc().getName().toString().toUpperCase();

        StringBuilder sb = new StringBuilder();
        String entityId = field.getEntity();
        if (entityId == null)
            entityId = dataQueryDtoHelper.getEntityId(dataQueryDTO);

        sb.append(sqlFunctionName);
        sb.append("(");

        sb.append(getFieldName(field, dataQueryDTO));
        
        sb.append(") ");
        return sb.toString();
    }
}
