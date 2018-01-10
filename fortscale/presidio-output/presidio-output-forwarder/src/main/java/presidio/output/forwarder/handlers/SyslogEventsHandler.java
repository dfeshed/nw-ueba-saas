package presidio.output.forwarder.handlers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.users.User;
import presidio.output.forwarder.services.SyslogService;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SyslogEventsHandler implements  EventsHandler{

    private SyslogService syslogServices;
    private ObjectMapper mapper;
    private SyslogEndpoints endpoints;

    public SyslogEventsHandler(SyslogService syslogServices, SyslogEndpoints endpoints) {
        this.syslogServices = syslogServices;
        this.endpoints = endpoints;
        mapper = configureJackson();
    }


    @Override
    public void onUserStartStreaming(Instant start, Instant end) {

        SyslogEndpoints.SyslogEndpoint syslogEndpoint = endpoints.getEndPoint("users");

        if (syslogEndpoint != null) {
            try {
                StreamingEvent streamingEvent = new StreamingEvent(Date.from(start), Date.from(end));
                syslogServices.send("STREAMING-USERS-START", UUID.randomUUID().toString(), mapper.writeValueAsString(streamingEvent),syslogEndpoint.getHost(), syslogEndpoint.getPort() );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUserEndStreaming(Instant start, Instant end) {
        SyslogEndpoints.SyslogEndpoint syslogEndpoint = endpoints.getEndPoint("users");

        if (syslogEndpoint != null) {
            try {
                StreamingEvent streamingEvent = new StreamingEvent(Date.from(start), Date.from(end));
                syslogServices.send("STREAMING-USERS-END", UUID.randomUUID().toString(), mapper.writeValueAsString(streamingEvent), syslogEndpoint.getHost(), syslogEndpoint.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAlertStartStreaming(Instant start, Instant end) {
        SyslogEndpoints.SyslogEndpoint syslogEndpoint = endpoints.getEndPoint("alerts");

        if (syslogEndpoint != null) {
            try {
                StreamingEvent streamingEvent = new StreamingEvent(Date.from(start), Date.from(end));
                syslogServices.send("STREAMING-ALERTS-START", UUID.randomUUID().toString(), mapper.writeValueAsString(streamingEvent), syslogEndpoint.getHost(), syslogEndpoint.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAlertEndStreaming(Instant start, Instant end) {
        SyslogEndpoints.SyslogEndpoint syslogEndpoint = endpoints.getEndPoint("alerts");

        if (syslogEndpoint != null) {
            try {
                StreamingEvent streamingEvent = new StreamingEvent(Date.from(start), Date.from(end));
                syslogServices.send("STREAMING-ALERTS-END", UUID.randomUUID().toString(), mapper.writeValueAsString(streamingEvent), syslogEndpoint.getHost(), syslogEndpoint.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUserChanged(User user) {
        SyslogEndpoints.SyslogEndpoint syslogEndpoint = endpoints.getEndPoint("user");

        if (syslogEndpoint != null) {
            try {
                syslogServices.send("USER", user.getId(), mapper.writeValueAsString(user), syslogEndpoint.getHost(), syslogEndpoint.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAlertChanged(Alert alert) {
        SyslogEndpoints.SyslogEndpoint syslogEndpoint = endpoints.getEndPoint("alerts");

        if (syslogEndpoint != null) {
            try {
                syslogServices.send("ALERT", alert.getId(), mapper.writeValueAsString(alert), syslogEndpoint.getHost(), syslogEndpoint.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onIndicatorChanged(Indicator indicator) {
        SyslogEndpoints.SyslogEndpoint syslogEndpoint = endpoints.getEndPoint("alerts");

        if (syslogEndpoint != null) {
            try {
                syslogServices.send("INDICATOR", indicator.getId(), mapper.writeValueAsString(indicator), syslogEndpoint.getHost(), syslogEndpoint.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @JsonIgnoreProperties({"smartId","indexedUserName","lastUpdatedBy","updatedBy","feedback","preferredClassification","userTags","createdDate","updatedDate"})
    class AlertJsonMixin extends Alert {
    }

    @JsonIgnoreProperties({"indexedUserName","userDisplayName","tags","startDate","endDate","indicators","alertClassifications","updatedBy","createdDate","updatedDate"})
    class UserJsonMixin extends User {
    }

    @JsonIgnoreProperties({"historicalData","updatedBy","lastUpdatedBy","createdDate","endDate","updatedDate"})
    class IndicatorJsonMixin extends Indicator {

        @JsonProperty
        public List<IndicatorEvent> getEvents() {
            return super.getEvents();
        }
    }

    class CustomDateSerializer extends DateSerializer {
        @Override
        protected long _timestamp(Date value) {
            return TimeUnit.SECONDS.convert(super._timestamp(value), TimeUnit.MILLISECONDS);
        }
    }

    class CustomEventSerializer extends JsonSerializer<IndicatorEvent> {

        @Override
        public void serialize(IndicatorEvent value, JsonGenerator generator,
                              SerializerProvider provider) throws IOException, JsonProcessingException {
            generator.writeString(value.getFeatures().get("eventId").toString());
        }
    }


    private ObjectMapper configureJackson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(Alert.class, AlertJsonMixin.class);
        mapper.addMixIn(User.class, UserJsonMixin.class);
        mapper.addMixIn(Indicator.class, IndicatorJsonMixin.class);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Date.class, new CustomDateSerializer());
        module.addSerializer(IndicatorEvent.class, new CustomEventSerializer());
        mapper.registerModule(module);
        return  mapper;
    }

}
