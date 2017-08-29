package presidio.output.domain.records.users;


import org.springframework.data.domain.Sort;

import java.util.List;

public class UserQuery {

    // filters
    private final List<String> filterByAlertClassifications;
    private final List<String> filterByIndicators;
    private Integer minScore;
    private Integer maxScore;

    // sort
    private final Sort sort;


    // paging
    private final int pageNumber;
    private final int pageSize;

    public UserQuery(UserQueryBuilder builder) {
        this.filterByAlertClassifications = builder.filterByAlertClassifications;
        this.filterByIndicators = builder.filterByIndicators;
        this.sort = builder.sort;

        this.pageNumber =  builder.pageNumber;
        this.pageSize =  builder.pageSize;
        this.minScore = builder.minScore;
        this.maxScore = builder.maxScore;
    }

    public List<String> getFilterByAlertClassifications() {
        return filterByAlertClassifications;
    }

    public List<String> getFilterByIndicators() {
        return filterByIndicators;
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

    public Integer getMinScore() {
        return minScore;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public static class UserQueryBuilder {

        // filters
        private List<String> filterByAlertClassifications;
        private List<String> filterByIndicators;
        private Integer minScore;
        private Integer maxScore;


        // sort
        private Sort sort;

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

        public UserQueryBuilder sort(Sort sort) {
            this.sort=sort;
            return this;
        }

        public UserQueryBuilder sortField(String sortField, boolean ascendingOrder) {
            Sort.Direction dir=ascendingOrder? Sort.Direction.ASC: Sort.Direction.DESC;
            Sort sort = new Sort(dir,sortField);
            return sort(sort);
        }

        public UserQueryBuilder minScore(Integer minScore) {
            this.minScore = minScore;
            return this;
        }

        public UserQueryBuilder maxScore(Integer maxScore) {
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
