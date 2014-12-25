package fortscale.services.dataqueries.querygenerators;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.SupportedDBType;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryDtoHelper;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;

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
