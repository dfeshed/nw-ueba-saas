package presidio.output.domain.records.users;


import java.util.List;

public class UserQuery {

    // filters
    private final List<String> filterByAlertClassifications;
    private final List<String> filterByIndicators;
    private String filterByUserId;
    private String filterByUserName;
    private boolean isPrefix;

    // sort
    private final List<String> sortField;
    private final boolean ascendingOrder;

    // paging
    private final int pageNumber;
    private final int pageSize;

    public UserQuery(UserQueryBuilder builder) {
        this.filterByAlertClassifications = builder.filterByAlertClassifications;
        this.filterByIndicators = builder.filterByIndicators;
        this.sortField = builder.sortField;
        this.ascendingOrder = builder.ascendingOrder;
        this.pageNumber = builder.pageNumber;
        this.pageSize = builder.pageSize;
        this.filterByUserId = builder.filterByUserId;
        this.filterByUserName = builder.filterByUserName;
        this.isPrefix = builder.isPrefix;
    }

    public String getFilterByUserId() {
        return filterByUserId;
    }

    public String getFilterByUserName() {
        return filterByUserName;
    }

    public boolean isPrefix() {
        return isPrefix;
    }

    public List<String> getFilterByAlertClassifications() {
        return filterByAlertClassifications;
    }

    public List<String> getFilterByIndicators() {
        return filterByIndicators;
    }

    public List<String> getSortField() {
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
        private String filterByUserId;
        private String filterByUserName;
        private boolean isPrefix;


        // sort
        private List<String> sortField;
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

        public UserQueryBuilder filterByUserName(String filterByUserName) {
            this.filterByUserName = filterByUserName;
            return this;
        }

        public UserQueryBuilder filterByUserNameWithPrefix(boolean prefixEnabled) {
            this.isPrefix = prefixEnabled;
            return this;
        }

        public UserQueryBuilder filterByUserId(String filterbyUserId) {
            this.filterByUserId = filterbyUserId;
            return this;
        }

        public UserQueryBuilder sortField(List<String> sortField, boolean ascendingOrder) {
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
