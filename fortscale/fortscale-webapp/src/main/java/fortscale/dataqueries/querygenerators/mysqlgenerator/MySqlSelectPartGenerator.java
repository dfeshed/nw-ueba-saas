package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.dataqueries.DataQueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates the SELECT part of the query in MySql - "SELECT field1, field2, field3..."
 */
@Component
public class MySqlSelectPartGenerator implements QueryPartGenerator {

	@Autowired
    private MySqlUtils mySqlUtils;

    @Autowired
    DataQueryUtils dataQueryUtils;

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder("SELECT ");

        ArrayList<String> fieldsSql;
        Joiner joiner = Joiner.on(", ").skipNulls();

        List<DataQueryDTO.DataQueryField> fields;

        if (dataQueryDTO.entities.length == 1 && (dataQueryDTO.fields == null || dataQueryDTO.fields.size() == 0))
            fields = getAllEntityFields(dataQueryDTO.entities[0]);
        else
            fields = dataQueryDTO.fields;

        fieldsSql = new ArrayList<>();

        for (DataQueryDTO.DataQueryField field : fields) {
            if (field != null)
                fieldsSql.add(mySqlUtils.getFieldSql(field, dataQueryDTO, true));
        }

        sb.append(joiner.join(fieldsSql));

		return sb.toString();
	}

    private List<DataQueryDTO.DataQueryField> getAllEntityFields(String entityId){
        List<String> fieldIds = dataQueryUtils.getAllEntityFields(entityId);
        ArrayList<DataQueryDTO.DataQueryField> fields = new ArrayList<DataQueryDTO.DataQueryField>();

        for(String fieldId: fieldIds){
            DataQueryDTO.DataQueryField field = new DataQueryDTO.DataQueryField();
            field.setId(fieldId);
            fields.add(field);
        }

        return fields;
    }


    public void setDataQueryUtils(DataQueryUtils dataQueryUtils) {
        this.dataQueryUtils = dataQueryUtils;
    }

    public void setMySqlUtils(MySqlUtils mySqlUtils) {
        this.mySqlUtils = mySqlUtils;
    }
}
