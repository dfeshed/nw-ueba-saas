package fortscale.web.beans.request;

import fortscale.domain.core.Severity;
import fortscale.domain.core.dao.rest.Alerts;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;


/**
 * Created by shays on 04/05/2016.
 * Filter object - contains all the fields that alert can be filtered by
 * Extend {@link(RestFilter}
 *
 * This is a simple pojo
 */
public class AlertRestFilter extends RestFilter {

    private String sortField;
    private String sortDirection;

    private String severity;
    private String status;
    private String feedback;
    @Size(min=2, max = 2, message = "The range should contain exactly to dates seperated with comma")
    private List<Date> alertStartRange;
    private String entityName;
    private String entityTags;
    private String entityId;
    private boolean totalSeverityCount;




    private String indicatorTypes;

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }



    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public List<Date> getAlertStartRange() {
        return alertStartRange;
    }



    public void setAlertStartRange(List<Date> alertStartRange) {
        this.alertStartRange = alertStartRange;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityTags() {
        return entityTags;
    }

    public void setEntityTags(String entityTags) {
        this.entityTags = entityTags;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public boolean isTotalSeverityCount() {
        return totalSeverityCount;
    }

    public void setTotalSeverityCount(boolean totalSeverityCount) {
        this.totalSeverityCount = totalSeverityCount;
    }

    public String getIndicatorTypes() {
        return indicatorTypes;
    }

    public void setIndicatorTypes(String indicatorTypes) {
        this.indicatorTypes = indicatorTypes;
    }



}
