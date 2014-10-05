package fortscale.dataqueries.querygenerators;

import fortscale.dataqueries.DataQueryEntity;
import fortscale.dataqueries.DataQueryEntityFactory;
import fortscale.dataqueries.SupportedDBType;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory for creating DataQueryRunner
 */
@Component
public class DataQueryRunnerFactory {

	@Autowired
	private MySqlQueryRunner mySqlQueryRunner;

    @Autowired
    DataQueryEntityFactory entityFactory;

	/**
	 * Get the relevant query runner according to DTO
	 * @param dataQueryDTO the query object
	 * @return query runner
	 */
	public DataQueryRunner getDataQueryRunner(DataQueryDTO dataQueryDTO) throws Exception{
        DataQueryEntity entity = entityFactory.getDataQueryEntity(dataQueryDTO.entities[0]);

        if (entity.getDbType() == SupportedDBType.MySQL)
		    return mySqlQueryRunner;

        throw new Exception("A DataQueryRunner for DB of type " + entity.getDbType().name() + " doesn't exist.");
	}

}
