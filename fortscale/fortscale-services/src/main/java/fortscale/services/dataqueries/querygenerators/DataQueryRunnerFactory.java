package fortscale.services.dataqueries.querygenerators;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.SupportedDBType;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory for creating DataQueryRunner
 */
@Component
public class DataQueryRunnerFactory{

    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

	@Autowired
	private MySqlQueryRunner mySqlQueryRunner;

	/**
	 * Get the relevant query runner according to DTO
	 * @param dataQueryDTO the query object
	 * @return query runner
	 */
	public DataQueryRunner getDataQueryRunner(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
		if (dataQueryDTO.getEntities().length > 1)
			throw new InvalidQueryException("Queries with more than one entity are not supported yet.");
		
        SupportedDBType type = dataEntitiesConfig.getEntityDbType(dataQueryDTO.getEntities()[0]);

        if (type == SupportedDBType.MySQL)
		    return mySqlQueryRunner;
        else
            throw new InvalidQueryException("The DB type for entity " + dataQueryDTO.getEntities()[0] + " is not supported.");
	}

}
