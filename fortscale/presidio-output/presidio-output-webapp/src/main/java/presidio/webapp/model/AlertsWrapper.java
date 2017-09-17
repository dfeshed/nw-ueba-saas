package presidio.webapp.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * AlertsWrapper
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-31T11:27:51.258Z")

public class AlertsWrapper {
    @JsonProperty("alerts")
    private List<Alert> alerts = new ArrayList<Alert>();

    @JsonProperty("total")
    private Integer total = null;

    @JsonProperty("page")
    private Integer page = 0;

    @JsonProperty("aggregationData")
    private Map<String, Map<String, Long>> aggregationData;

    public AlertsWrapper alerts(List<Alert> alerts) {
        this.alerts = alerts;
        return this;
    }

    public AlertsWrapper addAlertsItem(Alert alertsItem) {
        this.alerts.add(alertsItem);
        return this;
    }

    /**
     * Get alerts
     *
     * @return alerts
     **/
    @ApiModelProperty(value = "")
    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public AlertsWrapper total(Integer total) {
        this.total = total;
        return this;
    }

    /**
     * Get total
     * minimum: 0
     *
     * @return total
     **/
    @ApiModelProperty(value = "")
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public AlertsWrapper page(Integer page) {
        this.page = page;
        return this;
    }

    /**
     * Get page
     * minimum: 0
     *
     * @return page
     **/
    @ApiModelProperty(value = "")
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public AlertsWrapper aggregationData(Map<String, Map<String, Long>> aggregationData) {
        this.aggregationData = aggregationData;
        return this;
    }

    @ApiModelProperty(value = "")
    public Map<String, Map<String, Long>> getAggregationData() {
        return aggregationData;
    }

    public void setAggregationData(Map<String, Map<String, Long>> aggregationData) {
        this.aggregationData = aggregationData;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AlertsWrapper alertsWrapper = (AlertsWrapper) o;
        return Objects.equals(this.alerts, alertsWrapper.alerts) &&
                Objects.equals(this.total, alertsWrapper.total) &&
                Objects.equals(this.page, alertsWrapper.page) &&
                Objects.equals(this.aggregationData, alertsWrapper.aggregationData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alerts, total, page);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AlertsWrapper {\n");

        sb.append("    alerts: ").append(toIndentedString(alerts)).append("\n");
        sb.append("    total: ").append(toIndentedString(total)).append("\n");
        sb.append("    page: ").append(toIndentedString(page)).append("\n");
        sb.append("    aggregationData: ").append(toIndentedString(aggregationData)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

