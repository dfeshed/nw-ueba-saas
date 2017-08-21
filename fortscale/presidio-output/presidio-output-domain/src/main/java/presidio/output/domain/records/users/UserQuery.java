package presidio.output.domain.records.users;


import java.util.List;

public class UserQuery {

    // filters
    private final List<String> filterByAlertClassifications;
    private final List<String> filterByIndicators;

    // sort
    private final String sortField;
    private final boolean ascendingOrder;

    // paging
    private final int pageNumber;
    private final int pageSize;

    public UserQuery(UserQueryBuilder builder) {
        this.filterByAlertClassifications = builder.filterByAlertClassifications;
        this.filterByIndicators = builder.filterByIndicators;
        this.sortField =  builder.sortField;
        this.ascendingOrder =  builder.ascendingOrder;
        this.pageNumber =  builder.pageNumber;
        this.pageSize =  builder.pageSize;
    }

    public List<String> getFilterByAlertClassifications() {
        return filterByAlertClassifications;
    }

    public List<String> getFilterByIndicators() {
        return filterByIndicators;
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



    public static class UserQueryBuilder {

        // filters
        private List<String> filterByAlertClassifications;
        private List<String> filterByIndicators;

        // sort
        private String sortField;
        private boolean ascendingOrder;

        // paging
        private int pageNumber = -1;
        private int pageSize = -1;

        public UserQueryBuilder filterByAlertClassifications(List<String> filterByAlertClassifications) {
            this.filterByAlertClassifications = filterByAlertClassifications;
            return this;
        }

        public UserQueryBuilder filterByIndicators(List<String> filterByIndicators) {
            this.filterByIndicators = filterByIndicators;
            return this;
        }

        public UserQueryBuilder sortField(String sortField, boolean ascendingOrder) {
            this.sortField = sortField;
            this.ascendingOrder = ascendingOrder;
            return this;
        }

        public UserQueryBuilder pageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public UserQueryBuilder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public UserQuery build() {
            return new UserQuery(this);
        }
    }
}
