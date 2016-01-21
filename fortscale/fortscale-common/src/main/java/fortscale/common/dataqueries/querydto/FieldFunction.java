package fortscale.common.dataqueries.querydto;

import fortscale.common.dataentity.QueryFieldFunction;

import java.util.Map;

/**
* Created by Yossi on 10/11/2014.
*/
public class FieldFunction {
    private QueryFieldFunction name;
    private Map<String, String> params;

    public QueryFieldFunction getName() {
        return name;
    }

    public void setName(QueryFieldFunction name) {
        this.name = name;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
