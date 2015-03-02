package fortscale.services.dataqueries.querygenerators;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.DataEntity;
import fortscale.services.dataentity.DataEntityConfig;
import fortscale.services.dataentity.QueryFieldFunction;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querydto.FieldFunction;
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

		
		List<DataQueryDTO> result = null;

		for (String entityId : dataQueryDTO.getEntities()) {

			DataEntity lightDataEntity = new DataEntity(entityId);

			result = translateTheMainEntity(dataQueryDTO, entitiestree);

			List<DataQueryDTO> joinTranslation = new ArrayList<>();

			for (DataQueryDTO dto : result) {
				//joinTranslation.addAll(translateTheJoins(dto));
			}

			result.addAll(joinTranslation);

			List<DataQueryDTO> subQueryTransaltion = new ArrayList<>();
			for (DataQueryDTO dto : result) {
				//subQueryTransaltion.addAll(translateTheSubQueries(dto));
			}

			result = subQueryTransaltion;
		}






		return result;

	}

	/**
	 * This method will translate the main entity in the given data query DTO in to a list of data queries DTOs that will based on the leaf successor entities  from the abstract entity
	 * @param dto
	 * @param entitiestrees
	 * @return
	 */
	private List<DataQueryDTO> translateTheMainEntity(DataQueryDTO dto , List<TreeNode<DataEntity>> entitiestrees)
	{
		List<DataQueryDTO> result = null;
		TreeNode<DataEntity> abstractEntity = new TreeNode<>(new DataEntity())

        // for each entities tree in the tree list bring all the leaf successor of that entity
        for (TreeNode<DataEntity> tree : entitiestrees )
        {
			TreeNode<DataEntity> subTree  = tree.peekFromTree(tree);

			//in case that the
            if (subTree != null){
				ArrayList<TreeNode<DataEntity>> children = subTree.getChildrens();
            }
        }





		return result;

	}


	/**
	 * runs query according to the DTO
	 * @param query the string of the SQL query
	 * @return The results of the Query
	 */
	public abstract List<Map<String, Object>> executeQuery(String query);
}
