package fortscale.domain.core.dao;

import java.util.List;

/**
 * Created by Amir Keren on 09/09/15.
 *
 * This POJO class is used as the result of the mongo groupby query - groupByTags()
 */
public class TagCount {

    public static String COUNT_FIELD = "total";

    private List<String> tags;
    private long total;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}