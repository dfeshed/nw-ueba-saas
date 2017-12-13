package presidio.output.domain.records.alerts;

import org.springframework.data.domain.Sort;

import static presidio.output.domain.records.alerts.Indicator.SCORE_CONTRIBUTION;


public class IndicatorQuery {

    // sort
    private final Sort sort;
    private final int pageNumber;
    private final int pageSize;
    private final String filterByAlertsId;

    IndicatorQuery(IndicatorQueryBuilder indicatorQueryBuilder) {
        this.pageNumber = indicatorQueryBuilder.pageNumber;
        this.pageSize = indicatorQueryBuilder.pageSize;
        this.filterByAlertsId = indicatorQueryBuilder.filterByAlertsId;
        this.sort = indicatorQueryBuilder.sort;
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

    public String getFilterByAlertsId() {
        return filterByAlertsId;
    }

    public static class IndicatorQueryBuilder {

        private final String SORT_FIELD = SCORE_CONTRIBUTION;

        private Sort sort;
        private int pageNumber;
        private int pageSize;
        private String filterByAlertsId;

        public IndicatorQueryBuilder() {
        }

        IndicatorQueryBuilder sort(Sort sort) {
            this.sort = sort;
            return this;
        }

        public IndicatorQueryBuilder sortField() {
            Sort sort = new Sort(Sort.Direction.DESC, SORT_FIELD);
            return sort(sort);
        }

        IndicatorQueryBuilder pageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        IndicatorQueryBuilder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        IndicatorQueryBuilder filterByAlertsId(String filterByAlertsId) {
            this.filterByAlertsId = filterByAlertsId;
            return this;
        }


        public IndicatorQuery builde() {
            return new IndicatorQuery(this);
        }
    }
}
