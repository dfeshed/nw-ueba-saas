package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.DataEntity;
import fortscale.services.dataentity.SupportedDBType;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryJoin;
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

        StringBuilder sb = new StringBuilder();
        for(DataQueryJoin join: dataQueryDTO.getJoin()){
            try {
                if (dataEntitiesConfig.getEntityDbType(join.getEntity()) != SupportedDBType.MySQL)
                    continue;

                if (join.getJoinFields().size() != 2)
                    throw new InvalidQueryException("Invalid JOIN. Expected exactly 2 fields, got " + join.getJoinFields().size());

                sb.append(join.getType().name()).append(" JOIN ");
                sb.append(dataEntitiesConfig.getEntityTable(join.getEntity()));
                sb.append(" ON ");
                getJoinField(join.getJoinFields().get(0).getEntity(), join.getJoinFields().get(0).getField(), sb);
                sb.append(" = ");
                getJoinField(join.getJoinFields().get(1).getEntity(), join.getJoinFields().get(1).getField(), sb);
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
     * @param entityId The logical name of the JOIN's entity
     * @param fieldId The logical name of the JOIN's field
     * @param sb An existing StringBuilder to append the result to
     * @return
     */
    private void getJoinField(String entityId, String fieldId, StringBuilder sb) throws InvalidQueryException{
        String fieldColumn = dataEntitiesConfig.getFieldColumn(entityId, fieldId);
        if (dataEntitiesConfig.getFieldIsLogicalOnly(entityId, fieldId))
            sb.append(fieldColumn);
        else
            sb.append(dataEntitiesConfig.getEntityTable(entityId)).append(".").append(fieldColumn);
    }
}
