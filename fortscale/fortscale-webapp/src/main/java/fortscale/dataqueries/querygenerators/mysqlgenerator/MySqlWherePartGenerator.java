package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.dataqueries.DataQueryPartition;
import fortscale.dataqueries.DataQueryUtils;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Generate the "where" part of the query in MySql
 */
@Component
public class MySqlWherePartGenerator implements QueryPartGenerator {
    @Autowired
    MySqlFieldGenerator mySqlFieldGenerator;

    @Autowired
    DataQueryUtils dataQueryUtils;

    @Autowired
    MySqlValueGenerator mySqlValueGenerator;

    static {


    }

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{

		if (dataQueryDTO.conditions == null)
            return null;

        String partitionsSql = getPartitionsSql(dataQueryDTO);
		return "WHERE " + ((partitionsSql != null && partitionsSql.length() > 0) ? partitionsSql + " AND " : "") + getConditionTermSql(dataQueryDTO.conditions, dataQueryDTO);
	}

    private String getConditionTermSql(DataQueryDTO.ConditionTerm conditionTerm, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        ArrayList<String> terms = new ArrayList<String>();
        Joiner joiner = Joiner.on(" " + conditionTerm.operator.toString() + " ").skipNulls();

        for(DataQueryDTO.Term term: conditionTerm.terms){
            if (term.getClass() == DataQueryDTO.ConditionField.class)
                terms.add(getConditionFieldSql((DataQueryDTO.ConditionField)term, dataQueryDTO));
            else
                terms.add(getConditionTermSql((DataQueryDTO.ConditionTerm) term, dataQueryDTO));
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

        String entityId = dataQueryDTO.entities[0];
        PartitionStrategy partitionStrategy = dataQueryUtils.getEntityPartitionStrategy(entityId);

        if (partitionStrategy == null)
            return "";

        String partitionConditions =  createPartitionSql(partitionStrategy,dataQueryDTO.conditions, entityId,dataQueryDTO);


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
    private String createPartitionSql(PartitionStrategy partitionStrategy,DataQueryDTO.ConditionTerm term, String entityId,DataQueryDTO dataQueryDTO)throws InvalidQueryException{


        if (term.terms == null || term.terms.size() == 0)
            return null;

        ArrayList<String> sqlConditions = new ArrayList<>();
        Joiner joiner = Joiner.on(" AND ").skipNulls();
        ArrayList<String> entityPartitionsBaeFields = dataQueryUtils.getEntityPartitionBaseField(entityId);


        ArrayList<DataQueryDTO.ConditionField> conditions = new ArrayList<>();

        for (DataQueryDTO.Term childTerm: term.terms){

            if (childTerm.getClass() == DataQueryDTO.ConditionField.class){
                DataQueryDTO.ConditionField condition = (DataQueryDTO.ConditionField)childTerm;
                if (entityPartitionsBaeFields.contains(condition.field.getId()))
                {
                    DataQueryDTO.ConditionField partitionCondition = new DataQueryDTO.ConditionField();

                    //TODO - need to make it more generic - the interface partition strategy is based on long as partition (timestamp) - need to abstract it to String and to parse the value in the implementations
                    partitionCondition.setValue(partitionStrategy.getImpalaPartitionValue(Long.parseLong(condition.getValue())));
                    partitionCondition.operator = condition.operator;
                    partitionCondition.field = new DataQueryDTO.DataQueryField();
                    partitionCondition.field.setId(partitionStrategy.getImpalaPartitionFieldName());
                    String conditionSql = getConditionFieldSql(partitionCondition, dataQueryDTO);
                    if (!sqlConditions.contains(conditionSql)){
                        sqlConditions.add(conditionSql);
                    }
                }
            }

        }

        return joiner.join(sqlConditions);

    }




    private String getPartitionValue(DataQueryPartition partitionConfig, DataQueryDTO.ConditionField conditionField) throws InvalidQueryException{
        switch(partitionConfig.type){
            case daily:
                Date date = new Date(Long.parseLong(conditionField.getValue()) * 1000);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return Integer.toString(calendar.get(Calendar.YEAR) * 10000 + calendar.get(Calendar.MONTH) * 100 + calendar.get(Calendar.DATE));
            default:
                throw new InvalidQueryException("Handling for " + partitionConfig.type.name() + " is not implemented yet.");
        }
    }

    private String getConditionFieldSql(DataQueryDTO.ConditionField conditionField, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder();

        sb.append(mySqlFieldGenerator.generateSql(conditionField.field, dataQueryDTO));
        sb.append(" ");

        MySqlOperator operator;
        try {
            operator = MySqlOperator.valueOf(conditionField.operator.toString());
        }
        catch(Exception error){
            throw new InvalidQueryException("Unknown operator for MySql: " + conditionField.operator.toString() + ".");
        }

        if (operator.requiresValue && conditionField.getValue() == null)
            throw new InvalidQueryException("Can't create MySQL query, the " + operator.name() + " operator requires a value, but none was specified.");

        sb.append(operator.sqlOperator);
        sb.append(" ");

        String entityId = conditionField.field.getEntity();
        if (entityId == null)
            entityId = dataQueryDTO.entities[0];

        sb.append(mySqlValueGenerator.generateSql(conditionField.getValue(), dataQueryUtils.getFieldType(entityId , conditionField.field.getId())));

        return sb.toString();
    }

    public void setDataQueryUtils(DataQueryUtils dataQueryUtils) {
        this.dataQueryUtils = dataQueryUtils;
    }
}
