package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.dataqueries.DataQueryPartition;
import fortscale.dataqueries.DataQueryPartitionType;
import fortscale.dataqueries.DataQueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

import java.util.ArrayList;

/**
 * Generate the "where" part of the query in MySql
 */
@Component
public class MySqlWherePartGenerator implements QueryPartGenerator {
    @Autowired
    private MySqlUtils mySqlUtils;

    @Autowired
    DataQueryUtils dataQueryUtils;

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{

		if (dataQueryDTO.conditions == null || dataQueryDTO.conditions.size() == 0)
            return "";

        String partitionsSql = getPartitionsSql(dataQueryDTO);
		return "WHERE " + (partitionsSql != null ? partitionsSql : "") + getConditionTermSql(dataQueryDTO.conditions.get(0), dataQueryDTO);
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
        ArrayList<DataQueryPartition> partitions = dataQueryUtils.getEntityPartitions(dataQueryDTO.entities[0]);
        if (partitions == null || partitions.size() == 0)
            return "";

        // TODO: Continue the partitions SQL rendering.
        return null;
    }

    private String getConditionFieldSql(DataQueryDTO.ConditionField conditionField, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        return mySqlUtils.getConditionFieldSql(conditionField, dataQueryDTO);
    }
}
