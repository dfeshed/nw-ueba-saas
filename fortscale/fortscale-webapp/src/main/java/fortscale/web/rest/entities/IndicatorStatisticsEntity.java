package fortscale.web.rest.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shays on 08/10/2015.
 */
public class IndicatorStatisticsEntity {

    private int indicatorCountLast7Days;
    private int indicatorCountLastDay;

    @JsonProperty("indicator_count_last_7_days")
    public int getIndicatorCountLast7Days() {
        return indicatorCountLast7Days;
    }

    public void setIndicatorCountLast7Days(int indicatorCountLast7Days) {
        this.indicatorCountLast7Days = indicatorCountLast7Days;
    }

    @JsonProperty("indicator_count_last_day")
    public int getIndicatorCountLastDay() {
        return indicatorCountLastDay;
    }

    public void setIndicatorCountLastDay(int indicatorCountLastDay) {
        this.indicatorCountLastDay = indicatorCountLastDay;
    }
}
