package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.GeoLocationConfigurationWriter;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.List;
import java.util.Map;

/**
 * Geo-location configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSGeoLocationConfigurator extends GDSBaseConfigurator {

    private static final String SOURCE_IP_CONFIG_ENTRY = "source.";
    private static final String TARGET_IP_CONFIG_ENTRY = "target.";
	private static final String OUTPUT_TOPIC_ENTRY_PARAM = "output.topic";

    public GDSGeoLocationConfigurator() {
        configurationWriterService = new GeoLocationConfigurationWriter();
    }

    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {

        List<GDSEnrichmentDefinitionState.GeoLocationState> geoLocationStates = currGDSConfigurationState.getEnrichmentDefinitionState().getGeoLocationStates();

        addConfiguration(geoLocationStates, configurationParams, GDS_CONFIG_ENTRY + SOURCE_IP_CONFIG_ENTRY);
        addConfiguration(geoLocationStates, configurationParams, GDS_CONFIG_ENTRY + TARGET_IP_CONFIG_ENTRY);

    }

    private void addConfiguration(List<GDSEnrichmentDefinitionState.GeoLocationState> geoLocationStates, Map<String, Map<String, ConfigurationParam>> configurationParams, String configurationKey) {
        Map<String, ConfigurationParam> paramsMap = configurationParams.get(configurationKey);

		String lastState = currGDSConfigurationState.getStreamingTopologyDefinitionState().getLastStateValue();
		ConfigurationParam taskName = gdsInputHandler.getParamConfiguration(paramsMap, TASK_NAME_PARAM);
		ConfigurationParam outputTopic = gdsInputHandler.getParamConfiguration(paramsMap, OUTPUT_TOPIC_PARAM);

        ConfigurationParam ipField = paramsMap.get("ipField");
        ConfigurationParam countryField = paramsMap.get("countryField");
        ConfigurationParam longitudeField = paramsMap.get("longitudeField");
        ConfigurationParam latitudeField = paramsMap.get("latitudeField");
        ConfigurationParam countryIsoCodeField = paramsMap.get("countryIsoCodeField");
        ConfigurationParam regionField = paramsMap.get("regionField");
        ConfigurationParam cityField = paramsMap.get("cityField");
        ConfigurationParam ispField = paramsMap.get("ispField");
        ConfigurationParam usageTypeField = paramsMap.get("usageTypeField");
        ConfigurationParam doSessionUpdateFlag = paramsMap.get("doSessionUpdateFlag");
        ConfigurationParam doDataBuckets = paramsMap.get("doDataBuckets");
        ConfigurationParam doGeoLocation = paramsMap.get("doGeoLocation");


        GDSEnrichmentDefinitionState.GeoLocationState geoLocationState = new GDSEnrichmentDefinitionState.GeoLocationState();

        geoLocationState.setIpField(ipField.getParamValue());
        geoLocationState.setCityField(countryField.getParamValue());
        geoLocationState.setLongitudeField(longitudeField.getParamValue());
        geoLocationState.setLatitudeField(latitudeField.getParamValue());
        geoLocationState.setCountryIsoCodeField(countryIsoCodeField.getParamValue());
        geoLocationState.setRegionField(regionField.getParamValue());
        geoLocationState.setCityField(cityField.getParamValue());
        geoLocationState.setIspField(ispField.getParamValue());
        geoLocationState.setUsageTypeField(usageTypeField.getParamValue());
        geoLocationState.setDoSessionUpdateFlag(doSessionUpdateFlag.getParamFlag());
        geoLocationState.setDoDataBuckets(doDataBuckets.getParamFlag());
        geoLocationState.setDoGeoLocation(doGeoLocation.getParamFlag());
		geoLocationState.setOutputTopicEntry(OUTPUT_TOPIC_ENTRY_PARAM);
		geoLocationState.setTaskName(taskName.getParamValue());
		geoLocationState.setLastState(lastState);
		geoLocationState.setOutputTopic(outputTopic.getParamValue());

        geoLocationStates.add(geoLocationState);

        String lastStaeClac = taskName.getParamValue();
        if (lastStaeClac.indexOf("_") != -1 )
            lastStaeClac = lastStaeClac.substring(0,lastStaeClac.indexOf("_")-1);

        currGDSConfigurationState.getStreamingTopologyDefinitionState().setLastStateValue(lastStaeClac);
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getGeoLocationStates().clear();
    }

    @Override
    public GDSConfigurationType getType() {
        return GDSConfigurationType.GEO_LOCATION;
    }
}
