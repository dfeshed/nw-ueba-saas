package fortscale.services.dataqueries.querygenerators;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;


import java.util.List;
import java.util.Map;

/**
 * Interface for data queries
 */
public interface DataQueryRunner {


	/**
	 * Creates query according to the DTO
	 * @param dataQueryDTO    The DTO
	 * @return The results of the Query
	 * @throws InvalidQueryException	in case we failed to parse the DTO into query
	 */
	public String generateQuery(DataQueryDTO dataQueryDTO) throws InvalidQueryException;

    /**
     * Creates a query for the DTO, but one that only counts the total available results.
     * @param dataQueryDTO
     * @return
     * @throws InvalidQueryException
     */
    public String generateTotalQuery(DataQueryDTO dataQueryDTO) throws InvalidQueryException;

	/**
	 * runs query according to the DTO
	 * @param query the string of the SQL query
	 * @return The results of the Query
	 */
	public List<Map<String, Object>> executeQuery(String query);
}
