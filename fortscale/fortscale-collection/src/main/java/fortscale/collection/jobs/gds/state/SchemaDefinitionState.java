package fortscale.collection.jobs.gds.state;

import fortscale.services.configuration.ConfigurationParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gils
 * 31/12/2015
 */
public class SchemaDefinitionState implements Resettable{
    private Map<String, ConfigurationParam> paramsMap = new HashMap<>();

    public Map<String, ConfigurationParam> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, ConfigurationParam> paramsMap) {
        this.paramsMap = paramsMap;
    }

    @Override
    public void reset() {
        paramsMap.clear();
    }
}
