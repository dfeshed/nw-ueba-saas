package fortscale.domain.rest;

import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.dto.DateRange;
import fortscale.domain.rest.RestFilter;

import java.util.Set;


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
    private DateRange alertStartRange;
    private String entityName;
    private String entityTags;
    private String entityId;
    private boolean totalSeverityCount;

    private DataSourceAnomalyTypePairListWrapper indicatorTypes;


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

    public DateRange getAlertStartRange() {
        return alertStartRange;
    }

    public void setAlertStartRange(DateRange alertStartRange) {
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


    public DataSourceAnomalyTypePairListWrapper getIndicatorTypes() {
        return indicatorTypes;
    }

    public void setIndicatorTypes(DataSourceAnomalyTypePairListWrapper indicatorTypes) {
        this.indicatorTypes = indicatorTypes;
    }

    public static class DataSourceAnomalyTypePairListWrapper{
        private Set<DataSourceAnomalyTypePair> anomalyList;

        public DataSourceAnomalyTypePairListWrapper() {
        }

        public Set<DataSourceAnomalyTypePair> getAnomalyList() {
            return anomalyList;
        }

        public void setAnomalyList(Set<DataSourceAnomalyTypePair> anomalyList) {
            this.anomalyList = anomalyList;
        }
    }


    public Set<DataSourceAnomalyTypePair> getAnomalyTypesAsSet() {
        if (indicatorTypes == null){
            return null;
        } else {
            return indicatorTypes.getAnomalyList();
        }
    }


}
