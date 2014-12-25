package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.stereotype.Component;

/**
 * Aggregate similar functions.
 * This function can add sql query function generically,
 * such as avg, max, min, sum.
 * when one of the above functions being used, in the dataQuery, this function is being invoked in order
 * to return the SQL part of the function.
 *
 */
@Component
public class MySqlFunctionAggregate extends MySqlFieldFunction {

    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{

        String sqlFunctionName = field.getFunc().getName().toString().toUpperCase();

        StringBuilder sb = new StringBuilder();

        sb.append(sqlFunctionName);
        sb.append("(");

        if (field.getFunc().getParams() != null)
            if (field.getFunc().getParams().containsKey("distinct"))
                sb.append("DISTINCT ");

        String fieldName = getFieldName(field, dataQueryDTO);
        if (fieldName == null)
            throw new InvalidQueryException("The " + sqlFunctionName + " field function requires a field ID.");
        
        sb.append(fieldName);

        sb.append(")");
        return sb.toString();
    }


}


