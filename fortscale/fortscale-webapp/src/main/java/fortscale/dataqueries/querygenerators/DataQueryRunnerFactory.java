package fortscale.dataqueries.querygenerators;

import fortscale.dataqueries.DataEntitiesConfig;
import fortscale.dataqueries.SupportedDBType;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

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
