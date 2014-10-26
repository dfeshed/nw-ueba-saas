package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.dataqueries.DataQueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate the "from" part of the query in MySql
 */
@Component
public class MySqlFromPartGenerator implements QueryPartGenerator {
    @Autowired
    private MySqlUtils mySqlUtils;

    @Autowired
    DataQueryUtils dataQueryUtils;

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        try {
            String entityId = dataQueryDTO.entities[0];
            String tableName = dataQueryDTO.conditions != null && isHighScore(entityId, dataQueryDTO.conditions.get(0))
                    ? dataQueryUtils.getEntityPerformanceTable(entityId)
                    : dataQueryUtils.getEntityTable(entityId);

            // For now the generator supports only single table queries. When joins are supported, need to add the logic here.
            return "FROM " + tableName;
        }
        catch(Exception error){
            throw new InvalidQueryException("Can't create FROM part of the MySQL query. Error: " + error.getMessage());
        }
	}

    /**
     * Checks whether the DTO has a condition where the performance field (usually event_score) is >= the performance min value (usually 50).
     * This is pretty naive at the moment, since it doesn't take into account OR operators, but should be good enough for now.
     * @param term
     * @return
     * @throws Exception
     */
    private Boolean isHighScore(String entityId, DataQueryDTO.ConditionTerm term) throws Exception{
        if (term.terms == null || term.terms.size() == 0)
            return false;

        for (DataQueryDTO.Term childTerm: term.terms){
            if (childTerm.getClass() == DataQueryDTO.ConditionField.class){
                DataQueryDTO.ConditionField condition = (DataQueryDTO.ConditionField)childTerm;
                if (condition.field.getId().equals(dataQueryUtils.getEntityPerformanceTableField(entityId))){
                    int value = Integer.parseInt(condition.getValue());
                    if (value >= dataQueryUtils.getEntityPerformanceTableFieldMinValue(entityId) &&
                            (condition.operator == DataQueryDTO.Operator.equals || condition.operator == DataQueryDTO.Operator.greaterThan || condition.operator == DataQueryDTO.Operator.greaterThanOrEquals)){
                        return true;
                    }
                }
            }
            else if (isHighScore(entityId, (DataQueryDTO.ConditionTerm)childTerm))
                return true;
        }

        return false;
    }
}
