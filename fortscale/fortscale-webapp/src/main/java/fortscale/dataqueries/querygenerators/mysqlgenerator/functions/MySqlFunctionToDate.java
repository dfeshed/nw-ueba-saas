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
public class MySqlFunctionToDate implements MySqlFieldFunction {
    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

    public String generateSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        String entityId = field.getEntity();
        if (entityId == null)
            entityId = dataQueryDTO.entities[0];

        if (field.getId() == null)
        throw new InvalidQueryException("The to_date field function requires a field ID.");

        return "TO_DATE(" + dataEntitiesConfig.getFieldColumn(entityId, field.getId()) + ")";
    }
}
