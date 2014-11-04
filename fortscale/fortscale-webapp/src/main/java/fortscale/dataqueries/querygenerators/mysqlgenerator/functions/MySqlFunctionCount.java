package fortscale.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.dataqueries.DataEntitiesConfig;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Yossi on 04/11/2014.
 */
@Component
public class MySqlFunctionCount implements MySqlFieldFunction {
    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

    public String generateSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder();
        String entityId = field.getEntity();
        if (entityId == null)
            entityId = dataQueryDTO.entities[0];

        sb.append("COUNT(");
        if (field.func.params.containsKey("all"))
            sb.append("*");
        else{
            if (field.func.params.containsKey("distinct"))
                sb.append("DISTINCT ");

            sb.append(dataEntitiesConfig.getFieldColumn(entityId, field.getId()));
        }

        sb.append(")");
        return sb.toString();
    }
}
