package fortscale.common.dataqueries.querygenerators.mysqlgenerator;

import com.google.common.base.Joiner;
import fortscale.common.dataentity.DataEntity;
import fortscale.common.dataentity.DataEntityField;
import fortscale.common.dataqueries.querydto.DataQueryDTO;
import fortscale.common.dataqueries.querydto.DataQueryField;
import fortscale.common.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Generates the GROUP BY part of the query in MySql - "GROUP BY field1, field2, field3..."
 */
@Component
public class MySqlGroupByPartGenerator extends QueryPartGenerator {
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
            		fieldsSql.add(field.getAlias());
                else{
                    try {
                    	String entityId = field.getEntity();
                    	if (entityId == null)
                			entityId = dataQueryDtoHelper.getEntityId(dataQueryDTO);
                    	
                        DataEntity entity = dataEntitiesConfig.getLogicalEntity(entityId);
                        if (entity == null)
                            throw new InvalidQueryException("Unknown entity, '" + entityId + "'");

                        DataEntityField fieldConfig = entity.getField(field.getId());

                        if (fieldConfig.isLogicalOnly())
                            fieldsSql.add(fieldConfig.getId());
                        else
                            fieldsSql.add(mySqlFieldGenerator.generateSql(field, dataQueryDTO,  false, false));
                    }
                    catch(Exception error){
                        throw new InvalidQueryException(error.getMessage(), error);
                    }
                }
            }
        }

		StringBuilder sb = new StringBuilder("GROUP BY ");
		sb = joiner.appendTo(sb,fieldsSql);
		return sb.toString();
	}
}
