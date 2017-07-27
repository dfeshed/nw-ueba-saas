package presidio.monitoring.aspect.metrics;

import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.actuate.metrics.Metric;

import java.util.HashSet;
import java.util.Set;

public class JsonObjectMetric<T extends Number> extends Metric{

    private Set tags;
    private String unit;

    public JsonObjectMetric(String name, Number value, Set tags,String unit) {
        super(name, value);
        this.tags=tags;
        this.unit=unit;
    }

    public Set getTags() {
        return tags;
    }

    public String getUnit() {
        return unit;
    }

    public JSONObject getObject(){
        JSONArray tags= new JSONArray().put(this.getTags());
        JSONObject  data = new JSONObject();
        data.put("value",this.getValue());
        data.put("unit",this.getUnit());
        data.put("tags",tags);
        return data;
    }
}
