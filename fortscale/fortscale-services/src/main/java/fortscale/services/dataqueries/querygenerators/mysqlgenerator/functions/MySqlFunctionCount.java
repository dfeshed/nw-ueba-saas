package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;

import org.springframework.stereotype.Component;

/**
 * COUNT function generator for fields
 */
@Component
public class MySqlFunctionCount extends MySqlFieldFunction {
    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        String sqlFunctionName = field.getFunc().getName().toString().toUpperCase();

        StringBuilder sb = new StringBuilder();
        String entityId = field.getEntity();
//        if (entityId == null)
//            entityId = dataQueryDtoHelper.getEntityId(dataQueryDTO);

        sb.append(sqlFunctionName);
        sb.append("(");

        if (field.getFunc().getParams() != null) {
            if (field.getFunc().getParams().containsKey("all"))
                sb.append("*");
            else {
				// Distinct
                if (field.getFunc().getParams().containsKey("distinct"))
                    sb.append("DISTINCT ");
				// table name (myTable.)
				if (entityId != null) {
					sb.append(dataEntitiesConfig.getFieldTable(entityId, field.getId()));
				}
				// field name
                sb.append(getFieldName(field, dataQueryDTO));
            }
        }
        else
            sb.append(getFieldName(field, dataQueryDTO));
        
        sb.append(")");
        return sb.toString();
    }
}
