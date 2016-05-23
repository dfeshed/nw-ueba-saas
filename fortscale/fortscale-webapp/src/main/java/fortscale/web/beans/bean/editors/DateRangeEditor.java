package fortscale.web.beans.bean.editors;

import fortscale.web.beans.request.DateRange;
import org.apache.commons.lang.StringUtils;

import java.beans.PropertyEditorSupport;

/**
 * Created by shays on 23/05/2016.
 */
public class DateRangeEditor extends PropertyEditorSupport {

    public void setAsText(String text) {
        long startTime = 0;
        long endTime = 0;

        if (StringUtils.isNotBlank(text)) {
            String[] dates = text.split("," );
            if (dates.length == 2) {
                try {
                    startTime= Long.parseLong(dates[0]);
                    endTime = Long.parseLong(dates[1]);
                }catch (Exception e){

                }
            }
        }

        if (startTime > 0 && endTime >0) {
            setValue(new DateRange(startTime,endTime));
        } else  {
            throw new RuntimeException("Date range is not valid");
        }
    }
}
