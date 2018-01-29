package fortscale.utils.store.record;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by maria_dorohin on 8/30/17.
 */
public class StoreManagerMetadataProperties {

    private Map<String,String> properties;

    public StoreManagerMetadataProperties(){
        this.properties = new HashMap<>();
    }

    public StoreManagerMetadataProperties(Map<String,String> properties){
        this.properties = properties;
    }


    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(String key, String value) {
        properties.put(key, value);
    }
}
