package fortscale.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.dataqueries.DataEntitiesConfig;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Yossi on 04/11/2014.
 * An interface for MySQL functions
 */
public abstract class MySqlFieldFunction {
    @Autowired
    protected DataEntitiesConfig dataEntitiesConfig;

    public abstract String generateSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException;

    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig){
        this.dataEntitiesConfig = dataEntitiesConfig;
    }
}
