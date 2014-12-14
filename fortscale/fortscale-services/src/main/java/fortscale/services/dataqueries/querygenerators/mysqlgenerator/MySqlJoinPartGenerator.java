package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.DataEntity;
import fortscale.services.dataentity.SupportedDBType;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryJoin;
import fortscale.services.dataqueries.querydto.DataQueryJoinField;
import fortscale.services.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Generate JOINs in MySQL
 */
@Component
public class MySqlJoinPartGenerator implements QueryPartGenerator {
    @Autowired
    DataEntitiesConfig dataEntitiesConfig;

    public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException {
        if (dataQueryDTO.getJoin() == null)
            return "";

        SupportedDBType mainEntityType = dataEntitiesConfig.getEntityDbType(dataQueryDTO.getEntities()[0]);

        StringBuilder sb = new StringBuilder();
        for(DataQueryJoin join: dataQueryDTO.getJoin()){
            try {
                if (dataEntitiesConfig.getEntityDbType(join.getEntity()) != mainEntityType)
                    throw new InvalidQueryException("Entity join between " + join.getEntity() + " and " + dataQueryDTO.getEntities()[0] + " is not supported at the moment, since they use different technologies.");

                sb.append(join.getType().name()).append(" JOIN ");
                sb.append(dataEntitiesConfig.getEntityTable(join.getEntity()));
                sb.append(" ON ");
                getJoinField(join.getLeft(), sb);
                sb.append(" = ");
                getJoinField(join.getRight(), sb);
                sb.append(" ");
            }
            catch(Exception error){
                throw new InvalidQueryException(error.getMessage(), error);
            }
        }

        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    // For tests:
    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig) {
        this.dataEntitiesConfig = dataEntitiesConfig;
    }

    /**
     * Gets the physical name of a field for the JOIN clause
     * @param joinField The entity/field of the join
     * @param sb An existing StringBuilder to append the result to
     * @return
     */
    private void getJoinField(DataQueryJoinField joinField, StringBuilder sb) throws InvalidQueryException{
        String fieldColumn = dataEntitiesConfig.getFieldColumn(joinField.getEntity(), joinField.getField());
        if (dataEntitiesConfig.getFieldIsLogicalOnly(joinField.getEntity(), joinField.getField()))
            sb.append(fieldColumn);
        else
            sb.append(dataEntitiesConfig.getEntityTable(joinField.getEntity())).append(".").append(fieldColumn);
    }
}
