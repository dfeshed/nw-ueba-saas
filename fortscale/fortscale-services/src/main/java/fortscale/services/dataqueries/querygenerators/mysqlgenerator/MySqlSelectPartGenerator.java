package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.SingleQueryPartGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates the SELECT part of the query in MySql - "SELECT field1, field2, field3..."
 */
@Component
public class MySqlSelectPartGenerator extends SingleQueryPartGenerator {

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder("SELECT ");

        ArrayList<String> fieldsSql;
        Joiner joiner = Joiner.on(", ").skipNulls();

        List<DataQueryField> fields;

        if (dataQueryDTO.getEntities().length == 1 && (dataQueryDTO.getFields() == null || dataQueryDTO.getFields().size() == 0))
            fields = getAllEntityFields(dataQueryDTO.getEntities()[0]);
        else
            fields = dataQueryDTO.getFields();

        fieldsSql = new ArrayList<>();

        for (DataQueryField field : fields) {
            if (field != null)
                fieldsSql.add(mySqlFieldGenerator.generateSql(field, dataQueryDTO, true));
        }

        sb.append(joiner.join(fieldsSql));

		return sb.toString();
	}

    private List<DataQueryField> getAllEntityFields(String entityId) throws InvalidQueryException{
        List<String> fieldIds = dataEntitiesConfig.getAllEntityFields(entityId);
        ArrayList<DataQueryField> fields = new ArrayList<DataQueryField>();

        for(String fieldId: fieldIds){
            // Explicit fields should be explicitly requested in the fields list, so they aren't returned when all fields are specified.
            if (dataEntitiesConfig.getFieldIsExplicit(entityId, fieldId))
                continue;

            DataQueryField field = new DataQueryField();
            field.setId(fieldId);
            fields.add(field);
        }

        return fields;
    }
}
