package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.dataqueries.DataQueryPartition;
import fortscale.dataqueries.DataQueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    private String getPartitionsSql(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        String entityId = dataQueryDTO.entities[0];
        ArrayList<DataQueryPartition> partitions = dataQueryUtils.getEntityPartitions(entityId);
        if (partitions == null || partitions.size() == 0)
            return "";

        ArrayList<String> sqlConditions = new ArrayList<>();
        Joiner joiner = Joiner.on(" AND ").skipNulls();

        for(DataQueryPartition partition: partitions){
            ArrayList<DataQueryDTO.ConditionField> partitionConditions = getPartitionConditions(dataQueryDTO.conditions, partition.entityField);
            if (partitionConditions != null){
                sqlConditions.add(getPartitionSql(partition, partitionConditions, dataQueryDTO));
            }
        }

        return joiner.join(sqlConditions);
    }

    /**
     * Check whether a partition for a field should be enabled in the WHERE SQL query.
     * A partition is enabled if it's present in a DTO's conditions.
     * IMPORTANT: Partitions are allowed only in the root terms of a DTOs conditions, so if there are nested terms, they won't be considered when looking for partitions!!
     * @param term
     * @param partitionFieldId
     * @return
     */
    private ArrayList<DataQueryDTO.ConditionField> getPartitionConditions(DataQueryDTO.ConditionTerm term, String partitionFieldId){
        if (term.terms == null || term.terms.size() == 0)
            return null;

        ArrayList<DataQueryDTO.ConditionField> conditions = new ArrayList<>();

        for (DataQueryDTO.Term childTerm: term.terms){
            if (childTerm.getClass() == DataQueryDTO.ConditionField.class){
                DataQueryDTO.ConditionField condition = (DataQueryDTO.ConditionField)childTerm;
                if (condition.field.getId().equals(partitionFieldId))
                    conditions.add(condition);
            }
        }

        if (conditions.size() == 0)
            return null;

        return conditions;
    }

    private String getPartitionSql(DataQueryPartition partitionConfig, List<DataQueryDTO.ConditionField> conditionFields, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        ArrayList<String> sqlConditions = new ArrayList<>();
        Joiner joiner = Joiner.on(" AND ").skipNulls();

        for(DataQueryDTO.ConditionField conditionField: conditionFields){
            DataQueryDTO.ConditionField partitionCondition = new DataQueryDTO.ConditionField();
            partitionCondition.setValue(getPartitionValue(partitionConfig, conditionField));
            partitionCondition.operator = conditionField.operator;
            partitionCondition.field = new DataQueryDTO.DataQueryField();
            partitionCondition.field.setId(partitionConfig.partitionField);

            String conditionSql = getConditionFieldSql(partitionCondition, dataQueryDTO);
            if (!sqlConditions.contains(conditionSql)){
                sqlConditions.add(conditionSql);
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
