package com.rsa.netwitness.presidio.automation.utils.ade.inserter;



import presidio.data.domain.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YaronDL on 7/10/2017.
 */
public class AdeInserterFactory {
    public Map<Class<? extends Event>, AdeInserter> eventTypeToAdeInserter = new HashMap<>();

    public AdeInserterFactory(List<AdeInserter> inserterList){
        for(AdeInserter inserter: inserterList){
            eventTypeToAdeInserter.put(inserter.getEventClass(), inserter);
        }
    }


    public AdeInserter getAdeInserter(Class<? extends Event> eventClass){
        return eventTypeToAdeInserter.get(eventClass);
    }
}
