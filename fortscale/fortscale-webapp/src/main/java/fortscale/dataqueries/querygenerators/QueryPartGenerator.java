package fortscale.dataqueries.querygenerators;

import org.springframework.stereotype.Component;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

/**
 * Generate part of query
 */
public interface QueryPartGenerator {

	/**
	 * Generates part of query
	 * @param dataQueryDTO	the DTO we want to parse onto a query
	 * @return the part of the query
	 * @throws InvalidQueryException in case we failed to parse the DTO into the query type
	 */
	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException;

}
