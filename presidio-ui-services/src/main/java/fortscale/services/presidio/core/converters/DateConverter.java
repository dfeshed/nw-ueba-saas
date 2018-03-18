package fortscale.services.presidio.core.converters;

import fortscale.domain.dto.DateRange;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import presidio.output.client.model.AlertQuery;

import java.math.BigDecimal;

/**
 * Created by shays on 10/09/2017.
 */
public class DateConverter {

    public void updateDateRangeParamsOnQuery(DateRange dateRangeFilter, AlertQuery alertQuery) {
        if (dateRangeFilter!=null) {
            long startTimeSeconds = TimestampUtils.convertToMilliSeconds(dateRangeFilter.getFromTime());
            long endTimeSeconds   = TimestampUtils.convertToMilliSeconds(dateRangeFilter.getToTime());

            alertQuery.setStartTimeFrom(new BigDecimal(startTimeSeconds));
            alertQuery.setStartTimeTo(new BigDecimal(endTimeSeconds));
        }
    }



    public long convertResponseTimeToUiTime(BigDecimal time) {
        return TimestampUtils.convertToMilliSeconds(time.longValue());
    }

}
