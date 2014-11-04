package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.dataqueries.DataEntitiesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

/**
 * Generate the "from" part of the query in MySql
 */
@Component
public class MySqlFromPartGenerator implements QueryPartGenerator {
    @Autowired
    DataEntitiesConfig dataEntitiesConfig;

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        try {
            String entityId = dataQueryDTO.entities[0];
            String tableName = dataQueryDTO.conditions != null && isHighScore(entityId, dataQueryDTO.conditions)
                    ? dataEntitiesConfig.getEntityPerformanceTable(entityId)
                    : dataEntitiesConfig.getEntityTable(entityId);

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
            if (childTerm instanceof DataQueryDTO.ConditionField){
                DataQueryDTO.ConditionField condition = (DataQueryDTO.ConditionField)childTerm;
                if (condition.field.getId().equals(dataEntitiesConfig.getEntityPerformanceTableField(entityId))){
                    int value = Integer.parseInt(condition.getValue());
                    if (value >= dataEntitiesConfig.getEntityPerformanceTableFieldMinValue(entityId) &&
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

	// Getters and setters

    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig) {

        this.dataEntitiesConfig = dataEntitiesConfig;
    }
}
