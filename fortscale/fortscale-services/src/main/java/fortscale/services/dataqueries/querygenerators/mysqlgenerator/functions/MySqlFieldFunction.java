package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryDtoHelper;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Yossi on 04/11/2014.
 * An interface for MySQL functions
 */
public abstract class MySqlFieldFunction {
    @Autowired
    protected DataEntitiesConfig dataEntitiesConfig;

    @Autowired
    protected DataQueryDtoHelper dataQueryDtoHelper;

    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig){
        this.dataEntitiesConfig = dataEntitiesConfig;
    }

    public void setDataQueryDtoHelper(DataQueryDtoHelper dataQueryDtoHelper) {
        this.dataQueryDtoHelper = dataQueryDtoHelper;
    }

    public abstract String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException;

    /**
     * Gets the physical name of the field to use inside a function.
     * @param field
     * @param dataQueryDTO
     * @return
     * @throws InvalidQueryException
     */
    protected String getFieldName(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        if (field.getId() != null){
            if (dataQueryDTO.getSubQuery() != null){
                return field.getId();
            }
            else {
                String entityId = field.getEntity();
                if (entityId == null)
                    entityId = dataQueryDtoHelper.getEntityId(dataQueryDTO);

                if (entityId == null)
                	throw new InvalidQueryException("Can't get field name for function, entity not found.");
                
                return dataEntitiesConfig.getFieldColumn(entityId, field.getId());
            }
        }
        else if (field.getFunc().getParams() != null && field.getFunc().getParams().containsKey("field")){
            return field.getFunc().getParams().get("field");
        }

        return null;
    }
}
