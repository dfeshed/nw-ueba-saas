package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.DataEntitiesConfig;
import fortscale.dataqueries.querydto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            String entityId = dataQueryDTO.getEntities()[0];
            String tableName = dataQueryDTO.getConditions() != null && isHighScore(entityId, dataQueryDTO.getConditions())
                    ? dataEntitiesConfig.getEntityPerformanceTable(entityId)
                    : dataEntitiesConfig.getEntityTable(entityId);

            // For now the generator supports only single table queries. When joins are supported, need to add the logic here.
            StringBuilder sb = new StringBuilder("FROM ");
            sb.append(tableName);

            return sb.toString();
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
    private Boolean isHighScore(String entityId, ConditionTerm term) throws Exception{
        if (term.getTerms() == null || term.getTerms().size() == 0)
            return false;

        for (Term childTerm: term.getTerms()){
            if (childTerm instanceof ConditionField){
                ConditionField condition = (ConditionField)childTerm;
                if (condition.getField().getId().equals(dataEntitiesConfig.getEntityPerformanceTableField(entityId))){
                    int value = Integer.parseInt(condition.getValue());
                    if (value >= dataEntitiesConfig.getEntityPerformanceTableFieldMinValue(entityId) &&
                            (condition.getOperator() == QueryOperator.equals || condition.getOperator() == QueryOperator.greaterThan || condition.getOperator() == QueryOperator.greaterThanOrEquals)){
                        return true;
                    }
                }
            }
            else if (isHighScore(entityId, (ConditionTerm)childTerm))
                return true;
        }

        return false;
    }

	// Getters and setters

    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig) {

        this.dataEntitiesConfig = dataEntitiesConfig;
    }
}
