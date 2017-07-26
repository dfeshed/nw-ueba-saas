package presidio.output.domain.records;


import java.util.Date;

public class AlertQuery {

    // filters
    private final String filterBuUserName;
    private final String filterBySeverity;
    private final long filterByStartDate;
    private final long filterByEndDate;

    // sort
    private final String sortField;
    private final boolean ascendingOrder;

    // paging
    private final int pageNumber;
    private final int pageSize;

    // aggregation
    private final boolean aggregateBySeverity;

    public AlertQuery(AlertQueryBuilder builder) {
        this.filterBuUserName = builder.filterByUserName;
        this.filterBySeverity =  builder.filterBySeverity;
        this.filterByStartDate =  builder.filterByStartDate;
        this.filterByEndDate =  builder.filterByEndDate;
        this.sortField =  builder.sortField;
        this.ascendingOrder =  builder.ascendingOrder;
        this.pageNumber =  builder.pageNumber;
        this.pageSize =  builder.pageSize;
        this.aggregateBySeverity = builder.aggregateBySeverity;
    }

    public String getFilterBuUserName() {
        return filterBuUserName;
    }

    public String getFilterBySeverity() {
        return filterBySeverity;
    }

    public long getFilterByStartDate() {
        return filterByStartDate;
    }

    public long getFilterByEndDate() {
        return filterByEndDate;
    }

    public String getSortField() {
        return sortField;
    }

    public boolean isAscendingOrder() {
        return ascendingOrder;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isAggregateBySeverity() {
        return aggregateBySeverity;
    }

    public static class AlertQueryBuilder {

        // filters
        private String filterByUserName;
        private String filterBySeverity;
        private long filterByStartDate;
        private long filterByEndDate;

        // sort
        private String sortField;
        private boolean ascendingOrder;

        // paging
        private int pageNumber = -1;
        private int pageSize = -1;

        // aggregations
        private boolean aggregateBySeverity;

        public AlertQueryBuilder() {
        }

        public AlertQueryBuilder filterByUserName(String filterBuUserName) {
            this.filterByUserName = filterBuUserName;
            return this;
        }

        public AlertQueryBuilder filterBySeverity(String filterBySeverity) {
            this.filterBySeverity = filterBySeverity;
            return this;
        }

        public AlertQueryBuilder filterByStartDate(long filterByStartDate) {
            this.filterByStartDate = filterByStartDate;
            return this;
        }

        public AlertQueryBuilder filterByEndDate(long filterByEndDate) {
            this.filterByEndDate = filterByEndDate;
            return this;
        }

        public AlertQueryBuilder sortField(String sortField, boolean ascendingOrder) {
            this.sortField = sortField;
            this.ascendingOrder = ascendingOrder;
            return this;
        }

        public AlertQueryBuilder pageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public AlertQueryBuilder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public AlertQueryBuilder aggregateBySeverity(boolean aggregateBySeverity) {
            this.aggregateBySeverity = aggregateBySeverity;
            return this;
        }

        public AlertQuery build() {
            return new AlertQuery(this);
        }
    }
}
