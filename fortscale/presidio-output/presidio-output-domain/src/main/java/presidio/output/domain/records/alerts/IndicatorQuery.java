package presidio.output.domain.records.alerts;

import org.springframework.data.domain.Sort;


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

    public static class IndicatorQueryBuilder {

        private final String SORT_FIELD = "";

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
