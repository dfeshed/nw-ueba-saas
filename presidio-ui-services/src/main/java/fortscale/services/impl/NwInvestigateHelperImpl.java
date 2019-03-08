package fortscale.services.impl;

import fortscale.services.NwInvestigateHelper;
import fortscale.services.cache.MemoryBasedCache;
import fortscale.utils.configurations.ConfigrationServerClientUtils;
import org.apache.commons.codec.net.URLCodec;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;


import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
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
    private final String PATH_TEMPLATE_HOST = "/investigate/hosts";
    private final String MACHINE_ID = "machineId";
    private final String PATH_TEMPLATE_PROCESS = "/investigate/process-analysis";
    private final String SID = "sid";
    private final String SERVER_ID = "serverId";
    private final String CHECKSUM = "checksum";
    private final String AID = "aid";

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
                .queryParam("lastCollectionDate", endTime.toEpochSecond(ZoneOffset.UTC)*1000)
                .queryParam("startCollectionDate", startTime.toEpochSecond(ZoneOffset.UTC)*1000)
                .queryParam(QUERY_PARAM_NAME,value).toString().replaceAll("\\+", "%20");

        return url;

    }

    @Override
    public String getLinkToInvestigateHost(Object value, Object callbackId) {

        Configurations conf = getConfigurations();
        if (null == value || null == callbackId) {
            return null;
        }
        String url =  new JerseyUriBuilder()
                .scheme(URL_SCHEMA)
                .host(conf.getBaseLinkDestinationHostname())
                .path(PATH_TEMPLATE_HOST+"/"+value)
                .queryParam(MACHINE_ID, value)
                // This meta represents service id for endpoint hybrid. This is url format but Endpoint link consider only service Id. In future there may be something else might
                // come in this url. Need to update service id accordingly.
                .queryParam(SID, callbackId.toString().replace("nwe://", ""))
                .queryParam("tabName","OVERVIEW")
                .toString();

        return url;
    }

    @Override
    public String getLinkToInvestigateProcess(Object value, Object machineId,Map<String,Object> maps, boolean isSourceProcess) {

        Configurations conf = getConfigurations();

        JerseyUriBuilder url = new JerseyUriBuilder()
                .scheme(URL_SCHEMA)
                .host(conf.getBaseLinkDestinationHostname())
                .path(PATH_TEMPLATE_PROCESS);

        Object agentId = maps.get("agentId");
        Object osType = maps.get("osType");
        Object srcProcessVid = maps.get("srcProcessVid");
        Object dstProcessVid = maps.get("dstProcessVid");
        Object dstChecksumId = maps.get("dstChecksumId");
        Object srcChecksumId = maps.get("srcChecksumId");
        if (null == osType) {
            osType = "windows";
        }
        if (null == agentId || (isSourceProcess ? (null == srcChecksumId || null == srcProcessVid) : (null == dstChecksumId || null == dstProcessVid))) {
            return null;
        }
        return url.queryParam(SID,conf.getBrokerId())
                .queryParam(CHECKSUM, isSourceProcess ? srcChecksumId : dstChecksumId)
                .queryParam(SERVER_ID,conf.getBrokerId())
                .queryParam("aid",agentId)
                .queryParam("hn",machineId)
                .queryParam("pn",value)
                .queryParam("osType", osType)
                .queryParam("vid", isSourceProcess ? srcProcessVid : dstProcessVid)
                .queryParam("et",LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .queryParam("st",LocalDateTime.now().minusDays(7).toEpochSecond(ZoneOffset.UTC))
                .toString();
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
