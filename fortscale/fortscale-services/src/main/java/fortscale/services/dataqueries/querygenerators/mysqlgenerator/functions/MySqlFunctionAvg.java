package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.stereotype.Component;

/**
 * COUNT function generator for fields
 */
@Component
public class MySqlFunctionAvg extends MySqlFieldFunction {
    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder();
        String entityId = field.getEntity();
        if (entityId == null)
            entityId = dataQueryDTO.getEntities()[0];

        if (field.getId() == null)
            throw new InvalidQueryException("The avg field function requires a field ID.");

        sb.append("AVG(");

        if (field.getFunc().getParams() != null)
            if (field.getFunc().getParams().containsKey("distinct"))
                sb.append("DISTINCT ");

        sb.append(dataEntitiesConfig.getFieldColumn(entityId, field.getId()));

        sb.append(")");
        return sb.toString();
    }
}
