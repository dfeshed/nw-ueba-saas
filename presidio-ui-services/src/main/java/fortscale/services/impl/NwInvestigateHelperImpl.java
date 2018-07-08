package fortscale.services.impl;

import fortscale.services.NwInvestigateHelper;
import org.apache.commons.codec.net.URLCodec;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;


import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NwInvestigateHelperImpl implements NwInvestigateHelper {


    private String baseLinkDestinationHostname;// ="10.64.153.157";
    private String brokerId; //=UUID I.E. 6;
    private final String PATH_TEMPLATE = "investigation/{0}/events/date/{1}/{2}";
    private final URLCodec urlCodec = new URLCodec();


    public NwInvestigateHelperImpl(String baseLinkDestinationHostname, String brokerId) {
        this.baseLinkDestinationHostname = baseLinkDestinationHostname;
        this.brokerId = brokerId;
    }

    @Override
    public String getLinkToInvestigate(Object value, LocalDateTime startTime, LocalDateTime endTime) {

        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'z'");


        String startTimeStr = startTime.format(DATE_TIME_FORMATTER);
        String endTimeStr = endTime.format(DATE_TIME_FORMATTER);

        String pathWithParameters = MessageFormat.format(PATH_TEMPLATE,brokerId,startTimeStr,endTimeStr);

        String url =  new JerseyUriBuilder()
                .scheme("https")
                .host(baseLinkDestinationHostname)
                .path(pathWithParameters)
                .queryParam("search",value).toString();

        return url;

    }
}
