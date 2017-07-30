package presidio.webapp.restquery;

import java.time.Instant;

public class RestAlertQuery {
    // filters
    private String filterBuUserName;
    private String filterBySeverity;
    private Instant filterByStartDate;
    private Instant filterByEndDate;

    // sort
    private String sortField;
    private boolean ascendingOrder;

    // paging
    private int pageNumber;
    private int pageSize;

    public String getFilterBuUserName() {
        return filterBuUserName;
    }

    public void setFilterBuUserName(String filterBuUserName) {
        this.filterBuUserName = filterBuUserName;
    }

    public String getFilterBySeverity() {
        return filterBySeverity;
    }

    public void setFilterBySeverity(String filterBySeverity) {
        this.filterBySeverity = filterBySeverity;
    }

    public Instant getFilterByStartDate() {
        return filterByStartDate;
    }

    public void setFilterByStartDate(Instant filterByStartDate) {
        this.filterByStartDate = filterByStartDate;
    }

    public Instant getFilterByEndDate() {
        return filterByEndDate;
    }

    public void setFilterByEndDate(Instant filterByEndDate) {
        this.filterByEndDate = filterByEndDate;
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
