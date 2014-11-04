package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.DataEntitiesConfig;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Yossi on 03/11/2014.
 */
@Component
public class MySqlFieldFunctionGenerator {
    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

    /**
     * Generates SQL for a field function call, such as 'COUNT(*)' or 'MAX(eventscore)'
     * @param field
     * @param dataQueryDTO
     * @return
     */
    public String generateSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException {
        StringBuilder sb = new StringBuilder();
        String entityId = field.getEntity();
        if (entityId == null)
            entityId = dataQueryDTO.entities[0];

        switch(field.func.name){
            case count:
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
            case to_date:
                if (field.getId() == null)
                    throw new InvalidQueryException("The to_date field function requires a field ID.");

                return "TO_DATE(" + dataEntitiesConfig.getFieldColumn(entityId, field.getId()) + ")";
            case min:
            case max:
            default:
                throw new InvalidQueryException("There's no implementation for field function " + field.func.name + ".");
        }
    }
}
