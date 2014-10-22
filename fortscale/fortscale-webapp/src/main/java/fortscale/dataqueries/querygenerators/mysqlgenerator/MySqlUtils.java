package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.QueryValueType;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;
import sun.org.mozilla.javascript.internal.EcmaError;

import java.util.ArrayList;
import java.util.List;

/**
 * Some static functions that should be available to multiple MySql part generators
 */
@Component
public class MySqlUtils implements EmbeddedValueResolverAware {
    StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }

    public String getFieldSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO, Boolean aliasAsId) throws InvalidQueryException {
        StringBuilder fieldSB = new StringBuilder();

        if (field.getValue() != null){
            if (field.getAlias() == null)
                throw new InvalidQueryException("An alias should be specified for field value '" + field.getValue() + "'.");

            fieldSB.append(getValueSql(field.getValue(), field.valueType));
            fieldSB.append(" as " + field.getAlias());
        }
        else{
            if (dataQueryDTO.entities.length > 1 && field.getEntity() != null)
                fieldSB.append(field.getEntity() + ".");

            String columnName = getFieldColumn(field.getEntity() != null ? field.getEntity() : dataQueryDTO.entities[0], field.getId() );
            fieldSB.append(columnName);

            if (field.getAlias() != null)
                fieldSB.append(" as " + field.getAlias());
            else if (aliasAsId && !columnName.equals(field.getId()))
                fieldSB.append(" as " + field.getId());
        }

        return fieldSB.toString();
    }

    public String getFieldSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        return getFieldSql(field, dataQueryDTO, false);
    }

    /**
     * Returns an array of all of an entity's field IDs
     * @param entityId
     * @return
     */
    public ArrayList<String> getAllEntityFields(String entityId){
        ArrayList<String> fields = new ArrayList<String>();
        try{
            String[] configFields = stringValueResolver.resolveStringValue("${entities." + entityId + ".fields}").split("\\s*,[,\\s]*");
            for(String field: configFields){
                fields.add(field);
            }
        }
        catch(Exception error){
            return null;
        }

        String baseEntityId = getBaseEntity(entityId);
        if (baseEntityId != null){
            ArrayList<String> baseEntityFields = getAllEntityFields(baseEntityId);
            if (baseEntityFields != null){
                fields.addAll(baseEntityFields);
            }
        }

        return fields;
    }

    /**
     * Returns the physical column of a field according to entity/field. The column may be in a base entity of the specified entity.
     * @param entityId
     * @param fieldId
     * @return
     */
    public String getFieldColumn(String entityId, String fieldId) throws InvalidQueryException{
        String column = getExtendableValue(entityId, "field." + fieldId + ".column");
        if (column == null)
            throw new InvalidQueryException("Column for field " + fieldId + " in entity " + entityId + " not found.");

        return column;
    }

    /**
     * Searches for a key value in an entity, and if not found and the entity has an 'extends' property, searches its base entity as well (recursive)
     * @param entityId
     * @param key
     * @return
     */
    public String getExtendableValue(String entityId, String key){
        String fullKey = "entities." + entityId + "." + key;
        String value;
        try{
            value = stringValueResolver.resolveStringValue("${" + fullKey + "}");
        }
        catch(Exception error){
            value = null;
        }

        if (value == null) {
            String baseEntity = getBaseEntity(entityId);
            if (baseEntity == null)
                return null;

            return getExtendableValue(baseEntity, key);
        }
        else
            return value;
    }
    
    /**
     * Gets the ID of an entity's base class, if the specified entity has an 'entities.[entityId].extends' property.
     * @param entityId
     * @return
     */
    public String getBaseEntity(String entityId){
        try {
            return stringValueResolver.resolveStringValue("${entities." + entityId + ".extends}");
        }
        catch(Exception error){
            return null;
        }
    }

    public String getEntityTable(String entityId){
        try {
            return stringValueResolver.resolveStringValue("${entities." + entityId + ".table}");
        }
        catch(Exception error){
            return null;
        }
    }

    public String getValueSql(String value, QueryValueType type){
        if (value == null)
            return "null";

        switch (type){
            case STRING:
                return "\"" + value + "\"";
            default:
                return value;
        }
    }

    public String getConditionFieldSql(DataQueryDTO.ConditionField conditionField, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder();

        sb.append(getFieldSql(conditionField.field, dataQueryDTO));
        sb.append(" ");

        MySqlOperator operator;
        try {
            operator = MySqlOperator.valueOf(conditionField.operator.toString());
        }
        catch(Exception error){
            throw new InvalidQueryException("Unknown operator for MySql: " + conditionField.operator.toString() + ".");
        }

        if (operator.requiresValue && conditionField.getValue() == null)
            throw new InvalidQueryException("Can't create MySQL query, the " + operator.name() + " operator requires a value, but none was specified.");

        sb.append(operator.sqlOperator);
        sb.append(" ");

        String entityId = conditionField.field.getEntity();
        if (entityId == null)
            entityId = dataQueryDTO.entities[0];

        sb.append(getValueSql(conditionField.getValue(), getFieldType(entityId , conditionField.field.getId())));

        return sb.toString();
    }

    public QueryValueType getFieldType(String entityId, String fieldId) throws InvalidQueryException{
        String typeStr = getExtendableValue(entityId, "field." + fieldId + ".type");
        if (typeStr == null)
            throw new InvalidQueryException("Couldn't find type for field " + fieldId + " in entity " + entityId + ".");

        return QueryValueType.valueOf(typeStr);
    }

    public String getEntityPerformanceTable(String entityId){
        return getExtendableValue(entityId, "performance_table");
    }

    public String getEntityPerformanceTableField(String entityId){
        return getExtendableValue(entityId, "performance_field");
    }

    public int getEntityPerformanceTableFieldMinValue(String entityId) throws Exception{
        String value = getExtendableValue(entityId, "performance_field_min_value");

        if (value == null)
            throw new Exception("Entity " + entityId + " doesn't have a specified performance table min value.");

        return Integer.parseInt(value);
    }

    public static enum MySqlOperator{
        equals ("="),
        notEquals ("!="),
        greaterThan (">"),
        greaterThanOrEquals (">="),
        lesserThan ("<="),
        lesserThanOrEquals ("<="),
        in ("IN"),
        like ("LIKE"),
        hasValue ("IS NOT NULL", false),
        hasNoValue ("IS NULL");

        public final String sqlOperator;
        public final Boolean requiresValue;

        MySqlOperator(String operator){
            this.sqlOperator = operator;
            this.requiresValue = true;
        }

        MySqlOperator(String operator, Boolean requiresValue){
            this.sqlOperator = operator;
            this.requiresValue = requiresValue;
        }
    }
}
