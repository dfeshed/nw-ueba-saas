package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.GeoLocationConfiguration;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.Map;

/**
 * Geo-location configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSGeoLocationConfigurator extends GDSBaseConfigurator {

    public GDSGeoLocationConfigurator() {
        configurationService = new GeoLocationConfiguration();
    }

    @Override
    public void configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        GDSEnrichmentDefinitionState.GeoLocationState geoLocationState = currGDSConfigurationState.getGDSEnrichmentDefinitionState().getGeoLocationState();

        ConfigurationParam ipField = configurationParams.get("ipField");
        ConfigurationParam countryField = configurationParams.get("countryField");
        ConfigurationParam longitudeField = configurationParams.get("longitudeField");
        ConfigurationParam latitudeField = configurationParams.get("latitudeField");
        ConfigurationParam countryIsoCodeField = configurationParams.get("countryIsoCodeField");
        ConfigurationParam regionField = configurationParams.get("regionField");
        ConfigurationParam cityField = configurationParams.get("cityField");
        ConfigurationParam ispField = configurationParams.get("ispField");
        ConfigurationParam usageTypeField = configurationParams.get("usageTypeField");
        ConfigurationParam doSessionUpdateFlag = configurationParams.get("doSessionUpdateFlag");
        ConfigurationParam doDataBuckets = configurationParams.get("doDataBuckets");
        ConfigurationParam doGeoLocation = configurationParams.get("doGeoLocation");

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

        configurationService.setGDSConfigurationState(currGDSConfigurationState);
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getGDSEnrichmentDefinitionState().getGeoLocationState().reset();
    }
}
