package fortscale.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

/**
 * Created by Yossi on 04/11/2014.
 * An interface for MySQL functions
 */
public interface MySqlFieldFunction {
    public String generateSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException;
}
