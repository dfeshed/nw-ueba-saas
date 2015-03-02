package fortscale.services.dataqueries.querygenerators;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.DataEntity;
import fortscale.services.dataentity.DataEntityConfig;
import fortscale.services.dataentity.QueryFieldFunction;
import fortscale.services.dataqueries.querydto.*;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.utils.TreeNode;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for data queries
 */
public abstract class DataQueryRunner {


	/**
	 * Creates query according to the DTO
	 * @param dataQueryDTO    The DTO
	 * @return The results of the Query
	 * @throws InvalidQueryException	in case we failed to parse the DTO into query
	 */
	public abstract String generateQuery(DataQueryDTO dataQueryDTO) throws InvalidQueryException;

    /**
     * Generates a query for a DTO, but only for retrieving the total records available.
     * It does so by removing the limit, offset and sort properties of the DTO and replacing the fields with
     * a single COUNT(*) as 'total' field.
     * @param dataQueryDTO The original DTO
     * @return
     * @throws InvalidQueryException
     */
    public String generateTotalQuery(DataQueryDTO dataQueryDTO) throws InvalidQueryException {
        // Create a copy of the DataQueryDTO:
        DataQueryDTO totalDataQueryDTO = new DataQueryDTO(dataQueryDTO);

        // Create the count(*) field:
        DataQueryField countField = new DataQueryField();
        countField.setAlias("total");
        FieldFunction countFunction = new FieldFunction();
        countFunction.setName(QueryFieldFunction.count);
        HashMap<String, String> countParams = new HashMap<>();
        countParams.put("all", "true");
        countFunction.setParams(countParams);
        countField.setFunc(countFunction);

        // The set fields new fields to the totalDTO:
        ArrayList<DataQueryField> totalFields = new ArrayList<>();
        totalFields.add(countField);
        totalDataQueryDTO.setFields(totalFields);

        // Remove limit, offset and sort from DTO:
        totalDataQueryDTO.setLimit(0);
        totalDataQueryDTO.setOffset(0);
        totalDataQueryDTO.setSort(null);

        // Finally, generate the query and return it:
        return generateQuery(totalDataQueryDTO);
    }

	/**
	 * This method will get data query object and in case of abstract data query she will translate it to
	 * list of leaf data queries
	 * @param dataQueryDTO - the input data query
	 * @return
	 */
	public List<DataQueryDTO> translateAbstarctDataQuery(DataQueryDTO dataQueryDTO,DataEntitiesConfig dataEntitiesConfig)  throws Exception
	{

		List<TreeNode<DataEntity>> entitiestree = dataEntitiesConfig.getEntitiesTrees();


		List<DataQueryDTO> result = new ArrayList<>();

        result.addAll(translateTheMainEntity(dataQueryDTO, entitiestree,dataEntitiesConfig));


        for (DataQueryDTO dto : result) {
            translateTheJoins(dto, entitiestree, dataEntitiesConfig);
        }




        for (DataQueryDTO dto : result) {
            subQueryTransaltion.addAll(translateTheSubQueries(dto));
        }








		return result;

	}


	/**
	 * This method will translate the main entity in the given data query DTO in to a list of data queries DTOs that will based on the leaf successor entities  from the abstract entity
	 * @param dto
	 * @param entitiestrees
	 * @return
	 */
	private List<DataQueryDTO> translateTheMainEntity(DataQueryDTO dto , List<TreeNode<DataEntity>> entitiestrees,DataEntitiesConfig dataEntitiesConfig)
	{
        List<DataQueryDTO> result = new ArrayList<>();


       //The assumption is that we have one "main" entity to each data query request!!!
        for (String entityId : dto.getEntities()) {

            DataEntity dataEntity = dataEntitiesConfig.getBasetEntityFromCache(entityId);

            if (dataEntity != null) {

                TreeNode<DataEntity> abstractEntity = new TreeNode<>(dataEntity);

                // for each entities tree in the tree list bring all the leaf successor of that entity
                for (TreeNode<DataEntity> tree : entitiestrees )
                {
                    TreeNode<DataEntity> subTree  = tree.peekFromTree(abstractEntity);

                    //in case that the abstract entity exist in the current entities tree
                    if (subTree != null){


                        ArrayList<TreeNode<DataEntity>> children = subTree.getChildrens();

                        //in case that the subtree is a leaf (doesn't have children in the tree)
                        if (children.size() == 0 )
                        {
                            result.add(dto);
                            break;
                        }

                        for (TreeNode<DataEntity> dataEntityTreeNode : children)
                        {
                            DataQueryDTO childDto = new DataQueryDTO(dto);
                            String[] entities = new String[1];
                            entities[0] = dataEntityTreeNode.getData().getId();
                            result.add(childDto);
                        }

                        break;
                    }
                }

            }

        }

		return result;

	}

    /**
     * This method will translate each data query dto  with abstract entity in the join to list of data query dto with leaf entities
     * @param dto
     * @return
     */
    private DataQueryDTO translateTheJoins(DataQueryDTO dto,List<TreeNode<DataEntity>> entitiestrees,DataEntitiesConfig dataEntitiesConfig){

        //this list will mark which data query joins need to remove from the list cause they are base entities
        List<DataQueryJoin> listToRemove = new ArrayList<>();
        //this list will mark which data query joins need to add to  the list cause they are leaf entities
        List<DataQueryJoin> listToAdd = new ArrayList<>();


        for (DataQueryJoin dataQueryJoin : dto.getJoin())
        {
            DataEntity dataEntity = dataEntitiesConfig.getBasetEntityFromCache(dataQueryJoin.getEntity());
            if (dataEntity != null) {

                TreeNode<DataEntity> abstractEntity = new TreeNode<>(dataEntity);

                // for each entities tree in the tree list bring all the leaf successor of that entity
                for (TreeNode<DataEntity> tree : entitiestrees )
                {
                    TreeNode<DataEntity> subTree  = tree.peekFromTree(abstractEntity);

                    //in case that the abstract entity exist in the current entities tree
                    if (subTree != null){
                        ArrayList<TreeNode<DataEntity>> children = subTree.getChildrens();

                        //in case that the subtree is a leaf (doesn't have children in the tree)
                        if (children.size() == 0 )
                            break;


                        listToRemove.add(dataQueryJoin);

                        for (TreeNode<DataEntity> dataEntityTreeNode : children)
                        {

                            DataQueryJoin  leafEntityJoin = new DataQueryJoin(dataQueryJoin);
                            leafEntityJoin.setEntity(dataEntityTreeNode.getData().getId());
                            listToAdd.add(leafEntityJoin);

                        }

                        break;
                    }
                }
            }

        }
        dto.getJoin().addAll(listToAdd);
        dto.getJoin().removeAll(listToRemove);
        return dto;
    }

    /**
     * This method will translate each data query dto with abstract entity in the subquery to subquery with list of leaf entities with the combained method between them
     * @param dto
     * @param entitiestrees
     * @param dataEntitiesConfig
     * @return
     */
    private DataQueryDTO translateTheSubQueries (DataQueryDTO dto,List<TreeNode<DataEntity>> entitiestrees,DataEntitiesConfig dataEntitiesConfig)
    {

        MultipleDataQueryDTO subQuery =  dto.getSubQuery();


        //this list will mark which data query in the sub query that need to remove from the list cause they are base entities
        List<DataQueryDTO> listToRemove = new ArrayList<>();
        //this list will mark which data query in the sub query  need to add to the list cause they are leaf entities
        List<DataQueryDTO> listToAdd = new ArrayList<>();

        for (DataQueryDTO dataQueryDTO : subQuery.getDataQueries())
        {
            //the assumption that each data query dto have only one main entity!!!
            //also we don't support yet with recursive sub query translation
            DataEntity dataEntity = dataEntitiesConfig.getBasetEntityFromCache(dataQueryDTO.getEntities()[0]);
            if (dataEntity != null) {

                TreeNode<DataEntity> abstractEntity = new TreeNode<>(dataEntity);

                // for each entities tree in the tree list bring all the leaf successor of that entity
                for (TreeNode<DataEntity> tree : entitiestrees )
                {
                    TreeNode<DataEntity> subTree  = tree.peekFromTree(abstractEntity);

                    //in case that the abstract entity exist in the current entities tree
                    if (subTree != null){
                        ArrayList<TreeNode<DataEntity>> children = subTree.getChildrens();

                        //in case that the subtree is a leaf (doesn't have children in the tree)
                        if (children.size() == 0 )
                            break;


                        listToRemove.add(dataQueryJoin);

                        for (TreeNode<DataEntity> dataEntityTreeNode : children)
                        {

                            DataQueryJoin  leafEntityJoin = new DataQueryJoin(dataQueryJoin);
                            leafEntityJoin.setEntity(dataEntityTreeNode.getData().getId());
                            listToAdd.add(leafEntityJoin);

                        }

                        break;
                    }
                }
            }

        }
        dto.getJoin().addAll(listToAdd);
        dto.getJoin().removeAll(listToRemove);
        return dto;
    }


	/**
	 * runs query according to the DTO
	 * @param query the string of the SQL query
	 * @return The results of the Query
	 */
	public abstract List<Map<String, Object>> executeQuery(String query);
}
