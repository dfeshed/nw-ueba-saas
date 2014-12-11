package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;

import org.springframework.stereotype.Component;

/**
 * TO_DATE function generator for fields
 */
@Component
public class MySqlFunctionToDate extends MySqlFieldFunction {
    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{

        String sqlFunctionName = field.getFunc().getName().toString().toUpperCase();
        StringBuilder sb = new StringBuilder();
        String entityId = field.getEntity();

        if (entityId == null)
            entityId = dataQueryDTO.getEntities()[0];

        if (field.getId() == null)
            throw new InvalidQueryException("The " + sqlFunctionName + " field function requires a field ID.");


        sb.append(sqlFunctionName);
        sb.append("(");
        sb.append(dataEntitiesConfig.getFieldColumn(entityId, field.getId()));
        sb.append(")");

        return sb.toString();
    }
}
