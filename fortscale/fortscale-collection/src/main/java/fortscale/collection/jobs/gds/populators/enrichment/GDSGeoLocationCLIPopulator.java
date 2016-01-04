package fortscale.collection.jobs.gds.populators.enrichment;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSConfigurationStateImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gils
 * 03/01/2016
 */
public class GDSGeoLocationCLIPopulator implements GDSConfigurationPopulator{

    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSConfigurationStateImpl currentConfigurationState) throws Exception {

        Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        if(paramsMap.containsKey("sourceIpGeoLocationFlag") && paramsMap.get("sourceIpGeoLocationFlag").getParamFlag()) {

            System.out.println(String.format("Going to configure the source ip at GeoLocation task for %s", dataSourceName));
            paramsMap.put("taskName", new ConfigurationParam("taskName", false, "source_VpnEnrichTask"));

            if(paramsMap.containsKey("targetIpGeoLocationFlag") && paramsMap.get("targetIpGeoLocationFlag").getParamFlag())
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-source-ip-geolocated"));
            else
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-ip-geolocated"));

            paramsMap.put("ipField", new ConfigurationParam("ipField",false,"${impala.data.%s.table.field.source_ip}"));
            paramsMap.put("countryField", new ConfigurationParam("ipField",false,"src_country"));
            paramsMap.put("longtitudeField", new ConfigurationParam("ipField",false,"src_longtitudeField"));
            paramsMap.put("latitudeField", new ConfigurationParam("ipField",false,"src_latitudeField"));
            paramsMap.put("countryIsoCodeField", new ConfigurationParam("ipField",false,"src_countryIsoCodeField"));
            paramsMap.put("regionField", new ConfigurationParam("ipField",false,"src_regionField"));
            paramsMap.put("cityField", new ConfigurationParam("ipField",false,"src_cityField"));
            paramsMap.put("ispField", new ConfigurationParam("ipField",false,"src_ispField"));
            paramsMap.put("usageTypeField", new ConfigurationParam("ipField",false,"src_usageTypeField"));
            paramsMap.put("DoesssionUpdateFlag", new ConfigurationParam("ipField",false,""));
            paramsMap.put("doDataBuckets", new ConfigurationParam("ipField",false,""));
            paramsMap.put("doGeoLocation", new ConfigurationParam("ipField",true,""));


            paramsMap.put("lastState", new ConfigurationParam("lastState", false, "VpnEnrichTask"));
        }

        //Target Geo Location
        if(paramsMap.containsKey("targetIpGeoLocationFlag") && paramsMap.get("targetIpGeoLocationFlag").getParamFlag()) {

            System.out.println(String.format("Going to configure the target ip at  GeoLocation task for %s", dataSourceName));
            paramsMap.put("taskName", new ConfigurationParam("taskName", false, "target_VpnEnrichTask"));


            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-ip-geolocated"));

            paramsMap.put("ipField", new ConfigurationParam("ipField",false,"${impala.data.%s.table.field.target}"));
            paramsMap.put("countryField", new ConfigurationParam("countryField",false,"dst_country"));
            paramsMap.put("longtitudeField", new ConfigurationParam("longtitudeField",false,"dst_longtitudeField"));
            paramsMap.put("latitudeField", new ConfigurationParam("latitudeField",false,"dst_latitudeField"));
            paramsMap.put("countryIsoCodeField", new ConfigurationParam("countryIsoCodeField",false,"dst_countryIsoCodeField"));
            paramsMap.put("regionField", new ConfigurationParam("regionField",false,"dst_regionField"));
            paramsMap.put("cityField", new ConfigurationParam("cityField",false,"dst_cityField"));
            paramsMap.put("ispField", new ConfigurationParam("ispField",false,"dst_ispField"));
            paramsMap.put("usageTypeField", new ConfigurationParam("usageTypeField",false,"dst_usageTypeField"));
            paramsMap.put("DoesssionUpdateFlag", new ConfigurationParam("DoesssionUpdateFlag",false,""));
            paramsMap.put("doDataBuckets", new ConfigurationParam("doDataBuckets",false,""));
            paramsMap.put("doGeoLocation", new ConfigurationParam("doGeoLocation",true,""));
        }
        System.out.println(String.format("End configure the GeoLocation task for %s", dataSourceName));

        return paramsMap;
    }

}
