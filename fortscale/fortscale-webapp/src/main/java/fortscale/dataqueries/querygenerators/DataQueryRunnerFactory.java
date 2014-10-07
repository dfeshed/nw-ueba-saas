package fortscale.dataqueries.querygenerators;

import fortscale.dataqueries.DataQueryEntityFactory;
import fortscale.dataqueries.SupportedDBType;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.util.Map;

/**
 * Factory for creating DataQueryRunner
 */
@Component
public class DataQueryRunnerFactory implements EmbeddedValueResolverAware{

    StringValueResolver stringValueResolver;

	@Autowired
	private MySqlQueryRunner mySqlQueryRunner;

    @Autowired
    DataQueryEntityFactory entityFactory;

    private SupportedDBType getEntityDbType(String entityId){
        String type = stringValueResolver.resolveStringValue("${entities." + entityId + ".db}");
        return SupportedDBType.valueOf(type);
    }

    @Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.stringValueResolver = resolver;
	}
    
	/**
	 * Get the relevant query runner according to DTO
	 * @param dataQueryDTO the query object
	 * @return query runner
	 */
	public DataQueryRunner getDataQueryRunner(DataQueryDTO dataQueryDTO) throws Exception{
        //DataQueryEntity entity = entityFactory.getDataQueryEntity(dataQueryDTO.entities[0]);

        SupportedDBType type = getEntityDbType(dataQueryDTO.entities[0]);

        if (type == SupportedDBType.MySQL)
		    return mySqlQueryRunner;
        else
            throw new Exception("A DataQueryRunner for DB of type " + stringValueResolver.resolveStringValue("entities." + dataQueryDTO.entities[0] + ".db") + " doesn't exist.");
	}

}
