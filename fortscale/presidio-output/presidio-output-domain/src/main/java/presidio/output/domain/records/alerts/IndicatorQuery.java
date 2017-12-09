package presidio.output.domain.records.alerts;

import org.springframework.data.domain.Sort;


public class IndicatorQuery {

    // sort
    private final Sort sort;

    IndicatorQuery(IndicatorQueryBuilder indicatorQueryBuilder) {
        this.sort = indicatorQueryBuilder.sort;
    }

    public static class IndicatorQueryBuilder {

        private Sort sort;

        public IndicatorQueryBuilder(Sort sort) {
        }

        IndicatorQueryBuilder setSort(Sort sort) {
            this.sort = sort;
            return this;
        }

        public IndicatorQuery builde() {
            return new IndicatorQuery(this);
        }
    }
}
