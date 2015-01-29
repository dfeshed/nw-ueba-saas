package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import fortscale.services.dataentity.QueryValueType;
import fortscale.services.dataqueries.querydto.*;
import fortscale.services.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.operators.MySqlOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.utils.hdfs.partition.PartitionStrategy;

import java.util.*;

/**
 * Generate the "where" part of the query in MySql
 */
@Component
public class MySqlWherePartGenerator extends QueryPartGenerator {
    @Autowired
    MySqlValueGenerator mySqlValueGenerator;


	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{

		if (dataQueryDTO.getConditions() == null)
            return "";

        String partitionsSql = getPartitionsSql(dataQueryDTO);
        StringBuilder sb = new StringBuilder("WHERE ");

        if (partitionsSql != null && partitionsSql.length() > 0)
            sb.append(partitionsSql).append(" AND ");

        sb.append(getConditionTermSql(dataQueryDTO.getConditions(), dataQueryDTO));
		return sb.toString();
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

        StringBuilder sb = new StringBuilder("(").append(joiner.join(terms)).append(")");
        return sb.toString();
    }

    /**
     * Return the partiotion sql statement (condition on the partition fields )
     * @param dataQueryDTO
     * @return
     * @throws InvalidQueryException
     */

    private String getPartitionsSql(DataQueryDTO dataQueryDTO) throws InvalidQueryException{

        String entityId = dataQueryDtoHelper.getEntityId(dataQueryDTO);

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
        List<String> entityPartitionsBaeFields = dataEntitiesConfig.getEntityPartitionBaseField(entityId);

        for (Term childTerm: term.getTerms()){

            if (childTerm.getClass() == ConditionField.class){
                ConditionField condition = (ConditionField)childTerm;
                if (entityPartitionsBaeFields.contains(condition.getField().getId()))
                {
                    List<ConditionField> partitionConditions = getPartitionConditionFields(partitionStrategy, condition);

                    for(ConditionField partitionCondition: partitionConditions) {
                        String conditionSql = getConditionFieldSql(partitionCondition, dataQueryDTO, false);
                        if (!sqlConditions.contains(conditionSql)) {
                            sqlConditions.add(conditionSql);
                        }
                    }
                }
            }

        }

        return joiner.join(sqlConditions);

    }

    private String getConditionFieldSql(ConditionField conditionField, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
    	return getConditionFieldSql(conditionField, dataQueryDTO, true);
    }
    
    private String getConditionFieldSql(ConditionField conditionField, DataQueryDTO dataQueryDTO, Boolean mapToColumn) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder();

        sb.append(mySqlFieldGenerator.generateSql(conditionField.getField(), dataQueryDTO, false, mapToColumn));
        sb.append(" ");

        MySqlOperator operator = MySqlConditionOperators.getOperator(conditionField.getOperator());

        if (operator.requiresValue && conditionField.getValue() == null && conditionField.getValueField() == null)
            throw new InvalidQueryException("Can't create MySQL query, the " + conditionField.getOperator().name() + " operator requires a value, but none was specified.");

        sb.append(operator.sqlOperator);
        sb.append(" ");

        String entityId = conditionField.getField().getEntity();
        if (entityId == null)
            entityId = dataQueryDtoHelper.getEntityId(dataQueryDTO);

        if (conditionField.getValueField() != null)
            sb.append(mySqlFieldGenerator.generateSql(conditionField.getValueField(), dataQueryDTO, false, true));
        else {
            // The operator might need to add something to the value:
            QueryValueType type = dataEntitiesConfig.getFieldType(entityId, conditionField.getField().getId(), !mapToColumn);
            String value = operator.getOperatorValue(mySqlValueGenerator, conditionField.getValue(), type );
            sb.append(value);
        }
        return sb.toString();
    }

    public List<ConditionField> getPartitionConditionFields(PartitionStrategy partitionStrategy, ConditionField condition){
        ArrayList<ConditionField> partitionConditionFields = new ArrayList<>();

        // The partition can be a simple comparison operator (=, >, >=, <, <=) or a between operator, in which case it
        // is broken down to two (<=, >=) ConditionFields:
        if (condition.getOperator() == QueryOperator.between){
            String[] values = condition.getValue().split(",");

            ConditionField firstBetweenValueConditionField = new ConditionField(condition);
            firstBetweenValueConditionField.setValue(values[0]);
            firstBetweenValueConditionField.setOperator(QueryOperator.greaterThanOrEquals);
            partitionConditionFields.add(getPartitionConditionField(partitionStrategy, firstBetweenValueConditionField));

            ConditionField secondBetweenValueConditionField = new ConditionField(condition);
            secondBetweenValueConditionField.setValue(values[1]);
            secondBetweenValueConditionField.setOperator(QueryOperator.lesserThanOrEquals);
            partitionConditionFields.add(getPartitionConditionField(partitionStrategy, secondBetweenValueConditionField));
        }
        else{
            partitionConditionFields.add(getPartitionConditionField(partitionStrategy, condition));
        }

        return partitionConditionFields;
    }

    /**
     * Creates the conditionField for a partition, according to a conditionField. Partition conditions use the partition's physical fields, so there's a need to translate from
     * the original logical field.
     * @param partitionStrategy
     * @param condition
     * @return
     */
    public ConditionField getPartitionConditionField(PartitionStrategy partitionStrategy, ConditionField condition){
        ConditionField partitionCondition = new ConditionField();

        //TODO - need to make it more generic - the interface partition strategy is based on long as partition (timestamp) - need to abstract it to String and to parse the value in the implementations
        partitionCondition.setValue(partitionStrategy.getImpalaPartitionValue(Long.parseLong(condition.getValue())));
        partitionCondition.setOperator(condition.getOperator());
        partitionCondition.setField(new DataQueryField());
        partitionCondition.getField().setId(partitionStrategy.getImpalaPartitionFieldName());
        return partitionCondition;
    }

    public void setMySqlValueGenerator(MySqlValueGenerator mySqlValueGenerator){
        this.mySqlValueGenerator = mySqlValueGenerator;
    }
}
