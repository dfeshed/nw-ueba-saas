package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.omg.CORBA.DynAnyPackage.Invalid;
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
        else if (field.isAllFields() != null && field.isAllFields() == true){
            String entityId = field.getEntity();

            if (entityId == null)
                entityId = dataQueryDTO.getEntities()[0];

            List<String> fieldIds = dataEntitiesConfig.getAllEntityFields(entityId);
            for(String fieldId: fieldIds){
                // Explicit fields should be explicitly requested in the fields list, so they aren't returned when all fields are specified.
                if (dataEntitiesConfig.getFieldIsExplicit(entityId, fieldId))
                    continue;

                addFieldTable(entityId, fieldId, fieldSB);
                fieldSB.append(dataEntitiesConfig.getFieldColumn(entityId, fieldId));
                fieldSB.append(" as '").append(fieldId).append("'");
                fieldSB.append(", ");
            }
            if (fieldSB.length() > 2)
                fieldSB.delete(fieldSB.length() - 2, fieldSB.length());
        }
        else{
            if (field.getEntity() != null)
                addFieldTable(field.getEntity(), field.getId(), fieldSB);

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

    private void addFieldTable(String entityId, String fieldId, StringBuilder sb) throws InvalidQueryException{
        if (dataEntitiesConfig.getFieldIsLogicalOnly(entityId, fieldId))
            return;

        sb.append(dataEntitiesConfig.getEntityTable(entityId)).append(".");
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
