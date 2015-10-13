package com.rsa.asoc.sa.ui.threat.domain.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Domain object representing an incident
 *
 * @author Jay Garala
 * @since 10.6.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Incident {

    private String id;
    private String name;
    private String summary;
    private Priority priority;
    private Integer prioritySort;
    private int alertCount;

    /** Average risk score of the alerts associated with this incident */
    private int averageAlertRiskScore;

    /** Set by fired rules (e.g. ESA) and will be 0 on manually created incidents */
    private int riskScore;

    private String createdBy;
    private Instant created;
    private Instant lastUpdated;
    private Person lastUpdatedByUser;

    private Set<String> sources;

    private Person assignee;
    private IncidentStatus status;
    private Integer statusSort;
    private List<Category> categories;

    private List<JournalEntry> notes;

    private long totalRemediationTaskCount;
    private long openRemediationTaskCount;
    private boolean hasRemediationTasks;

    private boolean sealed;

    private String ruleId;
    private Instant firstAlertTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Integer getPrioritySort() {
        return prioritySort;
    }

    public void setPrioritySort(Integer prioritySort) {
        this.prioritySort = prioritySort;
    }

    public int getAlertCount() {
        return alertCount;
    }

    public void setAlertCount(int alertCount) {
        this.alertCount = alertCount;
    }

    public int getAverageAlertRiskScore() {
        return averageAlertRiskScore;
    }

    public void setAverageAlertRiskScore(int averageAlertRiskScore) {
        this.averageAlertRiskScore = averageAlertRiskScore;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Person getLastUpdatedByUser() {
        return lastUpdatedByUser;
    }

    public void setLastUpdatedByUser(Person lastUpdatedByUser) {
        this.lastUpdatedByUser = lastUpdatedByUser;
    }

    public Set<String> getSources() {
        return sources;
    }

    public void setSources(Set<String> sources) {
        this.sources = sources;
    }

    public Person getAssignee() {
        return assignee;
    }

    public void setAssignee(Person assignee) {
        this.assignee = assignee;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public Integer getStatusSort() {
        return statusSort;
    }

    public void setStatusSort(Integer statusSort) {
        this.statusSort = statusSort;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<JournalEntry> getNotes() {
        return notes;
    }

    public void setNotes(List<JournalEntry> notes) {
        this.notes = notes;
    }

    public long getTotalRemediationTaskCount() {
        return totalRemediationTaskCount;
    }

    public void setTotalRemediationTaskCount(long totalRemediationTaskCount) {
        this.totalRemediationTaskCount = totalRemediationTaskCount;
    }

    public long getOpenRemediationTaskCount() {
        return openRemediationTaskCount;
    }

    public void setOpenRemediationTaskCount(long openRemediationTaskCount) {
        this.openRemediationTaskCount = openRemediationTaskCount;
    }

    public boolean isHasRemediationTasks() {
        return hasRemediationTasks;
    }

    public void setHasRemediationTasks(boolean hasRemediationTasks) {
        this.hasRemediationTasks = hasRemediationTasks;
    }

    public boolean isSealed() {
        return sealed;
    }

    public void setSealed(boolean sealed) {
        this.sealed = sealed;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public Instant getFirstAlertTime() {
        return firstAlertTime;
    }

    public void setFirstAlertTime(Instant firstAlertTime) {
        this.firstAlertTime = firstAlertTime;
    }
}
