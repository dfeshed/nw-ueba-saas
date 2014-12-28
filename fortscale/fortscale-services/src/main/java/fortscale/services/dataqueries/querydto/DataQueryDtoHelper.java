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
    /**
     * Gets the ID of the main entity from a DataQueryDTO. Required for fields that don't explicitly specify their entity, and for validations.
     * Note - it's a single entity since it's the MAIN entity. All other entities should be specified explicitly by fields and JOINs.
     * @param dataQueryDTO
     * @return
     */
    public String getEntityId(DataQueryDTO dataQueryDTO){
        if (dataQueryDTO.getEntities() != null && dataQueryDTO.getEntities().length != 0)
            return dataQueryDTO.getEntities()[0];

        if (dataQueryDTO.getSubQuery() != null){
            if (!dataQueryDTO.getSubQuery().getDataQueries().isEmpty()){
                for(DataQueryDTO subDataQuery: dataQueryDTO.getSubQuery().getDataQueries()){
                    String entityId = getEntityId(subDataQuery);
                    if (entityId != null)
                        return entityId;
                }
            }
        }

        return null;
    }
}
