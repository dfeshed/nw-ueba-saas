package fortscale.services.dataqueries.querygenerators;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;

/**
 * Created by Yossi on 23/12/2014.
 * Query part generator which takes a single DataQueryDTO.
 */
public abstract class SingleQueryPartGenerator extends QueryPartGenerator<DataQueryDTO> {
    public abstract String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException;
}
