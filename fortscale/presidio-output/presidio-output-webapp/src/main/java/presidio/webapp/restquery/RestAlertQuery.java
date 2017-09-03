package presidio.webapp.restquery;

import io.swagger.annotations.ApiModel;
import org.springframework.data.domain.Sort;

import java.util.List;

@ApiModel("AlertQuery")
public class RestAlertQuery {
    // filters
    private String userName;
    private List<String> classification;
    private String severity;
    private long startDate;
    private long endDate;
    private String feedback;
    private double minScore;
    private double maxScore;
    private List<String> tags;
    private List<String> alertsIds;
    private List<String> indicatorNams;

    // sort
    private Sort sort;

    // paging
    private int pageNumber;
    private int pageSize;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getClassification() {
        return classification;
    }

    public void setClassification(List<String> classification) {
        this.classification = classification;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setAlertsIds(List<String> alertsIds) {
        this.alertsIds = alertsIds;
    }

    public void setIndicatorNams(List<String> indicatorNams) {
        this.indicatorNams = indicatorNams;
    }

    public String getFeedback() {
        return feedback;
    }

    public double getMinScore() {
        return minScore;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getAlertsIds() {
        return alertsIds;
    }

    public List<String> getIndicatorNams() {
        return indicatorNams;
    }
}
