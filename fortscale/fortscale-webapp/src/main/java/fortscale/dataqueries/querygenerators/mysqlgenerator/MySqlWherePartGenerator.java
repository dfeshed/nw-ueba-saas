package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
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

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{

		if (dataQueryDTO.conditions == null || dataQueryDTO.conditions.size() == 0)
            return "";

		return "WHERE " + getConditionTermSql(dataQueryDTO.conditions.get(0), dataQueryDTO);
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

    private String getConditionFieldSql(DataQueryDTO.ConditionField conditionField, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        return mySqlUtils.getConditionFieldSql(conditionField, dataQueryDTO);
    }
}
