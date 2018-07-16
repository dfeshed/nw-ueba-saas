package fortscale.services.impl;

import fortscale.services.NwInvestigateHelper;
import fortscale.services.cache.MemoryBasedCache;
import fortscale.utils.configurations.ConfigrationServerClientUtils;
import org.apache.commons.codec.net.URLCodec;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;


import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class NwInvestigateHelperImpl implements NwInvestigateHelper {


    public static final String INVESTIGATE_TIME_STRUCTURE = "yyyy-MM-dd'T'HH:mm:ss'z'";
    public static final String URL_SCHEMA = "https";
    public static final String QUERY_PARAM_NAME = "search";
    public static final String UI_INTEGRATION_ADMIN_SERVER_PARAM_NAME = "uiIntegration.adminServer";
    public static final String UI_INTEGRATION_BROKER_ID_PARAM_NAME = "uiIntegration.brokerId";
    private final String INVESTIGATE_LINK_CONFIGURATION = "CONFIGURATION";

    private final URLCodec urlCodec = new URLCodec();
    private MemoryBasedCache<String, Configurations> configurationsCache;
    private ConfigrationServerClientUtils configrationServerClientUtils;
    private Configurations latestKnownConfiguration = new Configurations();
    private final String PATH_TEMPLATE = "investigation/{0}/events/date/{1}/{2}";

    public NwInvestigateHelperImpl(ConfigrationServerClientUtils configrationServerClientUtils) {
        this.configrationServerClientUtils = configrationServerClientUtils;
        this.configurationsCache = new MemoryBasedCache(10,3600,String.class);
        getConfigurations();
    }

    @Override
    public String getLinkToInvestigate(Object value, LocalDateTime startTime, LocalDateTime endTime) {

        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(INVESTIGATE_TIME_STRUCTURE);
        String startTimeStr = startTime.format(DATE_TIME_FORMATTER);
        String endTimeStr = endTime.format(DATE_TIME_FORMATTER);

        Configurations conf = getConfigurations();

        String pathWithParameters = MessageFormat.format(PATH_TEMPLATE,conf.getBrokerId(),startTimeStr,endTimeStr);

        String url =  new JerseyUriBuilder()
                .scheme(URL_SCHEMA)
                .host(conf.getBaseLinkDestinationHostname())
                .path(pathWithParameters)
                .queryParam(QUERY_PARAM_NAME,value).toString();

        return url;

    }

    private Configurations getConfigurations() {
        Configurations conf = this.configurationsCache.get(INVESTIGATE_LINK_CONFIGURATION);

        if (conf !=null){
            return conf;
        }

        //If not in cache
        Properties properties = null;
        try {
            properties = configrationServerClientUtils.readConfigurationAsProperties("application-presidio",null);
            conf = new Configurations();
            conf.setBaseLinkDestinationHostname(properties.getProperty(UI_INTEGRATION_ADMIN_SERVER_PARAM_NAME));
            conf.setBrokerId(properties.getProperty(UI_INTEGRATION_BROKER_ID_PARAM_NAME));
            this.latestKnownConfiguration = conf;
        } catch (Exception e) {
            // if server is not available, use the latest configuration for another hour
            if (this.latestKnownConfiguration != null){
                conf = this.latestKnownConfiguration;
            } else {
                throw new RuntimeException("Cannot find load uiIntegration configuration");
            }
        }
        this.configurationsCache.put(INVESTIGATE_LINK_CONFIGURATION,conf);
        return conf;
    }

    private static class Configurations{
        private String baseLinkDestinationHostname;// ="10.64.153.157";
        private String brokerId; //=UUID I.E. 6;

        public String getBaseLinkDestinationHostname() {
            return baseLinkDestinationHostname;
        }

        public void setBaseLinkDestinationHostname(String baseLinkDestinationHostname) {
            this.baseLinkDestinationHostname = baseLinkDestinationHostname;
        }

        public String getBrokerId() {
            return brokerId;
        }

        public void setBrokerId(String brokerId) {
            this.brokerId = brokerId;
        }
    }
}
