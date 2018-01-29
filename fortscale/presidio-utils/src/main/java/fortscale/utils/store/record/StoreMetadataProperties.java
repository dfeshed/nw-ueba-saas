package fortscale.utils.store.record;


import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by maria_dorohin on 8/30/17.
 */
public class StoreMetadataProperties {

    private Map<String,String> properties;

    public StoreMetadataProperties(){
        this.properties = new HashMap<>();
    }

    public StoreMetadataProperties(Map<String,String> properties){
        Assert.assertNotNull(properties);
        this.properties = properties;
    }


    public String getProperty(String key) {
        return properties.get(key);
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StoreMetadataProperties storeMetadataProperties = (StoreMetadataProperties) o;
        return this.properties.equals(storeMetadataProperties.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

}
