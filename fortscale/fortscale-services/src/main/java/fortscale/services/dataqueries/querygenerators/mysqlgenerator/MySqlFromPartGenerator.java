package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataqueries.querydto.*;
import fortscale.services.dataqueries.querygenerators.QueryPartGenerator;
import org.springframework.stereotype.Component;

import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;

import javax.management.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Generate the "from" part of the query in MySql
 */
@Component
public class MySqlFromPartGenerator extends QueryPartGenerator {
	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        if (dataQueryDTO.getSubQuery() != null && dataQueryDTO.getEntities().length > 0)
            throw new InvalidQueryException("A DataQuery can't have both entities and a subquery.");

        try {
            StringBuilder sb = new StringBuilder("FROM ");

            // If a sub query exists, the FROM clause refers to it
            if (dataQueryDTO.getSubQuery() != null){
                sb.append(mySqlMultipleQueryGenerator.getSubQuerySql(dataQueryDTO.getSubQuery()));
            }
            // Otherwise, use the dataQueryDTO's entities to select from tables:
            else {
                if (dataQueryDTO.getEntities().length == 0)
                    throw new InvalidQueryException("At least one entity is required for a DataQuery.");

                String entityId = dataQueryDtoHelper.getEntityId(dataQueryDTO);
                String tableName = dataEntitiesConfig.getEntityTable(entityId);



                // If there is condition about the score, and the score is higher than configuration (default 50), we will use the performance table ("_top")
                if (dataQueryDTO.getConditions() != null && isHighScore(entityId, dataQueryDTO.getConditions()))
                    sb.append(dataEntitiesConfig.getEntityPerformanceTable(entityId)).append(" as ").append(tableName);
                else
                    sb.append(tableName);
            }

            return sb.toString();
        }
        catch(Exception error){
            throw new InvalidQueryException("Can't create FROM part of the MySQL query. Error: " + error.getMessage());
        }
	}

    final static ArrayList<QueryOperator> greaterThanOrEqualsOperators = new ArrayList<QueryOperator>();
    static{
        greaterThanOrEqualsOperators.add(QueryOperator.equals);
        greaterThanOrEqualsOperators.add(QueryOperator.greaterThan);
        greaterThanOrEqualsOperators.add(QueryOperator.greaterThanOrEquals);
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

        Boolean returnValue = false;

        for (Term childTerm: term.getTerms()){
            if (childTerm instanceof ConditionField){
                ConditionField condition = (ConditionField)childTerm;
                if (condition.getField().getId().equals(dataEntitiesConfig.getEntityPerformanceTableField(entityId))){
                    int value = Integer.parseInt(condition.getValue());

                    // If it's the performance field and its value is equal or required to the performance value and it's a >= operator, we can use the performance table.
                    if (greaterThanOrEqualsOperators.contains(condition.getOperator()) && value >= dataEntitiesConfig.getEntityPerformanceTableFieldMinValue(entityId))
                        returnValue = true;
                    // But if there's any condition on the performance field that doesn't allow to use the performance field, the isHighScore method should return false:
                    else
                        return false;
                }
            }
            else if (isHighScore(entityId, (ConditionTerm)childTerm))
                returnValue = true;
        }

        return returnValue;
    }
}
