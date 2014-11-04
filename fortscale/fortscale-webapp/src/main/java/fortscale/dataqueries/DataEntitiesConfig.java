package fortscale.dataqueries;

import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utilities for all data queries
 */
@Component
public class DataEntitiesConfig implements EmbeddedValueResolverAware {
    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }

    StringValueResolver stringValueResolver;

    /**
     * Returns an array of all of an entity's field IDs
     * @param entityId
     * @return
     */
    public ArrayList<String> getAllEntityFields(String entityId){
        ArrayList<String> fields = new ArrayList<String>();
        try{
            String[] configFields = stringValueResolver.resolveStringValue("${entities." + entityId + ".fields}").split("\\s*,[,\\s]*");
            Collections.addAll(fields, configFields);
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
     * Gets all the logical entities that are present in entities.properties.
     * @return
     */
    public List<DataEntity> getAllLogicalEntities() throws Exception{
        String[] entityIds = stringValueResolver.resolveStringValue("${entities}").split("\\s*,[,\\s]*");
        ArrayList<DataEntity> entities = new ArrayList<>();

        for(String entityId: entityIds){
            entities.add(getLogicalEntity(entityId));
        }

        return entities;
    }

    /**
     * Gets a DataQuery entity configuration, to be sent to the front-end. This is only the logical representation of the entity, without mappings.
     * @param entityId The ID of the entity
     * @return
     */
    public DataEntity getLogicalEntity(String entityId) throws Exception{
        DataEntity entity = new DataEntity();
        entity.id = entityId;
        entity.name = getExtendableValue(entityId, "name");
        entity.shortName = getExtendableValue(entityId, "short_name");

        List<String> fieldIds = getAllEntityFields(entityId);
        ArrayList<DataEntity.Field> fields = new ArrayList<>();

        for(String fieldId: fieldIds){
            try {
                String fieldPrefix = "field." + fieldId + ".";
                DataEntity.Field field = new DataEntity.Field();
                field.id = fieldId;
                field.name = getExtendableValue(entityId, fieldPrefix + "name");
                field.type = QueryValueType.valueOf(getExtendableValue(entityId, fieldPrefix + "type"));
                field.scoreField = getExtendableValue(entityId, fieldPrefix + "score");

                String isDefaultEnabled = getExtendableValue(entityId, fieldPrefix + "enabled");
                field.isDefaultEnabled = isDefaultEnabled == null || !isDefaultEnabled.equals("false");
                fields.add(field);
            } catch(Exception error){
                throw new Exception("Can't read field " + fieldId + " of entity " + entityId);
            }
        }

        entity.fields = fields;

        return entity;
    }

    /**
     * Get the entite partition Strategy
     * @param entityId
     * @return PartitionStrategy - The partition strategy of the entity
     * @throws Exception
     */
    public PartitionStrategy getEntityPartitionStrategy(String entityId) throws InvalidQueryException{

        String entityPartitionsConfig = getExtendableValue(entityId, "partitions");

        if (entityPartitionsConfig == null)
            return null;

        //will represent a the partition startigy of the current entity
        PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy(entityPartitionsConfig);


        return partitionStrategy;
    }


    /**
     * Get the entite partition base field
     * @param entityId
     * @return entityPartitionBaseField - The field that base on him we use the partition
     * @throws Exception
     */
    public ArrayList<String> getEntityPartitionBaseField(String entityId) throws InvalidQueryException{

        ArrayList<String> paritionBaseFields = new ArrayList<>();

        String [] entityPartitionBaseField = getExtendableValue(entityId, "partition.base.field").split(",");
        Collections.addAll(paritionBaseFields,entityPartitionBaseField);

        return paritionBaseFields;
    }

    /**
     * Returns the physical column of a field according to entity/field. The column may be in a base entity of the specified entity.
     * @param entityId
     * @param fieldId
     * @return
     */
    public String getFieldColumn(String entityId, String fieldId) throws InvalidQueryException {
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
    private String getExtendableValue(String entityId, String key){
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

    /**
     * Returns the physical table name of an entity, or null if none found.
     * @param entityId The ID of the entity
     * @return
     */
    public String getEntityTable(String entityId){
        try {
            return stringValueResolver.resolveStringValue("${entities." + entityId + ".table}");
        }
        catch(Exception error){
            return null;
        }
    }

    /**
     * Given an entity ID and field ID, returns the type of the field
     * @param entityId
     * @param fieldId
     * @return
     * @throws InvalidQueryException
     */
    public QueryValueType getFieldType(String entityId, String fieldId) throws InvalidQueryException{
        String typeStr = getExtendableValue(entityId, "field." + fieldId + ".type");
        if (typeStr == null)
            throw new InvalidQueryException("Couldn't find type for field " + fieldId + " in entity " + entityId + ".");

        return QueryValueType.valueOf(typeStr);
    }

    /**
     * Returns the physical table name of a performance table for a specified entity
     * @param entityId
     * @return
     */
    public String getEntityPerformanceTable(String entityId){
        return getExtendableValue(entityId, "performance_table");
    }

    /**
     * Returns the logical field which is used to determine if a performance table should be used
     * @param entityId
     * @return
     */
    public String getEntityPerformanceTableField(String entityId){
        return getExtendableValue(entityId, "performance_field");
    }

    /**
     * Returns the minimum value of a performance table field required to use the performance table instead of the regular table
     * @param entityId
     * @return
     * @throws Exception
     */
    public int getEntityPerformanceTableFieldMinValue(String entityId) throws Exception{
        String value = getExtendableValue(entityId, "performance_field_min_value");

        if (value == null)
            throw new Exception("Entity " + entityId + " doesn't have a specified performance table min value.");

        return Integer.parseInt(value);
    }
}
