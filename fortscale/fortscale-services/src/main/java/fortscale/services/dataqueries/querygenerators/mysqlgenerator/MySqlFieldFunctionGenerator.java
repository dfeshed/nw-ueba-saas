package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions.MySqlFunctionAggregate;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions.MySqlFunctionCount;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions.MySqlFunctionDateDiff;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions.MySqlFunctionToDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Yossi on 03/11/2014.
 */
@Component
public class MySqlFieldFunctionGenerator {


    @Autowired private MySqlFunctionCount mySqlFunctionCount;
    @Autowired private MySqlFunctionToDate mySqlFunctionToDate;
    @Autowired private MySqlFunctionAggregate mySqlFunctionAggregate;
    @Autowired private MySqlFunctionDateDiff mySqlFunctionDateDiff;


    /**
     * Generates SQL for a field function call, such as 'COUNT(*)' or 'MAX(eventscore)'
     * @param field
     * @param dataQueryDTO
     * @return
     */
    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException {
        switch(field.getFunc().getName()){
            case count: return mySqlFunctionCount.generateSql(field, dataQueryDTO);
            case to_date: return mySqlFunctionToDate.generateSql(field, dataQueryDTO);
            case min:
            case max:
            case avg:
            case sum: return mySqlFunctionAggregate.generateSql(field, dataQueryDTO);
            case datediff: return mySqlFunctionDateDiff.generateSql(field, dataQueryDTO);
            default:
                throw new InvalidQueryException(String.format("There's no implementation for field function %s.", field.getFunc().getName()));
        }
    }
}
