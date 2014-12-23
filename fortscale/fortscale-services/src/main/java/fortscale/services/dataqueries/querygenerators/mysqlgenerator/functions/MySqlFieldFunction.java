package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
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

    public abstract String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException;

    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig){
        this.dataEntitiesConfig = dataEntitiesConfig;
    }

    /**
     * Gets the physical name of the field to use inside a function.
     * @param field
     * @param dataQueryDTO
     * @return
     * @throws InvalidQueryException
     */
    protected String getFieldName(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        if (field.getId() != null){
            String entityId = field.getEntity();
            if (entityId == null && dataQueryDTO.getEntities() != null && dataQueryDTO.getEntities().length > 0) {
                entityId = dataQueryDTO.getEntities()[0];

                return dataEntitiesConfig.getFieldColumn(entityId, field.getId());
            }
        }
        else if (field.getFunc().getParams() != null && field.getFunc().getParams().containsKey("field")){
            return field.getFunc().getParams().get("field");
        }

        return null;
    }
}
