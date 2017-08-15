package presidio.webapp.restquery;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@ApiModel("AlertQuery")
public class RestAlertQuery implements Serializable {
    // filters

    private String userName;
    //TODO: should be ENUM
    private Set<String> severities;


    private Instant startDate;
    private Instant endDate;

    // sort
    private String sortField;
    private boolean ascendingOrder;

    // paging
    private int pageNumber;
    private int pageSize;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<String> getSeverities() {
        return severities;
    }

    public void setSeverities(Set<String> severities) {
        this.severities = severities;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public boolean isAscendingOrder() {
        return ascendingOrder;
    }

    public void setAscendingOrder(boolean ascendingOrder) {
        this.ascendingOrder = ascendingOrder;
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
}
