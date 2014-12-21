package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Yossi on 18/12/2014.
 */
@Component
public class MySqlFunctionDateDiff extends MySqlFieldFunction {
    final static String MISSING_START_PARAM_ERROR = "datediff function requires either a startDateField param or field ID.";

    private enum StartEnd{
        start, end
    }

    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException {
        StringBuilder sb = new StringBuilder();
        String entityId = field.getEntity();

        if (entityId == null)
            entityId = dataQueryDTO.getEntities()[0];

        if (field.getFunc().getParams() == null)
            throw new InvalidQueryException(MISSING_START_PARAM_ERROR);

        String startDate = getParamDateValue(field, entityId, StartEnd.start);
        if (startDate == null)
            throw new InvalidQueryException(MISSING_START_PARAM_ERROR);

        String endDate = getParamDateValue(field, entityId, StartEnd.end);
        if (endDate == null)
            endDate = "to_date(now())";

        sb.append("datediff(");
        sb.append(endDate).append(", ").append(startDate);
        sb.append(")");

        return sb.toString();
    }

    /**
     * Get the SQL for the start or end param of the datediff
     * @param field
     * @param entityId
     * @param startOrEnd
     * @return
     * @throws InvalidQueryException
     */
    private String getParamDateValue(DataQueryField field, String entityId, StartEnd startOrEnd) throws InvalidQueryException{
        String dateField = field.getFunc().getParams().get(startOrEnd + "DateField");
        String dateValue;

        if (dateField != null)
            return dataEntitiesConfig.getFieldColumn(entityId, dateField);
        else {
            String dateParamValue = field.getFunc().getParams().get(startOrEnd + "DateValue");
            if (dateParamValue != null) {
                long timestampLong;
                try {
                    timestampLong = new Long(dateParamValue);
                }
                catch(Exception error){
                    throw new InvalidQueryException("Invalid timestamp for datediff param: '" + dateParamValue + "'.");
                }

                return "to_date(" + timestampLong + ")";
            }
        }

        return null;
    }
}
