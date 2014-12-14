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

        if (field.getValue() != null)
            addFieldValue(field, dataQueryDTO, fieldSB);
        else if (field.getFunc() != null)
            addFieldWithFunction(field, dataQueryDTO, fieldSB);
        else if (field.isAllFields() != null && field.isAllFields() == true)
            addFieldAllFields(field, dataQueryDTO, fieldSB);
        else
            addRegularField(field, dataQueryDTO, aliasAsId, mapToColumn, fieldSB);

        return fieldSB.toString();
    }

    private void addFieldTable(String entityId, String fieldId, StringBuilder sb) throws InvalidQueryException{
        if (dataEntitiesConfig.getFieldIsLogicalOnly(entityId, fieldId))
            return;

        sb.append(dataEntitiesConfig.getEntityTable(entityId)).append(".");
    }

    /**
     * Render the SQL for a field that has a value rather than a column to use
     * @param field
     * @param dataQueryDTO
     * @param fieldSB
     * @throws InvalidQueryException
     */
    private void addFieldValue(DataQueryField field, DataQueryDTO dataQueryDTO, StringBuilder fieldSB) throws InvalidQueryException{
        if (field.getAlias() == null)
            throw new InvalidQueryException(String.format("An alias should be specified for field value '%s'.", field.getValue()));

        fieldSB.append(mySqlValueGenerator.generateSql(field.getValue(), field.getValueType()));
        fieldSB.append(" as '").append(field.getAlias()).append("'");
    }

    /**
     * Render the SQL for a field with a function
     * @param field
     * @param dataQueryDTO
     * @param fieldSB
     * @throws InvalidQueryException
     */
    private void addFieldWithFunction(DataQueryField field, DataQueryDTO dataQueryDTO, StringBuilder fieldSB) throws InvalidQueryException{
        fieldSB.append(mySqlFieldFunctionGenerator.generateSql(field, dataQueryDTO));
        if (field.getAlias() != null)
            fieldSB.append(" as '").append(field.getAlias()).append("'");
    }

    /**
     * Render the SQL for a field that represents all the fields in an entity
     * @param field
     * @param dataQueryDTO
     * @param fieldSB
     * @throws InvalidQueryException
     */
    private void addFieldAllFields(DataQueryField field, DataQueryDTO dataQueryDTO, StringBuilder fieldSB) throws InvalidQueryException{
        // First, validate that there's no ID or value, since that would be a confusing structure:
        if (field.getId() != null || field.getValue() != null || field.getAlias() != null)
            throw new InvalidQueryException("Invalid field, if allFields = true, can't have ID, value or alias.");

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

    /**
     * Render SQL for a regular field - column from a table.
     * @param field
     * @param dataQueryDTO
     * @param aliasAsId If true, the field's SQL is rendered as '[value] as [fieldId]'. This is required to return logical field names by the API.
     * @param mapToColumn If true, maps a field to a physical column. This should be usually true, since we want to map logical fields to physical ones.
     * @param fieldSB
     * @throws InvalidQueryException
     */
    private void addRegularField(DataQueryField field, DataQueryDTO dataQueryDTO, Boolean aliasAsId, Boolean mapToColumn, StringBuilder fieldSB) throws InvalidQueryException{
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
