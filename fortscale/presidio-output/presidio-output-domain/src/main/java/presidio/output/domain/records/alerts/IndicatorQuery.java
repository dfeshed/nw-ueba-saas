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

        private Sort sort;
        private int pageNumber;
        private int pageSize;
        private String filterByAlertsId;

        public IndicatorQueryBuilder(Sort sort) {
        }

        IndicatorQueryBuilder setSort(Sort sort) {
            this.sort = sort;
            return this;
        }

        IndicatorQueryBuilder setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        IndicatorQueryBuilder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        IndicatorQueryBuilder setFilterByAlertsId(String filterByAlertsId) {
            this.filterByAlertsId = filterByAlertsId;
            return this;
        }


        public IndicatorQuery builde() {
            return new IndicatorQuery(this);
        }
    }
}
