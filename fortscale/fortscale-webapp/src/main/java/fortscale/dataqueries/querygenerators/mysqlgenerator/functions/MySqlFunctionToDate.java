package fortscale.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

import org.springframework.stereotype.Component;

/**
 * TO_DATE function generator for fields
 */
@Component
public class MySqlFunctionToDate extends MySqlFieldFunction {
    public String generateSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        String entityId = field.getEntity();
        if (entityId == null)
            entityId = dataQueryDTO.entities[0];

        if (field.getId() == null)
        throw new InvalidQueryException("The to_date field function requires a field ID.");

        return "TO_DATE(" + dataEntitiesConfig.getFieldColumn(entityId, field.getId()) + ")";
    }
}
