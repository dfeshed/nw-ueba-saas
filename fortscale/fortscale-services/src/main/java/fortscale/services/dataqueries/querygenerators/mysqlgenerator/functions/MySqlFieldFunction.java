package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Yossi on 04/11/2014.
 * An interface for MySQL functions
 */
public abstract class MySqlFieldFunction {
    @Autowired
    protected DataEntitiesConfig dataEntitiesConfig;

    public abstract String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException;

    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig){
        this.dataEntitiesConfig = dataEntitiesConfig;
    }
}
