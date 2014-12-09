package fortscale.services.dataentity;

import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	 * Data entity ID --> configuration of entity (lazy initialization)
	 */
    private HashMap<String, DataEntityConfig> entitiesCache = new HashMap<>();

	/**
	 * All entities (lazy initialization)
	 */
    private List<DataEntity> allDataEntities;

    private DataEntityConfig getEntityFromCache(String entityId){
        DataEntityConfig entityConfig = entitiesCache.get(entityId);
        if(entityConfig == null){
            entityConfig = new DataEntityConfig();
            entitiesCache.put(entityId, entityConfig);
        }

        return entityConfig;
    }

    private DataEntityFieldConfig getFieldFromCache(String entityId, String fieldId){
        DataEntityConfig entityConfig = getEntityFromCache(entityId);
        return entityConfig.getField(fieldId);
    }

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
    public List<String> getAllEntityFields(String entityId){
        DataEntityConfig entityConfig = getEntityFromCache(entityId);
        if (entityConfig.getFieldsList() != null)
            return entityConfig.getFieldsList();

        ArrayList<String> fieldsList = new ArrayList<>();
        entityConfig.setFieldsList(fieldsList);

        try{
            String[] configFields = stringValueResolver.resolveStringValue(getPropertyKey(entityId, "fields")).split("\\s*,[,\\s]*");
            Collections.addAll(fieldsList, configFields);
        }
        catch(Exception error){
            return null;
        }
        
        String baseEntityId = getBaseEntityId(entityId);
        if (baseEntityId != null){
            List<String> baseEntityFields = getAllEntityFields(baseEntityId);
            if (baseEntityFields != null){
                fieldsList.addAll(baseEntityFields);
            }
        }

        return entityConfig.getFieldsList();
    }

    /**
     * Gets all the logical entities that are present in entities.properties.
     * @return
     */
    public List<DataEntity> getAllLogicalEntities() throws Exception{
        if (allDataEntities != null)
            return allDataEntities;

        String[] entityIds = stringValueResolver.resolveStringValue("${entities}").split("\\s*,[,\\s]*");
        ArrayList<DataEntity> entities = new ArrayList<>();

        for(String entityId: entityIds){
            entities.add(getLogicalEntity(entityId));
        }

        allDataEntities = entities;
        return entities;
    }

    /**
     * Gets the DB type that should be used to generate the query for the given entity
     * @param entityId
     * @return
     */
    public SupportedDBType getEntityDbType(String entityId){
        DataEntityConfig entityConfig = getEntityFromCache(entityId);
        if (entityConfig.getDbType() != null)
            return entityConfig.getDbType();

        String type = stringValueResolver.resolveStringValue(getPropertyKey(entityId, "db"));
        SupportedDBType dbType = SupportedDBType.valueOf(type);
        entityConfig.setDbType(dbType);
        return dbType;
    }

    /**
     * Gets a DataQuery entity configuration, to be sent to the front-end. This is only the logical representation of the entity, without mappings.
     * @param entityId The ID of the entity
     * @return
     */
    public DataEntity getLogicalEntity(String entityId) throws Exception{
        DataEntityConfig entityConfig = getEntityFromCache(entityId);

        DataEntity entity = new DataEntity();
        entity.setId(entityId);

        String entityName = entityConfig.getName();
        if (entityName == null){
            entityName = getExtendableValue(entityId, "name");
            entityConfig.setName(entityName);
        }

        entity.setName(entityName);

        String entityShortName = entityConfig.getShortName();
        if (entityShortName == null){
            entityShortName = getExtendableValue(entityId, "short_name");
            entityConfig.setShortName(entityShortName);
        }

        entity.setName(entityShortName);

        List<String> fieldIds = getAllEntityFields(entityId);
        ArrayList<DataEntityField> fields = new ArrayList<>();

        for(String fieldId: fieldIds){
            try {
                DataEntityField field = new DataEntityField();
                DataEntityFieldConfig fieldConfig = entityConfig.getField(fieldId);

                field.setId(fieldId);

                String fieldName = fieldConfig.getName();
                if (fieldName == null){
                    fieldName = getExtendableValue(entityId, "field", fieldId, "name");
                    fieldConfig.setName(fieldName);
                }
                field.setName(fieldName);

                QueryValueType fieldType = fieldConfig.getType();
                if (fieldType == null){
                    fieldType = QueryValueType.valueOf(getExtendableValue(entityId, "field", fieldId, "type"));
                    fieldConfig.setType(fieldType);
                }
                field.setType(fieldType);

                String scoreField = fieldConfig.getScore();
                if (scoreField == null){
                    scoreField = getExtendableValue(entityId, "field", fieldId, "score");
                    fieldConfig.setScore(scoreField);
                }
                field.setScoreField(scoreField);

                Boolean isDefaultEnabled = fieldConfig.getDefaultEnabled();
                if (isDefaultEnabled == null){
                    String isDefaultEnabledStr = getExtendableValue(entityId, "field", fieldId, "enabled");
                    isDefaultEnabled = isDefaultEnabledStr == null || !isDefaultEnabledStr.equals("false");
                    fieldConfig.setDefaultEnabled(isDefaultEnabled);
                }
                field.setIsDefaultEnabled(isDefaultEnabled);

                Boolean isLogicalOnly = fieldConfig.isLogicalOnly();
                if (isLogicalOnly == null){
                    String isLogicalOnlyStr = getExtendableValue(entityId, "field", fieldId, "is_logical_only");
                    isLogicalOnly = isLogicalOnlyStr != null && isLogicalOnlyStr.equalsIgnoreCase("true");
                    fieldConfig.setLogicalOnly(isLogicalOnly);
                }
                field.setLogicalOnly(isLogicalOnly);

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
        DataEntityConfig entityConfig = getEntityFromCache(entityId);
        String entityPartitionsConfig = entityConfig.getPartitions();
        if (entityPartitionsConfig == null){
            entityPartitionsConfig = getExtendableValue(entityId, "partitions");
            entityConfig.setPartitions(entityPartitionsConfig);
        }

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
    public List<String> getEntityPartitionBaseField(String entityId) throws InvalidQueryException{
        DataEntityConfig entityConfig = getEntityFromCache(entityId);
        if (entityConfig.getPartitionsBaseField() != null)
            return entityConfig.getPartitionsBaseField();

        ArrayList<String> partitionBaseFields = new ArrayList<>();

        String [] entityPartitionBaseField = getExtendableValue(entityId, "partition.base.field").split(",");
        Collections.addAll(partitionBaseFields,entityPartitionBaseField);

        entityConfig.setPartitionsBaseField(partitionBaseFields);
        return partitionBaseFields;
    }

    /**
     * Returns the physical column of a field according to entity/field. The column may be in a base entity of the specified entity.
     * @param entityId
     * @param fieldId
     * @return
     */
    public String getFieldColumn(String entityId, String fieldId) throws InvalidQueryException {
        DataEntityFieldConfig fieldConfig = getFieldFromCache(entityId, fieldId);
        if (fieldConfig.getColumn() != null)
            return fieldConfig.getColumn();

        String column = getExtendableValue(entityId, "field", fieldId, "column");
        if (column == null)
            throw new InvalidQueryException(String.format("Column for field %s in entity %s not found.", fieldId, entityId));

        fieldConfig.setColumn(column);
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
        DataEntityConfig entityConfig = getEntityFromCache(entityId);
        if (entityConfig.getExtendedEntity() != null)
            return entityConfig.getExtendedEntity();

        try {
            String baseEntityId = stringValueResolver.resolveStringValue(getPropertyKey(entityId, "extends"));
            entityConfig.setExtendedEntity(baseEntityId);
            return baseEntityId;
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
        DataEntityConfig entityConfig = getEntityFromCache(entityId);
        if (entityConfig.getTable() != null)
            return entityConfig.getTable();

        try {
            String table = stringValueResolver.resolveStringValue(getPropertyKey(entityId, "table"));
            entityConfig.setTable(table);
            return table;
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
    public QueryValueType getFieldType(String entityId, String fieldId, Boolean ignoreUnknownType) throws InvalidQueryException{
        DataEntityFieldConfig fieldConfig = getFieldFromCache(entityId, fieldId);
        if (fieldConfig.getType() != null)
            return fieldConfig.getType();

        String typeStr = getExtendableValue(entityId, "field", fieldId, "type");
        if (typeStr == null) {
            if (ignoreUnknownType)
                return null;
            
            throw new InvalidQueryException(String.format("Couldn't find type for field %s in entity %s.", fieldId, entityId));
        }

        QueryValueType type = QueryValueType.valueOf(typeStr);
        fieldConfig.setType(type);
        return type;
    }

    public QueryValueType getFieldType(String entityId, String fieldId) throws InvalidQueryException{
        return getFieldType(entityId, fieldId, false);
    }

    /**
     * Returns the physical table name of a performance table for a specified entity
     * @param entityId
     * @return
     */
    public String getEntityPerformanceTable(String entityId){
        DataEntityConfig entityConfig = getEntityFromCache(entityId);
        if (entityConfig.getPerformanceTable() != null)
            return entityConfig.getPerformanceTable();

        if (entityConfig.getPerformanceTable() != null && entityConfig.getPerformanceTable().equals(""))
            return null;

        String performanceTable = getExtendableValue(entityId, "performance_table");
        entityConfig.setPerformanceTable(performanceTable != null ? performanceTable : "");

        return performanceTable;
    }

    /**
     * Returns the logical field which is used to determine if a performance table should be used
     * @param entityId
     * @return
     */
    public String getEntityPerformanceTableField(String entityId){
        DataEntityConfig entityConfig = getEntityFromCache(entityId);
        if (entityConfig.getPerformanceField() != null)
            return entityConfig.getPerformanceField();

        String performanceTableField = getExtendableValue(entityId, "performance_field");
        entityConfig.setPerformanceField(performanceTableField);
        return performanceTableField;
    }

    /**
     * Returns the minimum value of a performance table field required to use the performance table instead of the regular table
     * @param entityId
     * @return
     * @throws Exception
     */
    public int getEntityPerformanceTableFieldMinValue(String entityId) throws Exception{
        DataEntityConfig entityConfig = getEntityFromCache(entityId);
        if (entityConfig.getPerformanceFieldMinValue() != 0)
            return entityConfig.getPerformanceFieldMinValue();

        String value = getExtendableValue(entityId, "performance_field_min_value");

        if (value == null)
            throw new Exception(String.format("Entity %s doesn't have a specified performance table min value.", entityId));

        int minValue = Integer.parseInt(value);
        entityConfig.setPerformanceFieldMinValue(minValue);
        return minValue;
    }
}
