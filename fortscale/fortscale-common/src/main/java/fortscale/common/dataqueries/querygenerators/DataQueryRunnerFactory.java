package fortscale.common.dataqueries.querygenerators;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataentity.SupportedDBType;
import fortscale.common.dataqueries.querydto.DataQueryDTO;
import fortscale.common.dataqueries.querydto.DataQueryDtoHelper;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory for creating DataQueryRunner
 */
@Component
public class DataQueryRunnerFactory {

	@Autowired
	private DataEntitiesConfig dataEntitiesConfig;

	@Autowired
	private MySqlQueryRunner mySqlQueryRunner;

    @Autowired
    DataQueryDtoHelper dataQueryDtoHelper;

	/**
	 * Get the relevant query runner according to DTO
	 * 
	 * @param dataQueryDTO
	 *            the query object
	 * @return query runner
	 */
	public DataQueryRunner getDataQueryRunner(DataQueryDTO dataQueryDTO)
			throws InvalidQueryException {
        String firstEntityId = dataQueryDtoHelper.getEntityId(dataQueryDTO);
        if (firstEntityId == null)
            throw new InvalidQueryException("Can't create dataQuery runner, no entity found.");

		SupportedDBType type = dataEntitiesConfig.getEntityDbType(firstEntityId);

		if (type == SupportedDBType.MySQL)
			return mySqlQueryRunner;
		else
			throw new InvalidQueryException("The DB type for entity '" + firstEntityId + "' is not supported.");
	}
}
