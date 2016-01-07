package fortscale.collection.jobs.gds.input.populators.enrichment;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.HashMap;
import java.util.Map;

/**
 * Geo-location command line populator
 *
 * @author gils
 * 03/01/2016
 */
public class GDSGeoLocationCLIPopulator implements GDSConfigurationPopulator{

    private static final String TASK_NAME_PARAM = "taskName";
    private static final String OUTPUT_TOPIC_PARAM = "outputTopic";
    private static final String IP_FIELD_PARAM = "ipField";
    private static final String COUNTRY_FIELD_PARAM = "countryField";
    private static final String LONGITUDE_FIELD_PARAM = "longitudeField";
    private static final String LATITUDE_FIELD_PARAM = "latitudeField";
    private static final String COUNTRY_ISO_CODE_FIELD_PARAM = "countryIsoCodeField";
    private static final String REGION_FIELD_PARAM = "regionField";
    private static final String CITY_FIELD_PARAM = "cityField";
    private static final String ISP_FIELD_PARAM = "ispField";
    private static final String USAGE_TYPE_FIELD_PARAM = "usageTypeField";
    private static final String DO_SESSSION_UPDATE_FLAG_PARAM = "doSessionUpdateFlag";
    private static final String DO_DATA_BUCKETS_PARAM = "doDataBuckets";
    private static final String DO_GEO_LOCATION_PARAM = "doGeoLocation";
    private static final String LAST_STATE_PARAM = "lastState";

    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {

        Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        if(currentConfigurationState.getStreamingTopologyDefinitionState().isSourceIpGeoLocationRequired()) {

            System.out.println(String.format("Going to configure the source ip at GeoLocation task for %s", dataSourceName));
            paramsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "source_VpnEnrichTask"));

            if(currentConfigurationState.getStreamingTopologyDefinitionState().isTargetIpGeoLocationRequired()) {
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-source-ip-geolocated"));
            }
            else {
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-ip-geolocated"));
            }

            paramsMap.put(IP_FIELD_PARAM, new ConfigurationParam(IP_FIELD_PARAM,false,"${impala.data.%s.table.field.source_ip}"));
            paramsMap.put(COUNTRY_FIELD_PARAM, new ConfigurationParam(COUNTRY_FIELD_PARAM,false,"src_country"));
            paramsMap.put(LONGITUDE_FIELD_PARAM, new ConfigurationParam(LONGITUDE_FIELD_PARAM,false,"src_longtitudeField"));
            paramsMap.put(LATITUDE_FIELD_PARAM, new ConfigurationParam(LATITUDE_FIELD_PARAM,false,"src_latitudeField"));
            paramsMap.put(COUNTRY_ISO_CODE_FIELD_PARAM, new ConfigurationParam(COUNTRY_ISO_CODE_FIELD_PARAM,false,"src_countryIsoCodeField"));
            paramsMap.put(REGION_FIELD_PARAM, new ConfigurationParam(REGION_FIELD_PARAM,false,"src_regionField"));
            paramsMap.put(CITY_FIELD_PARAM, new ConfigurationParam(CITY_FIELD_PARAM,false,"src_cityField"));
            paramsMap.put(ISP_FIELD_PARAM, new ConfigurationParam(ISP_FIELD_PARAM,false,"src_ispField"));
            paramsMap.put(USAGE_TYPE_FIELD_PARAM, new ConfigurationParam(USAGE_TYPE_FIELD_PARAM,false,"src_usageTypeField"));
            paramsMap.put(DO_SESSSION_UPDATE_FLAG_PARAM, new ConfigurationParam(DO_SESSSION_UPDATE_FLAG_PARAM,false,""));
            paramsMap.put(DO_DATA_BUCKETS_PARAM, new ConfigurationParam(DO_DATA_BUCKETS_PARAM,false,""));
            paramsMap.put(DO_GEO_LOCATION_PARAM, new ConfigurationParam(DO_GEO_LOCATION_PARAM,true,""));

            paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "VpnEnrichTask"));
        }

        //Target Geo Location
        if(currentConfigurationState.getStreamingTopologyDefinitionState().isTargetIpGeoLocationRequired()) {

            System.out.println(String.format("Going to configure the target ip at  GeoLocation task for %s", dataSourceName));
            paramsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "target_VpnEnrichTask"));

            paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-ip-geolocated"));

            paramsMap.put(IP_FIELD_PARAM, new ConfigurationParam(IP_FIELD_PARAM,false,"${impala.data.%s.table.field.target}"));
            paramsMap.put(COUNTRY_FIELD_PARAM, new ConfigurationParam(COUNTRY_FIELD_PARAM,false,"dst_country"));
            paramsMap.put(LONGITUDE_FIELD_PARAM, new ConfigurationParam(LONGITUDE_FIELD_PARAM,false,"dst_longtitudeField"));
            paramsMap.put(LATITUDE_FIELD_PARAM, new ConfigurationParam(LATITUDE_FIELD_PARAM,false,"dst_latitudeField"));
            paramsMap.put(COUNTRY_ISO_CODE_FIELD_PARAM, new ConfigurationParam(COUNTRY_ISO_CODE_FIELD_PARAM,false,"dst_countryIsoCodeField"));
            paramsMap.put(REGION_FIELD_PARAM, new ConfigurationParam(REGION_FIELD_PARAM,false,"dst_regionField"));
            paramsMap.put(CITY_FIELD_PARAM, new ConfigurationParam(CITY_FIELD_PARAM,false,"dst_cityField"));
            paramsMap.put(ISP_FIELD_PARAM, new ConfigurationParam(ISP_FIELD_PARAM,false,"dst_ispField"));
            paramsMap.put(USAGE_TYPE_FIELD_PARAM, new ConfigurationParam(USAGE_TYPE_FIELD_PARAM,false,"dst_usageTypeField"));
            paramsMap.put(DO_SESSSION_UPDATE_FLAG_PARAM, new ConfigurationParam(DO_SESSSION_UPDATE_FLAG_PARAM,false,""));
            paramsMap.put(DO_DATA_BUCKETS_PARAM, new ConfigurationParam(DO_DATA_BUCKETS_PARAM,false,""));
            paramsMap.put(DO_GEO_LOCATION_PARAM, new ConfigurationParam(DO_GEO_LOCATION_PARAM,true,""));

            paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "VpnEnrichTask"));
        }

        System.out.println(String.format("End configure the GeoLocation task for %s", dataSourceName));

        return paramsMap;
    }

}
