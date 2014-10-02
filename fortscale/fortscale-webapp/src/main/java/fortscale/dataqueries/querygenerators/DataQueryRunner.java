package fortscale.dataqueries.querygenerators;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.web.beans.DataBean;

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
	 * runs query according to the DTO
	 * @param query
	 * @return The results of the Query
	 */
	public DataBean<List<Map<String, Object>>> executeQuery(String query);
}
