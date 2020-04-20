package presidio.webapp.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AlertsWrapper
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-31T11:27:51.258Z")

public class MetricsWrapper {
    @JsonProperty("metrics")
    private List<Metric> metrics = new ArrayList<Metric>();

    @JsonProperty("total")
    private Integer total = null;

    @JsonProperty("page")
    private Integer page = 0;

    public MetricsWrapper alerts(List<Metric> metrics) {
        this.metrics = metrics;
        return this;
    }

    public MetricsWrapper addAlertsItem(Metric metricItem) {
        this.metrics.add(metricItem);
        return this;
    }

    /**
     * Get metrics
     *
     * @return metrics
     **/
    @ApiModelProperty(value = "")
    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public MetricsWrapper total(Integer total) {
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

    public MetricsWrapper page(Integer page) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MetricsWrapper alertsWrapper = (MetricsWrapper) o;
        return Objects.equals(this.metrics, alertsWrapper.metrics) &&
                Objects.equals(this.total, alertsWrapper.total) &&
                Objects.equals(this.page, alertsWrapper.page);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metrics, total, page);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AlertsWrapper {\n");

        sb.append("    metrics: ").append(toIndentedString(metrics)).append("\n");
        sb.append("    total: ").append(toIndentedString(total)).append("\n");
        sb.append("    page: ").append(toIndentedString(page)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

