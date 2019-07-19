package com.rsa.netwitness.presidio.automation.utils.adapter;

import com.rsa.netwitness.presidio.automation.common.helpers.End2EndEventsContainer;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters.PresidioEventConverter;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters.PresidioToNetwitnessEventConverterImpl;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.producers.DailyCefFilesPrinterImpl;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.producers.NetwitnessEventsProducer;
import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;
import presidio.data.domain.event.Event;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NetwitnessEventsGeneratorBasedCefFiles extends NetwitnessEventsGenerator {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(NetwitnessEventsGeneratorBasedCefFiles.class.getName());

    End2EndEventsContainer end2EndEventsContainer = new End2EndEventsContainer();

    public List<Event> generate(Instant startDate, Instant endDate, Map<String, String> config){

        LOGGER.info("Events Container Created");
        end2EndEventsContainer.generateEvents(Integer.parseInt(config.getOrDefault(HISTORICAL_DAYS_BACK_CONFIG_KEY, "10")),
                Integer.parseInt(config.getOrDefault(ANOMALY_DAY_CONFIG_KEY, "10")));
        return end2EndEventsContainer.getAllEvents(startDate,endDate);
    }

    public Stream<?> convert(List<Event> netwitnessEvents){
        PresidioEventConverter mapper = new PresidioToNetwitnessEventConverterImpl();
        return netwitnessEvents.stream().map(mapper::convert);
    }

    public Map<Schema, Long> dispatch(Stream<?> convertedEvents){
        NetwitnessEventsProducer printer  = new DailyCefFilesPrinterImpl();
        return printer.send(convertedEvents.map(e -> (NetwitnessEvent) e).collect(Collectors.toList()));
    }


    @Override
    public void generateAndSave(Instant startDate, Instant endDate, Map<String, String> config) {
        List<Event> events = generate(startDate, endDate, config);
        Stream<?> converted = convert(events);
        eventGeneratorResult = dispatch(converted);
    }

    @Override
    public void save(List<Map<String, Object>> netwitnessEvents, Schema schema) {

    }

}
