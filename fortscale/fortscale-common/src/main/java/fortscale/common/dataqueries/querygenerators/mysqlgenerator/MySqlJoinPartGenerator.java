package fortscale.common.dataqueries.querygenerators.mysqlgenerator;

import fortscale.common.dataentity.SupportedDBType;
import fortscale.common.dataqueries.querydto.DataQueryDTO;
import fortscale.common.dataqueries.querydto.DataQueryJoin;
import fortscale.common.dataqueries.querydto.DataQueryJoinField;
import fortscale.common.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.stereotype.Component;

/**
 * Generate JOINs in MySQL
 */
@Component
public class MySqlJoinPartGenerator extends QueryPartGenerator {
    public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException {
        if (dataQueryDTO.getJoin() == null)
            return "";

        String entityId = dataQueryDtoHelper.getEntityId(dataQueryDTO);
        SupportedDBType mainEntityType = dataEntitiesConfig.getEntityDbType(entityId);

        StringBuilder sb = new StringBuilder();
        for(DataQueryJoin join: dataQueryDTO.getJoin()){
            try {
                if (dataEntitiesConfig.getEntityDbType(join.getEntity()) != mainEntityType)
                    throw new InvalidQueryException("Entity join between " + join.getEntity() + " and " + entityId + " is not supported at the moment, since they use different technologies.");

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
