package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Yossi on 03/11/2014.
 * Generates SQL for a field
 */
@Component
public class MySqlFieldGenerator {
    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

    @Autowired
    MySqlValueGenerator mySqlValueGenerator;

    @Autowired
    MySqlFieldFunctionGenerator mySqlFieldFunctionGenerator;

    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO, Boolean aliasAsId, Boolean mapToColumn) throws InvalidQueryException {
        StringBuilder fieldSB = new StringBuilder();

        if (field.getValue() != null){
            if (field.getAlias() == null)
                throw new InvalidQueryException(String.format("An alias should be specified for field value '%s'.", field.getValue()));

            fieldSB.append(mySqlValueGenerator.generateSql(field.getValue(), field.getValueType()));
            fieldSB.append(" as '").append(field.getAlias()).append("'");
        }
        else if (field.getFunc() != null){
            fieldSB.append(mySqlFieldFunctionGenerator.generateSql(field, dataQueryDTO));
            if (field.getAlias() != null)
                fieldSB.append(" as '").append(field.getAlias()).append("'");
        }
        else if (field.isAllFields()){
            String entityId = field.getEntity();
            String entityTable = dataEntitiesConfig.getEntityTable(entityId);

            if (entityId == null)
                entityId = dataQueryDTO.getEntities()[0];

            List<String> fieldIds = dataEntitiesConfig.getAllEntityFields(entityId);
            for(String fieldId: fieldIds){
                fieldSB.append(entityTable).append(".").append(dataEntitiesConfig.getFieldColumn(entityId, fieldId));
                fieldSB.append(", ");
            }
            if (fieldSB.length() > 2)
                fieldSB.delete(fieldSB.length() - 2, fieldSB.length());
        }
        else{
            if (field.getEntity() != null)
                fieldSB.append(dataEntitiesConfig.getEntityTable(field.getEntity())).append(".");

            String columnName = mapToColumn 
            		? dataEntitiesConfig.getFieldColumn(field.getEntity() != null ? field.getEntity() : dataQueryDTO.getEntities()[0], field.getId() )
    				: field.getId();
            fieldSB.append(columnName);

            if (field.getAlias() != null)
                fieldSB.append(" as '").append(field.getAlias()).append("'");
            else if (aliasAsId && !columnName.equals(field.getId()))
                fieldSB.append(" as '").append(field.getId()).append("'");
        }

        return fieldSB.toString();
    }

    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO, Boolean aliasAsId) throws InvalidQueryException{
        return generateSql(field, dataQueryDTO, aliasAsId, true);
    }
    
    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        return generateSql(field, dataQueryDTO, false, true);
    }

    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig){
        this.dataEntitiesConfig = dataEntitiesConfig;
    }

    public void setMySqlValueGenerator(MySqlValueGenerator mySqlValueGenerator){
        this.mySqlValueGenerator = mySqlValueGenerator;
    }
}
