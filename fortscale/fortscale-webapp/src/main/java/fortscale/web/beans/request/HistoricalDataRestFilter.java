package fortscale.web.beans.request;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Min;

/**
 * Created by shays on 08/06/2016.
 */
public class HistoricalDataRestFilter {

    @NotEmpty
    private String contextType;

    @NotEmpty
    private String contextValue;

    @NotEmpty
    private String feature;

    @NotEmpty
    private String function;

    @Min(1) //Can be null, but if not null must be greate or equals to 1
    private Integer numColumns = null;


    Sort.Direction sortDirection = Sort.Direction.DESC; //Default value DESC

    int timeRange = 90; // Time range in days


    public String getContextType() {
        return contextType;
    }

    public void setContextType(String contextType) {
        this.contextType = contextType;
    }

    public String getContextValue() {
        return contextValue;
    }

    public void setContextValue(String contextValue) {
        this.contextValue = contextValue;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Integer getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(Integer numColumns) {
        this.numColumns = numColumns;
    }

    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Sort.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public int getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(int timeRange) {
        this.timeRange = timeRange;
    }
}
