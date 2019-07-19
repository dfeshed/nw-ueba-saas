package com.rsa.netwitness.presidio.automation.utils.adapter;

import com.rsa.netwitness.presidio.automation.common.helpers.End2EndEventsContainer;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters.mongo.EventToMetadataConverterFactory;
import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;
import presidio.data.domain.event.Event;
import presidio.nw.flume.domain.test.NetwitnessStoredData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetwitnessEventsGeneratorBasedMongo extends  NetwitnessEventsGenerator {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(NetwitnessEventsGeneratorBasedMongo.class.getName());

    private NetwitnessEventStore netwitnessEventStore;


    public NetwitnessEventsGeneratorBasedMongo( NetwitnessEventStore netwitnessEventStore){
        this.netwitnessEventStore = netwitnessEventStore;
    }

    @Override
    public void save(List<Map<String, Object>> netwitnessEvents, Schema schema){
        netwitnessEventStore.store(createNetwitnessStoreDataList(netwitnessEvents), schema);
    }

    private List<NetwitnessStoredData> createNetwitnessStoreDataList(List<Map<String, Object>> netwitnessEvents){
        List<NetwitnessStoredData> convertedNetwitnessEvents = new ArrayList<>();
        netwitnessEvents.forEach(netwitnessEvent -> {
            convertedNetwitnessEvents.add(new NetwitnessStoredData(netwitnessEvent));
        });

        return convertedNetwitnessEvents;
    }

    @Override
    public void generateAndSave(Instant startDate, Instant endDate, Map<String, String> config) {
        List<Map<String, Object>> netwitnessEvents = new ArrayList<>();

        LOGGER.info("\n===================== 1. Starting Events Generation ======================");
        LOGGER.info(String.format("Start Date: %s \n End Date: %s \n", startDate, endDate));
        LOGGER.info(String.format("Config %s \n", config.toString()));

        try {
            End2EndEventsContainer end2EndEventsContainer = new End2EndEventsContainer();
            LOGGER.info("\n===================== 2. Events Container Created ======================");
            end2EndEventsContainer.generateEvents(Integer.parseInt(config.getOrDefault(HISTORICAL_DAYS_BACK_CONFIG_KEY, "10")), Integer.parseInt(config.getOrDefault(ANOMALY_DAY_CONFIG_KEY, "10")));


            LOGGER.info("\n===================== 3. save generated Events ======================");
            List<? extends Event> schemaEvents = end2EndEventsContainer.getEvents(Schema.ACTIVE_DIRECTORY.getName(), startDate, endDate);
            netwitnessEvents = new EventToMetadataConverterFactory().getConverter(Schema.ACTIVE_DIRECTORY).convert(config, schemaEvents);
            save(netwitnessEvents, Schema.ACTIVE_DIRECTORY);

            schemaEvents = end2EndEventsContainer.getEvents(Schema.AUTHENTICATION.getName(), startDate, endDate);
            netwitnessEvents = new EventToMetadataConverterFactory().getConverter(Schema.AUTHENTICATION).convert(config, schemaEvents);
            save(netwitnessEvents, Schema.AUTHENTICATION);

            schemaEvents = end2EndEventsContainer.getEvents(Schema.FILE.getName(), startDate, endDate);
            netwitnessEvents = new EventToMetadataConverterFactory().getConverter(Schema.FILE).convert(config, schemaEvents);
            save(netwitnessEvents, Schema.FILE);

            schemaEvents = end2EndEventsContainer.getEvents(Schema.PROCESS.getName(), startDate, endDate);
            netwitnessEvents = new EventToMetadataConverterFactory().getConverter(Schema.PROCESS).convert(config, schemaEvents);
            save(netwitnessEvents, Schema.PROCESS);

            schemaEvents = end2EndEventsContainer.getEvents(Schema.REGISTRY.getName(), startDate, endDate);
            netwitnessEvents = new EventToMetadataConverterFactory().getConverter(Schema.REGISTRY).convert(config, schemaEvents);
            save(netwitnessEvents, Schema.REGISTRY);
        }
        catch (Exception e){
            LOGGER.error("error:" + e.getMessage(), e);
            LOGGER.error(e.getStackTrace().toString());
            System.out.println(e.getStackTrace());
        }
        LOGGER.info(String.format("Number of events generated: %d\n ", netwitnessEvents.size()));
        LOGGER.info("\n=======================================================================");

    }


}
