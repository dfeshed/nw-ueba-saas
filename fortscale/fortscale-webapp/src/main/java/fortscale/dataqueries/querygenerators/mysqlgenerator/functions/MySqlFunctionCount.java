package fortscale.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querydto.DataQueryField;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

import org.springframework.stereotype.Component;

/**
 * COUNT function generator for fields
 */
@Component
public class MySqlFunctionCount extends MySqlFieldFunction {
    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder();
        String entityId = field.getEntity();
        if (entityId == null)
            entityId = dataQueryDTO.getEntities()[0];

        sb.append("COUNT(");
        if (field.getFunc().getParams().containsKey("all"))
            sb.append("*");
        else{
            if (field.getFunc().getParams().containsKey("distinct"))
                sb.append("DISTINCT ");

            sb.append(dataEntitiesConfig.getFieldColumn(entityId, field.getId()));
        }

        sb.append(")");
        return sb.toString();
    }
}
