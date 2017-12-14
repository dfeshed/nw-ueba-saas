package fortscale.utils.elasticsearch;

import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.List;

public class ScrolledPage<T> extends AggregatedPageImpl<T> {

    private String scrollId;

    public ScrolledPage(List<T> content, Pageable pageable, long total, Aggregations aggregations, String scrollId) {
        super(content, pageable, total, aggregations);
        this.scrollId = scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public String getScrollId() {
        return scrollId;
    }
}