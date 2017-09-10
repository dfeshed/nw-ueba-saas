package presidio.output.domain.records.users;


import org.springframework.data.domain.Sort;

import java.util.List;

public class UserQuery {

    // filters
    private final List<String> filterByAlertClassifications;
    private final List<String> filterByIndicators;
    private List<UserSeverity> filterBySeverities;
    private List<String> filterByUsersIds;
    private int minScore = -1;
    private int maxScore = -1;
    private boolean isPrefix;
    private Boolean isAdmin;
    private String filterByUserName;

    // sort
    private final Sort sort;


    // paging
    private final int pageNumber;
    private final int pageSize;

    public UserQuery(UserQueryBuilder builder) {
        this.filterByAlertClassifications = builder.filterByAlertClassifications;
        this.filterByIndicators = builder.filterByIndicators;
        this.filterBySeverities = builder.filterBySeverities;
        this.filterByUsersIds = builder.filterByUsersIds;

        //Sort
        this.sort = builder.sort;
        this.pageNumber = builder.pageNumber;
        this.pageSize = builder.pageSize;
        this.minScore = builder.minScore;
        this.maxScore = builder.maxScore;

        this.filterByUserName = builder.filterByUserName;
        this.isPrefix = builder.isPrefix;
        this.isAdmin = builder.isAdmin;
    }

    public List<String> getFilterByUsersIds() {
        return filterByUsersIds;
    }


    public List<String> getFilterByAlertClassifications() {
        return filterByAlertClassifications;
    }

    public List<String> getFilterByIndicators() {
        return filterByIndicators;
    }

    public List<UserSeverity> getFilterBySeverities() {
        return filterBySeverities;
    }

    public String getFilterByUserName() {
        return filterByUserName;
    }

    public boolean isPrefix() {
        return isPrefix;
    }

    public Sort getSort() {
        return sort;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }


    public Boolean getFilterByIsAdmin() {
        return isAdmin;
    }

    public static class UserQueryBuilder {

        // filters
        private List<String> filterByAlertClassifications;
        private List<String> filterByIndicators;
        private List<UserSeverity> filterBySeverities;
        private List<String> filterByUsersIds;

        private int minScore = -1;
        private int maxScore = -1;
        private String filterByUserName;
        private boolean isPrefix = false;
        private Boolean isAdmin;


        // sort
        private Sort sort;

        // paging
        private int pageNumber = -1;
        private int pageSize = -1;

        public UserQueryBuilder filterByUsersIds(List<String> filterByUsersIds) {
            this.filterByUsersIds = filterByUsersIds;
            return this;
        }

        public UserQueryBuilder filterByAlertClassifications(List<String> filterByAlertClassifications) {
            this.filterByAlertClassifications = filterByAlertClassifications;
            return this;
        }

        public UserQueryBuilder filterBySeverities(List<UserSeverity> filterBySeverities) {
            this.filterBySeverities = filterBySeverities;
            return this;
        }

        public UserQueryBuilder filterByIndicators(List<String> filterByIndicators) {
            this.filterByIndicators = filterByIndicators;
            return this;
        }

        public UserQueryBuilder sort(Sort sort) {
            this.sort = sort;
            return this;
        }

        public UserQueryBuilder sortField(Sort sort) {
            this.sort = sort;
            return this;
        }

        public UserQueryBuilder minScore(int minScore) {
            this.minScore = minScore;
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


        public UserQueryBuilder filterByUserAdmin(Boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }


        public UserQueryBuilder maxScore(int maxScore) {
            this.maxScore = maxScore;
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
