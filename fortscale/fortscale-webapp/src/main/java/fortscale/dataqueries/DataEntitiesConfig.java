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
     * Given an entity name and an array of strings consisting the rest of the path, returns a key name in the same fortmat as found in the entities.properties file
     * @param entityId
     * @param path
     * @return
     */
    private String getPropertyKey(String entityId, String... path){
        StringBuilder keyBuilder = new StringBuilder("${entities.");
        keyBuilder.append(entityId);

        for(String pathElement: path){
            keyBuilder.append(".");
            keyBuilder.append(pathElement);
        }

        keyBuilder.append("}");
        return keyBuilder.toString();
    }

    /**
     * Returns an array of all of an entity's field IDs
     * @param entityId
     * @return
     */
    public ArrayList<String> getAllEntityFields(String entityId){
        ArrayList<String> fields = new ArrayList<String>();
        try{
            String[] configFields = stringValueResolver.resolveStringValue(String.format("${entities.%s.fields}", entityId)).split("\\s*,[,\\s]*");
            Collections.addAll(fields, configFields);
        }
        catch(Exception error){
            return null;
        }
        
        String baseEntityId = getBaseEntityId(entityId);
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
     * Gets the DB type that should be used to generate the query for the given entity
     * @param entityId
     * @return
     */
    public SupportedDBType getEntityDbType(String entityId){
        String type = stringValueResolver.resolveStringValue(getPropertyKey(entityId, "db"));
        return SupportedDBType.valueOf(type);
    }

    /**
     * Gets a DataQuery entity configuration, to be sent to the front-end. This is only the logical representation of the entity, without mappings.
     * @param entityId The ID of the entity
     * @return
     */
    public DataEntity getLogicalEntity(String entityId) throws Exception{
        DataEntity entity = new DataEntity();
        entity.setId(entityId);
        entity.setName(getExtendableValue(entityId, "name"));
        entity.setShortName(getExtendableValue(entityId, "short_name"));

        List<String> fieldIds = getAllEntityFields(entityId);
        ArrayList<DataEntityField> fields = new ArrayList<>();

        for(String fieldId: fieldIds){
            try {
                DataEntityField field = new DataEntityField();
                field.setId(fieldId);
                field.setName(getExtendableValue(entityId, "field", fieldId, "name"));
                field.setType(QueryValueType.valueOf(getExtendableValue(entityId, "field", fieldId, "type")));
                field.setScoreField(getExtendableValue(entityId, "field", fieldId, "score"));

                String isDefaultEnabled = getExtendableValue(entityId, "field", fieldId, "enabled");
                field.setIsDefaultEnabled(isDefaultEnabled == null || !isDefaultEnabled.equals("false"));
                fields.add(field);
            } catch(Exception error){
                throw new Exception(String.format("Can't read field %s of entity %s", fieldId, entityId));
            }
        }

        entity.setFields(fields);

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
        String column = getExtendableValue(entityId, "field", fieldId, "column");
        if (column == null)
            throw new InvalidQueryException(String.format("Column for field %s in entity %s not found.", fieldId, entityId));

        return column;
    }

    /**
     * Searches for a key value in an entity, and if not found and the entity has an 'extends' property, searches its base entity as well (recursive)
     * @param entityId
     * @param path
     * @return
     */
    private String getExtendableValue(String entityId, String... path){
        String fullKey = getPropertyKey(entityId, path);
        String value;
        try{
            value = stringValueResolver.resolveStringValue(fullKey);
        }
        catch(Exception error){
            value = null;
        }

        if (value == null) {
            String baseEntity = getBaseEntityId(entityId);
            if (baseEntity == null)
                return null;

            return getExtendableValue(baseEntity, path);
        }
        else
            return value;
    }

    /**
     * Gets the ID of an entity's base class, if the specified entity has an 'entities.[entityId].extends' property.
     * @param entityId
     * @return
     */
    public String getBaseEntityId(String entityId){
        try {
            return stringValueResolver.resolveStringValue(String.format("${entities.%s.extends}", entityId));
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
            return stringValueResolver.resolveStringValue(String.format("${entities.%s.table}", entityId));
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
        String typeStr = getExtendableValue(entityId, "field", fieldId, "type");
        if (typeStr == null)
            throw new InvalidQueryException(String.format("Couldn't find type for field %s in entity %s.", fieldId, entityId));

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
            throw new Exception(String.format("Entity %s doesn't have a specified performance table min value.", entityId));

        return Integer.parseInt(value);
    }
}
