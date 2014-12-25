package fortscale.services.dataqueries.querydto;

import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlMultipleQueryGenerator;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Yossi on 24/12/2014.
 * Static methods for common operations on DataQueryDTOs
 */
@Component
public class DataQueryDtoHelper {
    @Autowired
    MySqlMultipleQueryGenerator mySqlMultipleQueryGenerator;

    // For tests:
    public void setMySqlMultipleQueryGenerator(MySqlMultipleQueryGenerator mySqlMultipleQueryGenerator) {
        this.mySqlMultipleQueryGenerator = mySqlMultipleQueryGenerator;
    }

    public String getSubQuerySql(MultipleDataQueryDTO subQuery) throws InvalidQueryException {
        StringBuilder stringbuilder = new StringBuilder("(");
        stringbuilder.append(mySqlMultipleQueryGenerator.generateQueryPart(subQuery));
        stringbuilder.append(") as t1");
        return stringbuilder.toString();
    }

    public String getEntityId(DataQueryDTO dataQueryDTO){
        if (dataQueryDTO.getEntities() != null && dataQueryDTO.getEntities().length != 0)
            return dataQueryDTO.getEntities()[0];

        if (dataQueryDTO.getSubQuery() != null){
            if (dataQueryDTO.getSubQuery().getDataQueries().size() != 0){
                for(DataQueryDTO subDataQuery: dataQueryDTO.getSubQuery().getDataQueries()){
                    String entityId = getEntityId(dataQueryDTO.getSubQuery().getDataQueries().get(0));
                    if (entityId != null)
                        return entityId;
                }
            }
        }

        return null;
    }
}
