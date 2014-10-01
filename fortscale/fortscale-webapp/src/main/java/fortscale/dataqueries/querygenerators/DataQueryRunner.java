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
	 * Creates and runs query according to the DTO
	 * @param dataQueryDTO    The DTO
	 * @param useCache	true if we want to use the cache
	 * @return The results of the Query
	 * @throws InvalidQueryException	in case we failed to parse the DTO into query
	 */
	DataBean<List<Map<String, Object>>> runQuery(
					DataQueryDTO dataQueryDTO, boolean useCache) throws
					InvalidQueryException;
}
