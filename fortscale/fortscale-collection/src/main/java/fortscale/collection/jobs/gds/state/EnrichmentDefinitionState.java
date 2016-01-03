package fortscale.collection.jobs.gds.state;

import fortscale.services.configuration.ConfigurationParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gils
 * 31/12/2015
 */
public class EnrichmentDefinitionState implements Resettable{
    private Map<String, ConfigurationParam> paramsMap = new HashMap<>();

    public Map<String, ConfigurationParam> getParamsMap() {
        return paramsMap;
    }

    @Override
    public void reset() {
        paramsMap.clear();
    }
}
