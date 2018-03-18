package fortscale.domain.core.dao.rest;

import java.util.List;
import java.util.Map;

/**
 * Created by shays on 02/10/2017.
 */
public class Events {

    private List<Map<String, Object>> data;
    int total;

    public Events(List<Map<String, Object>> data, int total) {
        this.data = data;
        this.total = total;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public int getTotal() {
        return total;
    }
}
