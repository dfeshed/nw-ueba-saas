package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import java.util.ArrayList;

import fortscale.services.dataqueries.querydto.DataQueryField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;

/**
 * Generates the GROUP BY part of the query in MySql - "GROUP BY field1, field2, field3..."
 */
@Component
public class MySqlGroupByPartGenerator implements QueryPartGenerator, EmbeddedValueResolverAware {
    StringValueResolver stringValueResolver;

    @Autowired
    MySqlFieldGenerator mySqlFieldGenerator;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }

	@Override
	public String generateQueryPart(DataQueryDTO dataQueryDTO)
			throws InvalidQueryException {
		
		if (dataQueryDTO.getGroupBy() == null)
			return "";
		
		ArrayList<String> fieldsSql = new ArrayList<>();
        Joiner joiner = Joiner.on(", ").skipNulls();
        
        for (DataQueryField field : dataQueryDTO.getGroupBy()) {
            if (field != null){
            	if (field.getAlias() != null)
        			throw new InvalidQueryException("Invalid GROUP BY field, " + field.getId() + ", can't alias fields in a GROUP BY query part.");

        		fieldsSql.add(mySqlFieldGenerator.generateSql(field, dataQueryDTO));
            }
        }

		StringBuilder sb = new StringBuilder("GROUP BY ");
		sb = joiner.appendTo(sb,fieldsSql);
		return sb.toString();
	}

	public MySqlFieldGenerator getMySqlFieldGenerator() {
		return mySqlFieldGenerator;
	}

	public void setMySqlFieldGenerator(MySqlFieldGenerator mySqlFieldGenerator) {
		this.mySqlFieldGenerator = mySqlFieldGenerator;
	}
}
