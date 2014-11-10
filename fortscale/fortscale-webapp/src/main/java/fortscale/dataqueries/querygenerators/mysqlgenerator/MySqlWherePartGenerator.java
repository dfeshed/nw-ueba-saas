package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import fortscale.dataqueries.DataEntitiesConfig;
import fortscale.dataqueries.querydto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.utils.hdfs.partition.PartitionStrategy;

import java.util.*;

/**
 * Generate the "where" part of the query in MySql
 */
@Component
public class MySqlWherePartGenerator implements QueryPartGenerator {
    @Autowired
    MySqlFieldGenerator mySqlFieldGenerator;

    @Autowired
    DataEntitiesConfig dataEntitiesConfig;

    @Autowired
    MySqlValueGenerator mySqlValueGenerator;

    static {


    }

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{

		if (dataQueryDTO.getConditions() == null)
            return "";

        String partitionsSql = getPartitionsSql(dataQueryDTO);
		return "WHERE " + ((partitionsSql != null && partitionsSql.length() > 0) ? partitionsSql + " AND " : "") + getConditionTermSql(dataQueryDTO.getConditions(), dataQueryDTO);
	}

    private String getConditionTermSql(ConditionTerm conditionTerm, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        ArrayList<String> terms = new ArrayList<String>();
        Joiner joiner = Joiner.on(" " + conditionTerm.getOperator().toString() + " ").skipNulls();

        for(Term term: conditionTerm.getTerms()){
            if (term.getClass() == ConditionField.class)
                terms.add(getConditionFieldSql((ConditionField)term, dataQueryDTO));
            else
                terms.add(getConditionTermSql((ConditionTerm) term, dataQueryDTO));
        }

        return "(" + joiner.join(terms) + ")";
    }

    /**
     * Return the partiotion sql statement (condition on the partition fields )
     * @param dataQueryDTO
     * @return
     * @throws InvalidQueryException
     */

    private String getPartitionsSql(DataQueryDTO dataQueryDTO) throws InvalidQueryException{

        String entityId = dataQueryDTO.getEntities()[0];

        PartitionStrategy partitionStrategy = dataEntitiesConfig.getEntityPartitionStrategy(entityId);
        if (partitionStrategy == null)
            return "";

        String partitionConditions =  createPartitionSql(partitionStrategy, dataQueryDTO.getConditions(), entityId,dataQueryDTO);


        return partitionConditions;
    }

    /**
     * Check whether a partition for a field should be enabled in the WHERE SQL query.
     * A partition is enabled if it's present in a DTO's conditions.
     * If yes create his sql statement
     * IMPORTANT: Partitions are allowed only in the root terms of a DTOs conditions, so if there are nested terms, they won't be considered when looking for partitions!!
     * @param partitionStrategy
     * @param term
     * @param entityId
     * @param dataQueryDTO
     * @return
     */
    private String createPartitionSql(PartitionStrategy partitionStrategy,ConditionTerm term, String entityId,DataQueryDTO dataQueryDTO)throws InvalidQueryException{


        if (term.getTerms() == null || term.getTerms().size() == 0)
            return null;

        ArrayList<String> sqlConditions = new ArrayList<>();
        Joiner joiner = Joiner.on(" " + term.getOperator().toString() + " ").skipNulls();
        ArrayList<String> entityPartitionsBaeFields = dataEntitiesConfig.getEntityPartitionBaseField(entityId);



        for (Term childTerm: term.getTerms()){

            if (childTerm.getClass() == ConditionField.class){
                ConditionField condition = (ConditionField)childTerm;
                if (entityPartitionsBaeFields.contains(condition.getField().getId()))
                {
                    ConditionField partitionCondition = new ConditionField();

                    //TODO - need to make it more generic - the interface partition strategy is based on long as partition (timestamp) - need to abstract it to String and to parse the value in the implementations
                    partitionCondition.setValue(partitionStrategy.getImpalaPartitionValue(Long.parseLong(condition.getValue())));
                    partitionCondition.setOperator(condition.getOperator());
                    partitionCondition.setField(new DataQueryField());
                    partitionCondition.getField().setId(partitionStrategy.getImpalaPartitionFieldName());
                    String conditionSql = getConditionFieldSql(partitionCondition, dataQueryDTO);
                    if (!sqlConditions.contains(conditionSql)){
                        sqlConditions.add(conditionSql);
                    }
                }
            }

        }

        return joiner.join(sqlConditions);

    }


    private String getConditionFieldSql(ConditionField conditionField, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder();

        sb.append(mySqlFieldGenerator.generateSql(conditionField.getField(), dataQueryDTO));
        sb.append(" ");

        MySqlOperator operator = MySqlConditionOperators.getOperator(conditionField.getOperator());
        if (operator == null)
            throw new InvalidQueryException("Unknown operator for MySql: " + conditionField.getOperator().toString() + ".");

        if (operator.requiresValue && conditionField.getValue() == null)
            throw new InvalidQueryException("Can't create MySQL query, the " + conditionField.getOperator().name() + " operator requires a value, but none was specified.");

        sb.append(operator.sqlOperator);
        sb.append(" ");

        String entityId = conditionField.getField().getEntity();
        if (entityId == null)
            entityId = dataQueryDTO.getEntities()[0];

        sb.append(mySqlValueGenerator.generateSql(conditionField.getValue(), dataEntitiesConfig.getFieldType(entityId , conditionField.getField().getId())));

        return sb.toString();
    }

    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig) {
        this.dataEntitiesConfig = dataEntitiesConfig;
    }

    public void setMySqlFieldGenerator(MySqlFieldGenerator mySqlFieldGenerator){
        this.mySqlFieldGenerator = mySqlFieldGenerator;
    }

    public void setMySqlValueGenerator(MySqlValueGenerator mySqlValueGenerator){
        this.mySqlValueGenerator = mySqlValueGenerator;
    }
}
