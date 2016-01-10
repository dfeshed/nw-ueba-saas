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

    private static final String GDS_CONFIG_ENTRY = "gds.config.entry.";
    private static final String SOURCE_IP_CONFIG_ENTRY = "source.";
    private static final String TARGET_IP_CONFIG_ENTRY = "target.";

    @Override
    public Map<String, Map<String, ConfigurationParam>> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {

        Map<String, Map<String, ConfigurationParam>> configurationsMap = new HashMap<>();
        HashMap<String, ConfigurationParam> sourceGeoLocationIpParamsMap = new HashMap<>();

        configurationsMap.put(GDS_CONFIG_ENTRY + SOURCE_IP_CONFIG_ENTRY, sourceGeoLocationIpParamsMap);

        String dataSourceName = currentConfigurationState.getDataSourceName();

        if(currentConfigurationState.getStreamingTopologyDefinitionState().isSourceIpGeoLocationRequired()) {

            System.out.println(String.format("Going to configure the source ip at GeoLocation task for %s", dataSourceName));
            sourceGeoLocationIpParamsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "source_VpnEnrichTask"));

            if(currentConfigurationState.getStreamingTopologyDefinitionState().isTargetIpGeoLocationRequired()) {
                sourceGeoLocationIpParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-source-ip-geolocated"));
            }
            else {
                sourceGeoLocationIpParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-ip-geolocated"));
            }

            sourceGeoLocationIpParamsMap.put(IP_FIELD_PARAM, new ConfigurationParam(IP_FIELD_PARAM,false,"${impala.data.%s.table.field.source_ip}"));
            sourceGeoLocationIpParamsMap.put(COUNTRY_FIELD_PARAM, new ConfigurationParam(COUNTRY_FIELD_PARAM,false,"src_country"));
            sourceGeoLocationIpParamsMap.put(LONGITUDE_FIELD_PARAM, new ConfigurationParam(LONGITUDE_FIELD_PARAM,false,"src_longtitudeField"));
            sourceGeoLocationIpParamsMap.put(LATITUDE_FIELD_PARAM, new ConfigurationParam(LATITUDE_FIELD_PARAM,false,"src_latitudeField"));
            sourceGeoLocationIpParamsMap.put(COUNTRY_ISO_CODE_FIELD_PARAM, new ConfigurationParam(COUNTRY_ISO_CODE_FIELD_PARAM,false,"src_countryIsoCodeField"));
            sourceGeoLocationIpParamsMap.put(REGION_FIELD_PARAM, new ConfigurationParam(REGION_FIELD_PARAM,false,"src_regionField"));
            sourceGeoLocationIpParamsMap.put(CITY_FIELD_PARAM, new ConfigurationParam(CITY_FIELD_PARAM,false,"src_cityField"));
            sourceGeoLocationIpParamsMap.put(ISP_FIELD_PARAM, new ConfigurationParam(ISP_FIELD_PARAM,false,"src_ispField"));
            sourceGeoLocationIpParamsMap.put(USAGE_TYPE_FIELD_PARAM, new ConfigurationParam(USAGE_TYPE_FIELD_PARAM,false,"src_usageTypeField"));
            sourceGeoLocationIpParamsMap.put(DO_SESSSION_UPDATE_FLAG_PARAM, new ConfigurationParam(DO_SESSSION_UPDATE_FLAG_PARAM,false,""));
            sourceGeoLocationIpParamsMap.put(DO_DATA_BUCKETS_PARAM, new ConfigurationParam(DO_DATA_BUCKETS_PARAM,false,""));
            sourceGeoLocationIpParamsMap.put(DO_GEO_LOCATION_PARAM, new ConfigurationParam(DO_GEO_LOCATION_PARAM,true,""));

            sourceGeoLocationIpParamsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "VpnEnrichTask"));
        }

        //Target Geo Location
        if(currentConfigurationState.getStreamingTopologyDefinitionState().isTargetIpGeoLocationRequired()) {
            HashMap<String, ConfigurationParam> targetGeoLocationIpParamsMap = new HashMap<>();

            configurationsMap.put(GDS_CONFIG_ENTRY + TARGET_IP_CONFIG_ENTRY, targetGeoLocationIpParamsMap);


            System.out.println(String.format("Going to configure the target ip at  GeoLocation task for %s", dataSourceName));
            targetGeoLocationIpParamsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "target_VpnEnrichTask"));

            targetGeoLocationIpParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-ip-geolocated"));

            targetGeoLocationIpParamsMap.put(IP_FIELD_PARAM, new ConfigurationParam(IP_FIELD_PARAM,false,"${impala.data.%s.table.field.target}"));
            targetGeoLocationIpParamsMap.put(COUNTRY_FIELD_PARAM, new ConfigurationParam(COUNTRY_FIELD_PARAM,false,"dst_country"));
            targetGeoLocationIpParamsMap.put(LONGITUDE_FIELD_PARAM, new ConfigurationParam(LONGITUDE_FIELD_PARAM,false,"dst_longtitudeField"));
            targetGeoLocationIpParamsMap.put(LATITUDE_FIELD_PARAM, new ConfigurationParam(LATITUDE_FIELD_PARAM,false,"dst_latitudeField"));
            targetGeoLocationIpParamsMap.put(COUNTRY_ISO_CODE_FIELD_PARAM, new ConfigurationParam(COUNTRY_ISO_CODE_FIELD_PARAM,false,"dst_countryIsoCodeField"));
            targetGeoLocationIpParamsMap.put(REGION_FIELD_PARAM, new ConfigurationParam(REGION_FIELD_PARAM,false,"dst_regionField"));
            targetGeoLocationIpParamsMap.put(CITY_FIELD_PARAM, new ConfigurationParam(CITY_FIELD_PARAM,false,"dst_cityField"));
            targetGeoLocationIpParamsMap.put(ISP_FIELD_PARAM, new ConfigurationParam(ISP_FIELD_PARAM,false,"dst_ispField"));
            targetGeoLocationIpParamsMap.put(USAGE_TYPE_FIELD_PARAM, new ConfigurationParam(USAGE_TYPE_FIELD_PARAM,false,"dst_usageTypeField"));
            targetGeoLocationIpParamsMap.put(DO_SESSSION_UPDATE_FLAG_PARAM, new ConfigurationParam(DO_SESSSION_UPDATE_FLAG_PARAM,false,""));
            targetGeoLocationIpParamsMap.put(DO_DATA_BUCKETS_PARAM, new ConfigurationParam(DO_DATA_BUCKETS_PARAM,false,""));
            targetGeoLocationIpParamsMap.put(DO_GEO_LOCATION_PARAM, new ConfigurationParam(DO_GEO_LOCATION_PARAM,true,""));

            targetGeoLocationIpParamsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "VpnEnrichTask"));
        }

        System.out.println(String.format("End configure the GeoLocation task for %s", dataSourceName));

        return configurationsMap;
    }

}
