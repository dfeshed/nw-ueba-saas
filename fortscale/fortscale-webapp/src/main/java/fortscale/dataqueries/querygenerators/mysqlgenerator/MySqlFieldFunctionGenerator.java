package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.DataEntitiesConfig;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.dataqueries.querygenerators.mysqlgenerator.functions.MySqlFunctionCount;
import fortscale.dataqueries.querygenerators.mysqlgenerator.functions.MySqlFunctionToDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Yossi on 03/11/2014.
 */
@Component
public class MySqlFieldFunctionGenerator {
    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

    @Autowired private MySqlFunctionCount mySqlFunctionCount;
    @Autowired private MySqlFunctionToDate mySqlFunctionToDate;

    /**
     * Generates SQL for a field function call, such as 'COUNT(*)' or 'MAX(eventscore)'
     * @param field
     * @param dataQueryDTO
     * @return
     */
    public String generateSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException {
        switch(field.func.name){
            case count: return mySqlFunctionCount.generateSql(field, dataQueryDTO);
            case to_date: return mySqlFunctionToDate.generateSql(field, dataQueryDTO);
            case min:
            case max:
            default:
                throw new InvalidQueryException("There's no implementation for field function " + field.func.name + ".");
        }
    }
}
