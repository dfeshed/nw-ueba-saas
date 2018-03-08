package presidio.output.domain.records.alerts;


import org.springframework.data.domain.Sort;

import java.util.List;

public class    AlertQuery {

    // filters
    private final List<String> filterByUserName;
    private final List<String> filterByUserId;
    private final List<String> filterByClassification;
    private final List<String> filterBySeverity;
    private final long filterByStartDate;
    private final long filterByEndDate;
    private final List<String> filterByFeedback;
    private final double filterByMinScore;
    private final double filterByMaxScore;
    private final List<String> filterByTags;
    private final List<String> filterByAlertsIds;
    private final List<String> filterByIndicatorNames;

    // sort
    private final Sort sort;

    // paging
    private final int pageNumber;
    private final int pageSize;

    // aggregation
    private final List<String> aggregateByFields;

    public AlertQuery(AlertQueryBuilder builder) {
        this.filterByClassification = builder.filterByClassification;
        this.filterByUserName = builder.filterByUserName;
        this.filterBySeverity = builder.filterBySeverity;
        this.filterByStartDate = builder.filterByStartDate;
        this.sort = builder.sort;
        this.filterByEndDate = builder.filterByEndDate;
        this.pageNumber = builder.pageNumber;
        this.pageSize = builder.pageSize;
        this.filterByFeedback = builder.filterByFeedback;
        this.filterByMinScore = builder.filterByMinScore;
        this.filterByMaxScore = builder.filterByMaxScore;
        this.filterByTags = builder.filterByTags;
        this.filterByAlertsIds = builder.filterByAlertsIds;
        this.filterByIndicatorNames = builder.filterByIndicatorNames;
        this.filterByUserId = builder.filterByUserId;
        this.aggregateByFields = builder.aggregateByFields;
    }

    public List<String> getFilterByUserName() {
        return filterByUserName;
    }

    public List<String> getFilterByClassification() {
        return filterByClassification;
    }

    public List<String> getFilterBySeverity() {
        return filterBySeverity;
    }

    public List<String> getFilterByFeedback() {
        return filterByFeedback;
    }

    public List<String> getFilterByTags() {
        return filterByTags;
    }

    public List<String> getFilterByAlertsIds() {
        return filterByAlertsIds;
    }

    public List<String> getFilterByIndicatorNames() {
        return filterByIndicatorNames;
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

    public long getFilterByStartDate() {
        return filterByStartDate;
    }

    public long getFilterByEndDate() {
        return filterByEndDate;
    }

    public double getFilterByMinScore() {
        return filterByMinScore;
    }

    public double getFilterByMaxScore() {
        return filterByMaxScore;
    }

    public List<String> getFilterByUserId() {
        return filterByUserId;
    }

    public List<String> getAggregateByFields() {
        return aggregateByFields;
    }

    public static class AlertQueryBuilder {

        // filters
        private List<String> filterByUserName;
        private List<String> filterByClassification;
        private List<String> filterBySeverity;
        private long filterByStartDate;
        private long filterByEndDate;
        private List<String> filterByFeedback;
        private double filterByMinScore = -1;
        private double filterByMaxScore = -1;
        private List<String> filterByTags;
        private List<String> filterByAlertsIds;
        private List<String> filterByIndicatorNames;
        private List<String> filterByUserId;

        // sort
        private Sort sort;

        // paging
        private int pageNumber = -1;
        private int pageSize = -1;

        // aggregations
        private List<String> aggregateByFields;

        public AlertQueryBuilder() {
        }

        public AlertQueryBuilder filterByIndicatorNames(List<String> filterByIndicatorNames) {
            this.filterByIndicatorNames = filterByIndicatorNames;
            return this;
        }

        public AlertQueryBuilder filterByTags(List<String> filterByTags) {
            this.filterByTags = filterByTags;
            return this;
        }

        public AlertQueryBuilder filterByAlertsIds(List<String> filterByAlertsIds) {
            this.filterByAlertsIds = filterByAlertsIds;
            return this;
        }

        public AlertQueryBuilder filterByMinScore(double filterByMinScore) {
            this.filterByMinScore = filterByMinScore;
            return this;
        }

        public AlertQueryBuilder filterByMaxScore(double filterByMaxScore) {
            this.filterByMaxScore = filterByMaxScore;
            return this;
        }

        public AlertQueryBuilder filterByFeedback(List<String> filterByFeedback) {
            this.filterByFeedback = filterByFeedback;
            return this;
        }

        public AlertQueryBuilder filterByUserName(List<String> filterBuUserName) {
            this.filterByUserName = filterBuUserName;
            return this;
        }

        public AlertQueryBuilder filterByUserId(List<String> filterByUserId) {
            this.filterByUserId = filterByUserId;
            return this;
        }

        public AlertQueryBuilder filterByClassification(List<String> filterClassification) {
            this.filterByClassification = filterClassification;
            return this;
        }

        public AlertQueryBuilder filterBySeverity(List<String> filterBySeverity) {
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

        public AlertQueryBuilder sortField(Sort sort) {
            this.sort = sort;
            return this;
        }

        public AlertQueryBuilder sortField(String sortField, boolean ascendingOrder) {
            Sort.Direction dir = ascendingOrder ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = new Sort(dir, sortField);
            return sortField(sort);
        }

        public AlertQueryBuilder setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public AlertQueryBuilder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public AlertQueryBuilder aggregateByFields(List<String> aggregateByFields) {
            this.aggregateByFields = aggregateByFields;
            return this;
        }

        public AlertQuery build() {
            return new AlertQuery(this);
        }
    }
}
