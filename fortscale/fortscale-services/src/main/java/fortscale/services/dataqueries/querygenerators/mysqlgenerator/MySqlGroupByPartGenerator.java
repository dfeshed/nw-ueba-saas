package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import java.util.ArrayList;

import fortscale.services.dataentity.*;
import fortscale.services.dataqueries.querydto.DataQueryField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;

/**
 * Generates the GROUP BY part of the query in MySql - "GROUP BY field1, field2, field3..."
 */
@Component
public class MySqlGroupByPartGenerator implements QueryPartGenerator {
    @Autowired
    MySqlFieldGenerator mySqlFieldGenerator;

    @Autowired
    DataEntitiesConfig dataEntitiesConfig;

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
                			entityId = dataQueryDTO.getEntities()[0];
                    	
                        DataEntity entity = dataEntitiesConfig.getLogicalEntity(entityId);
                        if (entity == null)
                            throw new InvalidQueryException("Unknown entity, '" + dataQueryDTO.getEntities()[0] + "'");

                        DataEntityField fieldConfig = entity.getField(field.getId());

                        if (fieldConfig.isLogicalOnly())
                            fieldsSql.add(fieldConfig.getId());
                        else
                            fieldsSql.add(mySqlFieldGenerator.generateSql(field, dataQueryDTO));
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

	public MySqlFieldGenerator getMySqlFieldGenerator() {
		return mySqlFieldGenerator;
	}

    /**
     * Set MySQL field generator, used by tests
     * @param mySqlFieldGenerator
     */
	public void setMySqlFieldGenerator(MySqlFieldGenerator mySqlFieldGenerator) {
		this.mySqlFieldGenerator = mySqlFieldGenerator;
	}

    /**
     * Set the dataEntitiesConfig, used by tests
     * @param dataEntitiesConfig
     */
    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig){
        this.dataEntitiesConfig = dataEntitiesConfig;
    }
}
