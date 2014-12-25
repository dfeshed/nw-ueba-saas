package fortscale.services.dataqueries.querygenerators;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryDtoHelper;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlFieldGenerator;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlMultipleQueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Generate part of query
 */
public abstract class QueryPartGenerator<T> {

    @Autowired
    protected DataEntitiesConfig dataEntitiesConfig;

    @Autowired
    protected MySqlFieldGenerator mySqlFieldGenerator;

    @Autowired
    protected MySqlMultipleQueryGenerator mySqlMultipleQueryGenerator;

    @Autowired
    protected DataQueryDtoHelper dataQueryDtoHelper;

    public void setDataQueryDtoHelper(DataQueryDtoHelper dataQueryDtoHelper) {
        this.dataQueryDtoHelper = dataQueryDtoHelper;
    }

    /**
     * Set the dataEntitiesConfig, used by tests
     * @param dataEntitiesConfig
     */
    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig){
        this.dataEntitiesConfig = dataEntitiesConfig;
    }

    public MySqlFieldGenerator getMySqlFieldGenerator() {
        return mySqlFieldGenerator;
    }

    /**
     * Set MySQL field generator, used by tests
     * @param mySqlFieldGenerator
     */
    public void setMySqlFieldGenerator(MySqlFieldGenerator mySqlFieldGenerator) {
        this.mySqlFieldGenerator = mySqlFieldGenerator;
    }

	/**
	 * Generates part of query
	 * @param querySource	the DTO we want to parse onto a query
	 * @return the part of the query
	 * @throws InvalidQueryException in case we failed to parse the DTO into the query type
	 */
	public abstract String generateQueryPart(T querySource) throws InvalidQueryException;

}
