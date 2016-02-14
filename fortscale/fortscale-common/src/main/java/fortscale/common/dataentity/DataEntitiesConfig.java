package fortscale.common.dataentity;

import fortscale.common.dataqueries.querydto.DataQueryField;
import fortscale.common.dataqueries.querydto.QuerySort;
import fortscale.common.dataqueries.querydto.SortDirection;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.utils.TreeNode;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
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
public class DataEntitiesConfig  implements EmbeddedValueResolverAware,InitializingBean {
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
    private HashMap<String,DataEntity> allDataEntities;

	/**
	 * All base entities (lazy initialization)
	 */
	private HashMap<String,DataEntity> allBaseDataEntities;


	/**
	 * All leaf entities (lazy initialization)
	 */
	private HashMap<String,DataEntity> allLeafDataEntities;

	/**
	 * Entity Hierarchy tree (lazy initialization)
	 */
	private List<TreeNode<DataEntity>> entitiesHierarchyTreeCach;



	@Override
	public void afterPropertiesSet() throws Exception {

		// will fill the entities cache and will encapsulate the actual logic execution from the UI
		getAllLogicalEntities();
	}

    /**
     * Gets a DataEntityConfig object from cache, or creates a new one under the specified entityId if not found.
     * DOES NOT get the DataEntityConfig data itself, just creates the object in cache!
     * @param entityId
     * @return
     */
    private DataEntityConfig getEntityFromCache(String entityId){
        DataEntityConfig entityConfig = entitiesCache.get(entityId);
        if(entityConfig == null){
            entityConfig = new DataEntityConfig();
            entityConfig.setId(entityId);
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
            pathElement.trim();
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
            //We must have the fields attribute, but if the list of fields is empty
            //We can skip and conitunue to parent fields.
            String fieldsListAsStrimg = stringValueResolver.resolveStringValue(getPropertyKey(entityId, "fields"));
            if (StringUtils.isNotBlank(fieldsListAsStrimg)) {
                String[] configFields = fieldsListAsStrimg.split("\\s*,[,\\s]*");
                Collections.addAll(fieldsList, configFields);
            }
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

        if (allDataEntities != null){

            return new ArrayList<>(allDataEntities.values());
        }


        HashMap<String,DataEntity> baseEntities = getAllBaseEntities();
        HashMap<String,DataEntity> leafEntities = getAllLeafeEntities();

        HashMap<String,DataEntity> entities = new HashMap<>();
		entities.putAll(baseEntities);
		entities.putAll(leafEntities);




        allDataEntities = entities;
        return new ArrayList<>(entities.values());
    }

	/**
	 * Gets all the base entities that are present in entities.properties.
	 * @return
	 */
	public HashMap<String,DataEntity> getAllBaseEntities() throws Exception{
		if (allBaseDataEntities != null)
			return allBaseDataEntities;

		String[] entityIds = stringValueResolver.resolveStringValue("${base_entities}").split("\\s*,[,\\s]*");
		HashMap<String,DataEntity> entities  = new HashMap<>();

		for(String entityId: entityIds){
			entities.put(entityId, getLogicalEntity(entityId));
		}

		allBaseDataEntities = entities;
		return entities;
	}

	/**
	 * Gets all the leaf entities that are present in entities.properties.
	 * @return
	 */
	public HashMap<String,DataEntity> getAllLeafeEntities() throws Exception{
		if (allLeafDataEntities != null)
			return allLeafDataEntities;

		String[] entityIds = stringValueResolver.resolveStringValue("${leaf_entities}").split("\\s*,[,\\s]*");
        HashMap<String,DataEntity> entities = new HashMap<>();

		for(String entityId: entityIds){
			entities.put(entityId, getLogicalEntity(entityId));
		}

		allLeafDataEntities = entities;
		return entities;
	}

	/**
	 * This method will return a list of trees that will represent the inheritance hierarchy of the entities.properties file
	 * The tree will build exhaustive bottom up
	 */
	public List<TreeNode<DataEntity>> getEntitiesTrees() throws Exception
	{

		if (entitiesHierarchyTreeCach != null)
			return entitiesHierarchyTreeCach;

		// get the roots entities for the trees
		String[]  leaffEntities = stringValueResolver.resolveStringValue("${leaf_entities}").split("\\s*,[,\\s]*");

		List<TreeNode<DataEntity>> entitiesTrees = new ArrayList<>() ;


		//start build the sub trees for each leaf
		for (String leaf : leaffEntities)
		{
			DataEntity dataEntity = getLogicalEntity(leaf);
			TreeNode<DataEntity> entityNode = new TreeNode<>(dataEntity);
			TreeNode<DataEntity> tree = getSubTree(entityNode);
			entitiesTrees = mergerEntitiesSubTrees(tree,entitiesTrees);

		}

		entitiesHierarchyTreeCach = entitiesTrees;

		return entitiesTrees;

	}

	private TreeNode<DataEntity> getSubTree(TreeNode<DataEntity> treeNode) throws Exception
	{

		try {
			//get the parent entity from the entity properties file
			String parentEntityId = stringValueResolver.resolveStringValue(String.format("${entities.%s.extends}", treeNode.getData().getId()));

			if (!StringUtils.isEmpty(parentEntityId)) {
				DataEntity parentEntity = getLogicalEntity(parentEntityId);
				TreeNode<DataEntity> parentTreeNode = new TreeNode<>(parentEntity);

				// set the parent node to the current tree node
				treeNode.setParent(parentTreeNode);

				//set the current node to be child of the parent node
				parentTreeNode.setChaild(treeNode);

				return getSubTree(parentTreeNode);
			}
		}
		catch (Exception e){
			return treeNode;
		}

		return treeNode;


	}




	private List<TreeNode<DataEntity>> mergerEntitiesSubTrees (TreeNode<DataEntity> tree , List<TreeNode<DataEntity>> entitiesTrees )
	{

		for (TreeNode<DataEntity> subTree : entitiesTrees)
		{
			if (mergerTwoTrees(tree,subTree))
				return entitiesTrees;
		}

		entitiesTrees.add(tree);
		return entitiesTrees;


	}

	/**
	 * This method get two potential sub trees and trying to merger sub tree number 1 to sub tree number 2
	 * If those tress are complete strange (doesn't share the same root) it will return false
	 * @param tree1
	 * @param tree2
	 * @return
	 */
	private boolean mergerTwoTrees(TreeNode<DataEntity> tree1 , TreeNode<DataEntity> tree2 )
	{
		boolean result = false;

		//check if the roots are equal in that case we merger two sub trees from the same tree
		TreeNode<DataEntity> potentialParent = tree1.getData().equals(tree2.getData()) == true ? tree2 : null ;

		// if the roots are not equals the two sub  trees are not from the same tree
		if (potentialParent == null)
		{
			return result;
		}


		//find the connection point
		for (TreeNode<DataEntity> child : tree1)
		{
			TreeNode<DataEntity> exist  = tree2.peekFromTree(child);

			//we found connection point
			if (exist == null)
			{
				potentialParent.setChaild(child);
				result = true;
				break;
			}
			potentialParent = exist;
			result = mergerTwoTrees(child,potentialParent);
		}
		return result;
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


        // Peek from cahce if we already have this entity
        if(allDataEntities!=null && allDataEntities.containsKey(entityId))
        {
            return allDataEntities.get(entityId);
        }

        DataEntityConfig entityConfig = getEntityFromCache(entityId);

        DataEntity entity = new DataEntity();
        entity.setId(entityId);

        String entityName = entityConfig.getName();
        if (entityName == null){
            entityName = getExtendableValue(entityId, "name");
            entityConfig.setName(entityName);
        }

        entity.setName(entityName);

        String entityNameForMenu = entityConfig.getNameForMenu();
        if (entityNameForMenu == null){
            entityNameForMenu = getExtendableValue(entityId, "nameForMenu");
            entityConfig.setNameForMenu(entityNameForMenu);
        }

        entity.setNameForMenu(entityNameForMenu);




        String entityShortName = entityConfig.getShortName();
        if (entityShortName == null){
            entityShortName = getExtendableValue(entityId, "short_name");
            entityConfig.setShortName(entityShortName);
        }

        entity.setShortName(entityShortName);

        String entityEventsEntity = entityConfig.getEventsEntity();
        if (entityEventsEntity == null){
            entityEventsEntity = getExtendableValue(entityId, "events_entity");
            entityConfig.setEventsEntity(entityEventsEntity);
        }

        entity.setEventsEntity(entityEventsEntity);

        String entitySessionEntity = entityConfig.getSessionEntity();
        if (entitySessionEntity == null){
            entitySessionEntity = getExtendableValue(entityId, "session_entity");
            entityConfig.setSessionEntity(entitySessionEntity);
        }

        entity.setSessionEntity(entitySessionEntity);

        Boolean isAbstractEntity = entityConfig.getIsAbstractEntity();
        if (isAbstractEntity == null){
            String isAbstractEntityStr   = getExtendableValue(entityId, "is_abstract");
            isAbstractEntity = isAbstractEntityStr != null && isAbstractEntityStr.equals("true");
            entityConfig.setIsAbstractEntity(isAbstractEntity);
        }
        entity.setIsAbstract(isAbstractEntity);

        Boolean showInExplore = entityConfig.getShowInExplore();
        if (showInExplore == null){
            String showInExploreStr   = getExtendableValue(entityId, "show_in_explore");
            showInExplore = showInExploreStr != null && showInExploreStr.equals("true");
            entityConfig.setShowInExplore(showInExplore);
        }
        entity.setShowInExplore(showInExplore);

        String extendsEntity = getBaseEntityId(entityId);
        entity.setExtendsEntity(extendsEntity);

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

                String format = fieldConfig.getFormat();
                if (format == null){
                    format = getExtendableValue(entityId, "field", fieldId, "format");
                    fieldConfig.setFormat(format);
                }
                field.setFormat(format);


                List<String> valueList = fieldConfig.getValueList();
                if (valueList == null){
                    String valueListStr = getExtendableValue(entityId, "field", fieldId, "valueList");
                    if (valueListStr != null){
                        valueList = new ArrayList<String>();
                        String[] valueListArr = valueListStr.split("\\s*,[,\\s]*");
                        for(String value: valueListArr){
                            valueList.add(value);
                        }
                    }
                    fieldConfig.setValueList(valueList);
                }
                field.setValueList(valueList);


                String joinFrom = fieldConfig.getJoinFrom();
                if (joinFrom == null){
                    joinFrom = getExtendableValue(entityId, "field", fieldId, "joinFrom");
                    fieldConfig.setJoinFrom(joinFrom);
                }
                field.setJoinFrom(joinFrom);

                String joinTo = fieldConfig.getJoinTo();
                if (joinTo == null){
                    joinTo = getExtendableValue(entityId, "field", fieldId, "joinTo");
                    fieldConfig.setJoinFrom(joinTo);
                }
                field.setJoinTo(joinTo);

                
                Boolean isDefaultEnabled = fieldConfig.getDefaultEnabled();
                if (isDefaultEnabled == null){
                    String isDefaultEnabledStr = getExtendableValue(entityId, "field", fieldId, "enabledByDefault");
                    isDefaultEnabled = isDefaultEnabledStr == null || !isDefaultEnabledStr.equals("false");
                    isDefaultEnabled = isDefaultEnabled && !getFieldIsExplicit(entityId, fieldId);
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

                Boolean isTokenized = fieldConfig.isTokenized();
                if (isTokenized == null){
                    String isTokenizedStr = getExtendableValue(entityId, "field", fieldId, "is_tokenized");
                    isTokenized = isTokenizedStr != null && isTokenizedStr.equalsIgnoreCase("true");
                    fieldConfig.setIsTokenized(isTokenized);
                }
                field.setTokenized(isTokenized);

				//in case that its user entity - get the ShowOnlyForUser properite
				//this property will mark that this field suppose to be shown only at User
				String shownForSpecificEntity = fieldConfig.getShownForSpecificEntity();
				if (shownForSpecificEntity == null){
					shownForSpecificEntity = getExtendableValue(entityId, "field", fieldId, "showForSpecificEntity");

					fieldConfig.setShownForSpecificEntity(shownForSpecificEntity);
				}

				field.setShownForSpecificEntity(shownForSpecificEntity);




                Boolean isSearchable = fieldConfig.isSearchable();
                if (isSearchable == null){
                    String isSearchableStr = getExtendableValue(entityId, "field", fieldId, "searchable");
                    isSearchable = isSearchableStr != null && isSearchableStr.equalsIgnoreCase("true");
                    fieldConfig.setSearchable(isSearchable);
                }
                field.setSearchable(isSearchable);

                int rank = fieldConfig.getRank();
                if (fieldConfig.isDefaultRank()){
                    String rankStr = getExtendableValue(entityId, "field", fieldId, "rank");
                    if (rankStr != null) {
                        rank = Integer.parseInt(rankStr);
                        fieldConfig.setRank(rank);
                    }
                }
                field.setRank(rank);

                List<String> attributes = fieldConfig.getAttributes();
                if (attributes == null){
                    String attributesStr = getExtendableValue(entityId, "field", fieldId, "attributes");
                    if (attributesStr != null){
                        attributes = new ArrayList<String>();
                        String[] attributesArr = attributesStr.split("\\s*,[,\\s]*");
                        for(String attribute: attributesArr){
                            attributes.add(attribute);
                        }
                    }
                    fieldConfig.setAttributes(attributes);
                }
                field.setAttributes(attributes);

                List<String> tags = fieldConfig.getTags();
                if (tags == null){
                    String tagsStr = getExtendableValue(entityId, "field", fieldId, "tags");
                    if (tagsStr != null){
                        tags = new ArrayList<String>();
                        String[] tagsArr = tagsStr.split("\\s*,[,\\s]*");
                        for(String tag: tagsArr){
                            tags.add(tag);
                        }
                    }
                    fieldConfig.setTags(tags);
                }
                field.setTags(tags);

                fields.add(field);
            } catch(Exception error){
                throw new Exception(String.format("Can't read field %s of entity %s", fieldId, entityId));
            }
        }

        Collections.sort(fields);
        entity.setFields(fields);

        if (entityConfig.getDefaultSort() == null) {
            setDataEntityConfigDefaultSort(entityConfig, entity);
        }
        entity.setDefaultSort(entityConfig.getDefaultSort());

        return entity;
    }

    /**
     * Sets the defaultSort property of entityConfig
     * @param entityConfig
     * @param entity
     * @throws InvalidQueryException
     */
    private void setDataEntityConfigDefaultSort(DataEntityConfig entityConfig, DataEntity entity) throws InvalidQueryException{
        String entityId = entityConfig.getId();
        List<QuerySort> entityDefaultSort = new ArrayList<>();

        String defaultSortConfig = getExtendableValue(entityId, "default_sort");
        if (defaultSortConfig == null)
            throw new InvalidQueryException("DataEntity '" + entityId + "' doesn't have a default_sort property.");

        // The default_sort config property is formatted like this:
        // default_sort = field1_id [ASC/DESC][, field2_id [ASC/DESC][, field3_id [ASC/DESC]]]
        // The next code parses the config string by splitting using commas and then extracting the field and direction.
        // Note that direction is optional, ASC is the default if none was specified.
        String[] defaultSortStr = defaultSortConfig.split("\\s*,[,\\s]*");
        for(String sortStr: defaultSortStr){
            String[] sortFieldDirection = sortStr.split("\\s+");
            QuerySort sort = new QuerySort();
            DataEntityField sortField = entity.getField(sortFieldDirection[0]);
            if (sortField == null)
                throw new InvalidQueryException("Default sort field '" + sortFieldDirection[0] + "' is not found in the '" + entity.getName() + "' entity.");

            DataQueryField queryField = new DataQueryField();
            queryField.setEntity(entity.getId());
            queryField.setId(sortField.getId());
            sort.setField(queryField);

            SortDirection sortDirection;

            // Direction is specified in configuration
            if (sortFieldDirection.length > 1){
                sortDirection = SortDirection.valueOf(sortFieldDirection[1]);
                if (sortDirection == null)
                    throw new InvalidQueryException("Invalid sort direction, '" + sortFieldDirection[1] + "'.");
            }
            else
                sortDirection = SortDirection.ASC;

            sort.setDirection(sortDirection);
            entityDefaultSort.add(sort);
        }

        entityConfig.setDefaultSort(entityDefaultSort);
    }
    /**
     * Get the entity partition Strategy
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

    public Boolean getFieldIsLogicalOnly(String entityId, String fieldId) throws InvalidQueryException{
        return getFieldFlag(DataEntityFieldConfig.IS_LOGICAL_ONLY, entityId, fieldId);
    }

    public Boolean getFieldIsTokenized(String entityId, String fieldId) throws InvalidQueryException{
        return getFieldFlag(DataEntityFieldConfig.IS_TOKENIZED, entityId, fieldId);
    }

    public Boolean getFieldIsExplicit(String entityId, String fieldId) throws InvalidQueryException{
        return getFieldFlag(DataEntityFieldConfig.EXPLICIT, entityId, fieldId);
    }

	/**
	 * Return the name of the table
	 * @param entityId	The entity
	 * @param fieldId	The field
	 * @return the table name (or empty string for logical fields)
	 * @throws InvalidQueryException
	 */
	public String getFieldTable(String entityId, String fieldId) throws InvalidQueryException{
		if (getFieldIsLogicalOnly(entityId, fieldId))
			return "";

		return getEntityTable(entityId) + ".";
	}

    private Boolean getFieldFlag(String flagName, String entityId, String fieldId) throws InvalidQueryException{
        DataEntityFieldConfig fieldConfig = getFieldFromCache(entityId, fieldId);
        Boolean flagValue = fieldConfig.getFlag(flagName);
        if (flagValue != null)
            return flagValue;

        String flagValueStr = getExtendableValue(entityId, "field", fieldId, flagName);
        flagValue = flagValueStr != null && flagValueStr.equalsIgnoreCase("true");
        fieldConfig.setFlag(flagName, flagValue);
        return flagValue;

    }

    public DataEntityFieldConfig getFieldConfig(String entityId, String fieldId){
        DataEntityFieldConfig fieldConfig = getFieldFromCache(entityId, fieldId);
        return fieldConfig;
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


	//This method will get entityid and will return the DataEntity from the allDataEntites cache
	//The assumption that what doesn't exist in the cache is not a valid entity and the result will be null
    public DataEntity getEntityFromOverAllCache(String entityId)
    {
        return getEntityFromCache(entityId,allDataEntities);

    }

	//This method will get entityid and will return the DataEntity from the allLeafDataEntities cache
	//The assumption that what doesn't exist in the cache is not a valid entity and the result will be null
    public DataEntity getLeafEntityFromCache(String entityId)
    {
        return getEntityFromCache(entityId,allLeafDataEntities);

    }


	//This method will get entityid and will return the DataEntity from the allBaseDataEntities cache
	//The assumption that what doesn't exist in the cache is not a valid entity and the result will be null
    public DataEntity getBasetEntityFromCache(String entityId)
    {
        return getEntityFromCache(entityId,allBaseDataEntities);

    }

	//This method will get entityid and specific cache and will return the DataEntity from the specific cache
	//The assumption that what doesn't exist in the cache is not a valid entity and the result will be null
    private DataEntity getEntityFromCache (String entityId , HashMap<String,DataEntity> cache)
    {
       return cache.get(entityId);

    }
}
