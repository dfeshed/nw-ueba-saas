package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.util.StringValueResolver;

import java.util.ArrayList;

/**
 * Generates the SELECT part of the query in MySql - "SELECT field1, field2, field3..."
 */
@Component
public class MySqlSelectPartGenerator implements QueryPartGenerator, EmbeddedValueResolverAware {
    StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }

    @Autowired
    private MySqlUtils mySqlUtils;

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder("SELECT ");
        ArrayList<String> fields = new ArrayList<String>();
        Joiner joiner = Joiner.on(", ").skipNulls();

        for(DataQueryDTO.DataQueryField field: dataQueryDTO.fields){
            if (field != null)
                fields.add(mySqlUtils.getFieldSql(field, dataQueryDTO, true));
        }

        sb.append(joiner.join(fields));

		return sb.toString();
	}

}
