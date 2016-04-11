package fortscale.common.dataqueries.querygenerators.mysqlgenerator;

import com.google.common.base.Joiner;
import fortscale.common.dataentity.QueryValueType;
import fortscale.common.dataqueries.querydto.*;
import fortscale.common.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.operators.MySqlOperator;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.operators.MySqlOperatorsList;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Generate the "where" part of the query in MySql
 */
@Component
public class MySqlWherePartGenerator extends QueryPartGenerator {
    @Autowired
    MySqlValueGenerator mySqlValueGenerator;


	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{

		if (dataQueryDTO.getConditions() == null || dataQueryDTO.getConditions().isEmpty()) {
            return "";
        }

        String partitionsSql = getPartitionsSql(dataQueryDTO);
        StringBuilder sb = new StringBuilder("WHERE ");

        if (partitionsSql != null && partitionsSql.length() > 0)
            sb.append(partitionsSql).append(" AND ");

        sb.append(getConditionTermSql(dataQueryDTO.getConditions(), dataQueryDTO));
		return sb.toString();
	}

    private String getConditionTermSql(ConditionTerm conditionTerm, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        ArrayList<String> terms = new ArrayList<String>();
        Joiner joiner = Joiner.on(" " + conditionTerm.getLogicalOperator().toString() + " ").skipNulls();

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

        String partitionConditions =  createPartitionSql(partitionStrategy, dataQueryDTO.getConditions(), entityId, dataQueryDTO);


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
        Joiner joiner = Joiner.on(" " + term.getLogicalOperator().toString() + " ").skipNulls();
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

    /**
     * Builds the SQL condition statement
     * @param conditionField field that is sent from the API that represent the logical condition
     * @param dataQueryDTO the dataQuery object that is passed recursively
     * @param mapToColumn boolean value whether to map field to column
     * @return the SQL Statement fragment string
     * @throws InvalidQueryException
     */
    private String getConditionFieldSql(ConditionField conditionField, DataQueryDTO dataQueryDTO, Boolean mapToColumn) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        //Calculate enforcefiledValueToLowererCase -
        //Columns names and string values should be translated to lower case if the condition is of the form of
        //<column_name> = value  and also the value could be case sensitive (type is string, select, etc..)
        String entityId = conditionField.getField().getEntity();
        if (entityId == null) {
            entityId = dataQueryDtoHelper.getEntityId(dataQueryDTO);
        }
        QueryValueType type = dataEntitiesConfig.getFieldType(entityId, conditionField.getField().getId(), !mapToColumn);

        //ConditionField - Compare given field to other field instead of comparing to given value.
        //If ConditionField is false - we compare the given field to given value
        boolean isConditionField = (conditionField.getValueField() != null);

        boolean enforcefiledValueToLowererCase = !isConditionField  && type !=null && type.isCaseSensitive();

        MySqlOperatorsList operatorList = MySqlConditionOperators.getOperator(conditionField.getQueryOperator(),type);
        boolean firstElement = true;
        for (MySqlOperator operator : operatorList.getMySqlOperators()){
            if (!firstElement){
                sb.append(" ").append(operatorList.getLogicalOperator().toString()).append(" ");
            }
            firstElement = false;
            sb.append(mySqlFieldGenerator.generateSql(conditionField.getField(), dataQueryDTO, false, mapToColumn, enforcefiledValueToLowererCase));
            sb.append(" ");

            if (operator.requiresValue && conditionField.getValue() == null && conditionField.getValueField() == null) {
                throw new InvalidQueryException("Can't create MySQL query, the " + conditionField.getQueryOperator().name() + " operator requires a value, but none was specified.");
            }
            //when the column is a string with tokens(s) we do not need to add the value afterwards, but rather replace it with the token(s)
            if (conditionField.getValue() != null && dataEntitiesConfig.getFieldIsTokenized(conditionField.getField().getEntity(), conditionField.getField().getId())){
                sb = replaceTokens(sb, conditionField.getValue());
            } else {
                sb.append(operator.sqlOperator);
                sb.append(" ");


                if (isConditionField)
                    sb.append(mySqlFieldGenerator.generateSql(conditionField.getValueField(), dataQueryDTO, false, false));
                else {
                    // The operator might need to add something to the value:
                    String value = operator.getOperatorValue(mySqlValueGenerator, conditionField.getValue(), type, enforcefiledValueToLowererCase);
                    sb.append(value);
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * replacing a token inside a column expression.
     * The token can be multiple values and then multiple tokens. E.g., date between two values
     * @param sb
     * @param tokenArray
     * @return
     */
    private StringBuilder replaceTokens(StringBuilder sb, String tokenArray) {
        String[] tokens = tokenArray.split(",");
        String message = MessageFormat.format(sb.toString(), tokens);
        return new StringBuilder(message);
    }

    public List<ConditionField> getPartitionConditionFields(PartitionStrategy partitionStrategy, ConditionField condition){
        ArrayList<ConditionField> partitionConditionFields = new ArrayList<>();

        // The partition can be a simple comparison operator (=, >, >=, <, <=) or a between operator, in which case it
        // is broken down to two (<=, >=) ConditionFields:
        if (condition.getQueryOperator() == QueryOperator.between){
            String[] values = condition.getValue().split(",");

            ConditionField firstBetweenValueConditionField = new ConditionField(condition);
            firstBetweenValueConditionField.setValue(values[0]);
            firstBetweenValueConditionField.setQueryOperator(QueryOperator.greaterThanOrEquals);
            partitionConditionFields.add(getPartitionConditionField(partitionStrategy, firstBetweenValueConditionField));

            ConditionField secondBetweenValueConditionField = new ConditionField(condition);
            secondBetweenValueConditionField.setValue(values[1]);
            secondBetweenValueConditionField.setQueryOperator(QueryOperator.lesserThanOrEquals);
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
        partitionCondition.setQueryOperator(condition.getQueryOperator());
        partitionCondition.setField(new DataQueryField());
        partitionCondition.getField().setId(partitionStrategy.getImpalaPartitionFieldName());
        return partitionCondition;
    }

    public void setMySqlValueGenerator(MySqlValueGenerator mySqlValueGenerator){
        this.mySqlValueGenerator = mySqlValueGenerator;
    }
}
