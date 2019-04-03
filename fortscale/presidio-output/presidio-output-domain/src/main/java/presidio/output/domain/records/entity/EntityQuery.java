package presidio.output.domain.records.entity;

import org.springframework.data.domain.Sort;

import java.util.List;

public class EntityQuery {
    // filters
    private final List<String> filterByAlertClassifications;
    private final List<String> filterByIndicators;
    private final List<EntitySeverity> filterBySeverities;
    private final List<String> filterByUserTags;
    private final List<String> filterByUsersIds;
    private int minScore=-1;
    private int maxScore=-1;
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

    public EntityQuery(EntityQueryBuilder builder) {
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

    public List<EntitySeverity> getFilterBySeverities() {
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

    public static class EntityQueryBuilder {

        // filters
        private List<String> filterByAlertClassifications;
        private List<String> filterByIndicators;
        private List<EntitySeverity> filterBySeverities;
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

        public EntityQueryBuilder filterByUsersIds(List<String> filterByUsersIds) {
            this.filterByUsersIds = filterByUsersIds;
            return this;
        }

        public EntityQueryBuilder filterByAlertClassifications(List<String> filterByAlertClassifications) {
            this.filterByAlertClassifications = filterByAlertClassifications;
            return this;
        }

        public EntityQueryBuilder filterBySeverities(List<EntitySeverity> filterBySeverities) {
            this.filterBySeverities = filterBySeverities;
            return this;
        }

        public EntityQueryBuilder filterByIndicators(List<String> filterByIndicators) {
            this.filterByIndicators = filterByIndicators;
            return this;
        }

        public EntityQueryBuilder filterByUserTags(List<String> filterByUserTags) {
            this.filterByUserTags = filterByUserTags;
            return this;
        }

        public EntityQueryBuilder sort(Sort sort) {
            this.sort = sort;
            return this;
        }

        public EntityQueryBuilder minScore(int minScore) {
            this.minScore = minScore;
            return this;
        }

        public EntityQueryBuilder filterByUserName(String filterByUserName) {
            this.filterByUserName = filterByUserName;
            return this;
        }

        public EntityQueryBuilder filterByFreeText(String filterByFreeText) {
            this.filterByFreeText = filterByFreeText;
            return this;
        }

        public EntityQueryBuilder aggregateByFields(List<String> aggregateByFields) {
            this.aggregateByFields = aggregateByFields;
            return this;
        }

        public EntityQueryBuilder filterByUserNameWithPrefix(boolean prefixEnabled) {
            this.isPrefix = prefixEnabled;
            return this;
        }

        public EntityQueryBuilder maxScore(int maxScore) {
            this.maxScore = maxScore;
            return this;
        }

        public EntityQueryBuilder pageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public EntityQueryBuilder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public EntityQuery build() {
            return new EntityQuery(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityQuery entityQuery = (EntityQuery) o;

        if (minScore != entityQuery.minScore) return false;
        if (maxScore != entityQuery.maxScore) return false;
        if (isPrefix != entityQuery.isPrefix) return false;
        if (pageNumber != entityQuery.pageNumber) return false;
        if (pageSize != entityQuery.pageSize) return false;
        if (filterByAlertClassifications != null ? !filterByAlertClassifications.equals(entityQuery.filterByAlertClassifications) : entityQuery.filterByAlertClassifications != null)
            return false;
        if (filterByIndicators != null ? !filterByIndicators.equals(entityQuery.filterByIndicators) : entityQuery.filterByIndicators != null)
            return false;
        if (filterBySeverities != null ? !filterBySeverities.equals(entityQuery.filterBySeverities) : entityQuery.filterBySeverities != null)
            return false;
        if (filterByUserTags != null ? !filterByUserTags.equals(entityQuery.filterByUserTags) : entityQuery.filterByUserTags != null)
            return false;
        if (filterByUsersIds != null ? !filterByUsersIds.equals(entityQuery.filterByUsersIds) : entityQuery.filterByUsersIds != null)
            return false;
        if (filterByUserName != null ? !filterByUserName.equals(entityQuery.filterByUserName) : entityQuery.filterByUserName != null)
            return false;
        if (sort != null ? !sort.equals(entityQuery.sort) : entityQuery.sort != null) return false;
        return aggregateByFields != null ? aggregateByFields.equals(entityQuery.aggregateByFields) : entityQuery.aggregateByFields == null;
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
