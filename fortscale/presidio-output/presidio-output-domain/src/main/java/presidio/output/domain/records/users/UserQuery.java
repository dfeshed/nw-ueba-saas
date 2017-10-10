package presidio.output.domain.records.users;


import org.springframework.data.domain.Sort;

import java.util.List;

public class UserQuery {

    // filters
    private final List<String> filterByAlertClassifications;
    private final List<String> filterByIndicators;
    private final List<UserSeverity> filterBySeverities;
    private final List<String> filterByUserTags;
    private final List<String> filterByUsersIds;
    private int minScore;
    private int maxScore;
    private boolean isPrefix;
    private String filterByUserName;
    private String filterByFreeText;

    // sort
    private final Sort sort;

    // aggregation
    private final List<String> aggregateByFields;

    // paging
    private final int pageNumber;
    private final int pageSize;

    public UserQuery(UserQueryBuilder builder) {
        // filter
        this.filterByAlertClassifications = builder.filterByAlertClassifications;
        this.filterByIndicators = builder.filterByIndicators;
        this.filterBySeverities = builder.filterBySeverities;
        this.filterByUsersIds = builder.filterByUsersIds;
        this.filterByUserTags = builder.filterByUserTags;
        this.filterByUserName = builder.filterByUserName;
        this.filterByFreeText = builder.filterByFreeText;
        this.isPrefix = builder.isPrefix;
        this.minScore = builder.minScore;
        this.maxScore = builder.maxScore;

        //Sort
        this.sort = builder.sort;

        // page
        this.pageNumber = builder.pageNumber;
        this.pageSize = builder.pageSize;

        // aggregate
        this.aggregateByFields = builder.aggregateByFields;
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

    public String getFilterByFreeText() {
        return filterByFreeText;
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

    public List<String> getFilterByUserTags() {
        return filterByUserTags;
    }

    public List<String> getAggregateByFields() {
        return aggregateByFields;
    }

    public static class UserQueryBuilder {

        // filters
        private List<String> filterByAlertClassifications;
        private List<String> filterByIndicators;
        private List<UserSeverity> filterBySeverities;
        private List<String> filterByUsersIds;
        private List<String> filterByUserTags;

        private int minScore = -1;
        private int maxScore = -1;
        private String filterByUserName;
        private String filterByFreeText;
        private boolean isPrefix = false;

        // sort
        private Sort sort;

        // aggregation
        private List<String> aggregateByFields;

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

        public UserQueryBuilder filterByUserTags(List<String> filterByUserTags) {
            this.filterByUserTags = filterByUserTags;
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

        public UserQueryBuilder filterByFreeText(String filterByFreeText) {
            this.filterByFreeText = filterByFreeText;
            return this;
        }

        public UserQueryBuilder aggregateByFields(List<String> aggregateByFields) {
            this.aggregateByFields = aggregateByFields;
            return this;
        }

        public UserQueryBuilder filterByUserNameWithPrefix(boolean prefixEnabled) {
            this.isPrefix = prefixEnabled;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserQuery userQuery = (UserQuery) o;

        if (minScore != userQuery.minScore) return false;
        if (maxScore != userQuery.maxScore) return false;
        if (isPrefix != userQuery.isPrefix) return false;
        if (pageNumber != userQuery.pageNumber) return false;
        if (pageSize != userQuery.pageSize) return false;
        if (filterByAlertClassifications != null ? !filterByAlertClassifications.equals(userQuery.filterByAlertClassifications) : userQuery.filterByAlertClassifications != null)
            return false;
        if (filterByIndicators != null ? !filterByIndicators.equals(userQuery.filterByIndicators) : userQuery.filterByIndicators != null)
            return false;
        if (filterBySeverities != null ? !filterBySeverities.equals(userQuery.filterBySeverities) : userQuery.filterBySeverities != null)
            return false;
        if (filterByUserTags != null ? !filterByUserTags.equals(userQuery.filterByUserTags) : userQuery.filterByUserTags != null)
            return false;
        if (filterByUsersIds != null ? !filterByUsersIds.equals(userQuery.filterByUsersIds) : userQuery.filterByUsersIds != null)
            return false;
        if (filterByUserName != null ? !filterByUserName.equals(userQuery.filterByUserName) : userQuery.filterByUserName != null)
            return false;
        if (sort != null ? !sort.equals(userQuery.sort) : userQuery.sort != null) return false;
        return aggregateByFields != null ? aggregateByFields.equals(userQuery.aggregateByFields) : userQuery.aggregateByFields == null;
    }

    @Override
    public int hashCode() {
        int result = filterByAlertClassifications != null ? filterByAlertClassifications.hashCode() : 0;
        result = 31 * result + (filterByIndicators != null ? filterByIndicators.hashCode() : 0);
        result = 31 * result + (filterBySeverities != null ? filterBySeverities.hashCode() : 0);
        result = 31 * result + (filterByUserTags != null ? filterByUserTags.hashCode() : 0);
        result = 31 * result + (filterByUsersIds != null ? filterByUsersIds.hashCode() : 0);
        result = 31 * result + minScore;
        result = 31 * result + maxScore;
        result = 31 * result + (isPrefix ? 1 : 0);
        result = 31 * result + (filterByUserName != null ? filterByUserName.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        result = 31 * result + (aggregateByFields != null ? aggregateByFields.hashCode() : 0);
        result = 31 * result + pageNumber;
        result = 31 * result + pageSize;
        return result;
    }
}
